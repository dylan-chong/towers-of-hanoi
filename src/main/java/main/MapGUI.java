package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import main.mapdata.*;
import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    private static final int MAX_SEARCH_RESULTS = Integer.MAX_VALUE;

    private final MapDataParser dataParser;
    private final View view;
    private final MapData.Factory mapDataFactory;
    private final Drawer.Factory drawerFactory;

    private Drawer drawer;
    private MapData mapData;
    /**
     * Should never be null. Stuff to highlight
     */
    private HighlightData highlightData = new HighlightData(null, null, null, null);

    private Map<RoadInfo, Collection<RoadSegment>> searchResults;
    private MouseAdapter drawingMouseListener;

    private UserState state = UserState.NORMAL;
    private Node routeStartNode;
    private Node routeEndNode;

    @Inject
    public MapGUI(MapDataParser dataParser,
                  View view,
                  MapData.Factory mapDataFactory,
                  Drawer.Factory drawerFactory) {
        this.dataParser = dataParser;
        this.view = view;
        this.mapDataFactory = mapDataFactory;
        this.drawerFactory = drawerFactory;
    }

    @Override
    protected void redraw(Graphics graphics) {
        if (drawer == null) return;

        drawer.draw(graphics, highlightData, getDrawingAreaDimension());
    }

    /**
     * Called after the mouse has been clicked on the graphics pane and then
     * released.
     */
    protected void onClick(MouseEvent e) {
        if (mapData == null) return;

        Location clickLocation = view.getLocationFromPoint(
                new Point(e.getX(), e.getY())
        );
        ClickSelection selection = selectClosestNodeTo(clickLocation);
        // Show information and set highlightData
        applyClickSelection(selection);

        if (state == UserState.NORMAL) return;
        if (selection.selectedNode == null) return;

        if (state == UserState.ENTER_ROUTE_START_NODE) {
            routeStartNode = selection.selectedNode;
            state = UserState.ENTER_ROUTE_LAST_NODE;
            highlightData = highlightData.getNewWithRoute(routeStartNode, null);
            outputLine("Now select the node for the end of your route");

        } else if (state == UserState.ENTER_ROUTE_LAST_NODE) {
            routeEndNode = selection.selectedNode;
            outputLine("Finding route from " + routeStartNode.id +
                    " to " + routeEndNode.id + " ...");
            highlightData = highlightData.getNewWithRoute(
                    routeStartNode, routeEndNode
            );
            redraw(); // Show the selection before searching

            // TODO NEXT find route
            outputLine("*** TODO FIND ROUTE ***");

            state = UserState.NORMAL;
        }
    }

    @Override
    protected void onSearch() {
        String searchTerm = getSearchBox().getText();
        if (searchTerm.isEmpty()) {
            highlightData = new HighlightData(null, null, null, null);
            return;
        }

        outputLine("Search results for '" + searchTerm + "':");

        searchResults = mapData.findRoadSegmentsByString(
                searchTerm,
                MAX_SEARCH_RESULTS
        );
        if (searchResults.isEmpty()) {
            outputLine("No matches");
            return;
        }
        searchResults.keySet()
                .stream()
                .map(roadInfo -> new RoadInfoByName(roadInfo.label, roadInfo.city))
                .distinct()
                .forEach(infoByName -> {
                    outputLine("- Found: " + infoByName);
                });

        // Flatten values in searchResults into a list
        Collection<RoadSegment> highlightedRoadSegments = searchResults.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        highlightData = new HighlightData(
                null, highlightedRoadSegments, null, null
        );
    }

    @Override
    protected void onMove(Move m) {
        view.applyMove(m, getDrawingAreaDimension());
    }

    /**
     * @param polygons a File for polygon-shapes.mp (map be null)
     */
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        try {
            outputLine("Loading data");
            long loadStartTime = System.currentTimeMillis();

            BufferedReader nodesReader = new BufferedReader(new FileReader(nodes));
            BufferedReader segmentsReader = new BufferedReader(new FileReader(segments));
            Scanner roadInfoScanner = new Scanner(roads);
            BufferedReader polysReader = (polygons == null) ? null :
                new BufferedReader(new FileReader(polygons));


            AtomicReference<MapData> mapDataRef = new AtomicReference<>();

            mapDataRef.set(mapDataFactory.create(
                    () -> onFinishLoad(mapDataRef.get(), loadStartTime),
                    () -> dataParser.parseNodes(nodesReader),
                    () -> dataParser.parseRoadSegments(segmentsReader),
                    () -> dataParser.parseRoadInfo(roadInfoScanner),
                    () -> {
                        if (polysReader != null) {
                            return dataParser.parsePolygons(polysReader);
                        } else {
                            return Collections.emptyList();
                        }
                    }
            ));
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    protected void onEnterDirectionsClick() {
        state = UserState.ENTER_ROUTE_START_NODE;
        outputLine("Click on an intersection to define the start");
    }

    private void onFinishLoad(MapData mapData, long loadStartTime) {
        this.mapData = mapData;
        this.highlightData = new HighlightData(null, null, null, null);
        this.drawer = drawerFactory.create(this.mapData, view);
        this.redraw();

        long duration = System.currentTimeMillis() - loadStartTime;
        outputLine("Loading finished (took " + duration + " ms)");
    }

    /**
     * @param info Text to put in the text area
     */
    private void outputLine(String info) {
        JTextArea t = getTextOutputArea();
        t.append(info);
        t.append("\n");
    }

    /**
     * Display information, and store the node and segments to be highlighted
     * on draw
     */
    private void applyClickSelection(ClickSelection selection) {
        highlightData = new HighlightData(
                selection.selectedNode, // these fields may be null
                selection.connectedSegments,
                null, null
        );
        if (selection.selectedNode == null) {
            outputLine("There are no nodes there");
            return;
        }
        selection.segmentInfosByName.forEach(roadInfoByName ->
                outputLine("- Connected to: " + roadInfoByName)
        );
    }

    private ClickSelection selectClosestNodeTo(Location location) {
        // 'Node' means 'intersection' here
        Node selectedNode = mapData.findNodeNearLocation(location);
        if (selectedNode == null) {
            return new ClickSelection(null, null, null, null);
        }

        outputLine("Found an intersection: " + selectedNode.latLong);

        // Find roadSegments (for highlighting) and roadInfos (for printing)
        Collection<RoadSegment> roadSegments =
                mapData.findRoadSegmentsForNode(selectedNode);
        Collection<RoadInfo> roadInfos = roadSegments.stream()
                .map(segment -> mapData.findRoadInfoForSegment(segment))
                .collect(Collectors.toList());

        // Print road label/city (and avoid printing duplicate names)
        Collection<RoadInfoByName> roadInfosByName = roadInfos.stream()
                .map(roadInfo -> new RoadInfoByName(roadInfo.label, roadInfo.city))
                .distinct()
                .collect(Collectors.toList());

        return new ClickSelection(
                selectedNode,
                roadSegments,
                roadInfos,
                roadInfosByName
        );
    }

    @Override
    protected MouseAdapter getMouseListener() {
        if (drawingMouseListener == null) {
            drawingMouseListener = new DrawingMouseListener();
        }
        return drawingMouseListener;
    }

    private class DrawingMouseListener extends MouseAdapter {
        private MouseEvent mouseDownEvent, lastDragEvent;

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDownEvent = e;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDownEvent = null;
            lastDragEvent = null;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            onClick(e);
            redraw();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (view == null) return;
            if (mouseDownEvent == null) throw new AssertionError();

            MouseEvent dragStart = lastDragEvent;
            if (lastDragEvent == null) {
                dragStart = mouseDownEvent;
            }

            int dx = e.getX() - dragStart.getX();
            int dy = e.getY() - dragStart.getY();
            view.applyDrag(dx, dy);

            lastDragEvent = e;

            redraw();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            Move move = Move.ZOOM_OUT;
            int zooms = e.getUnitsToScroll();
            if (zooms < 0) move = Move.ZOOM_IN;

            for (int i = 0; i < Math.abs(zooms); i++) {
                view.applyMove(move, getDrawingAreaDimension());
            }
            redraw();
        }
    }

    /**
     * Stores information that is found when finding the node that was
     * clicked
     */
    private static class ClickSelection {
        public final Node selectedNode;
        public final Collection<RoadSegment> connectedSegments;
        public final Collection<RoadInfo> segmentInfos;
        public final Collection<RoadInfoByName> segmentInfosByName;

        public ClickSelection(Node selectedNode,
                              Collection<RoadSegment> connectedSegments,
                              Collection<RoadInfo> segmentInfos,
                              Collection<RoadInfoByName> segmentInfosByName) {
            this.selectedNode = selectedNode;
            this.connectedSegments = connectedSegments;
            this.segmentInfos = segmentInfos;
            this.segmentInfosByName = segmentInfosByName;
        }
    }

}
