package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import main.mapdata.*;
import main.mapdata.model.MapDataLoader;
import main.mapdata.model.MapDataModel;
import main.structures.Route;
import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    private static final int MAX_SEARCH_RESULTS = Integer.MAX_VALUE;

    private final View view;
    private final Drawer.Factory drawerFactory;
    private final MapDataLoader mapDataLoader;

    private Drawer drawer;
    private MapDataModel mapModel;
    /**
     * Should never be null. Stuff to highlight
     */
    private HighlightData highlightData;

    private Map<RoadInfo, Collection<RoadSegment>> searchResults;
    private MouseAdapter drawingMouseListener;

    private MapGUIState state = MapGUIState.NORMAL;
    private Node routeStartNode;
    private Node routeEndNode;

    @Inject
    public MapGUI(View view,
                  Drawer.Factory drawerFactory,
                  MapDataLoader mapDataLoader) {
        this.view = view;
        this.drawerFactory = drawerFactory;
        this.mapDataLoader = mapDataLoader;
    }

    @Override
    protected void redraw(Graphics graphics) {
        if (drawer == null) return;

        drawer.draw(graphics, highlightData, getDrawingAreaDimension());
    }

    @Override
    protected void onSearch() {
        String searchTerm = getSearchBox().getText();
        if (searchTerm.isEmpty()) {
            clearHighlightingData();
            return;
        }

        outputLine("Search results for '" + searchTerm + "':");

        searchResults = mapModel.findRoadSegmentsByString(
                searchTerm,
                MAX_SEARCH_RESULTS
        );
        if (searchResults.isEmpty()) {
            outputLine("No matches");
            return;
        }
        RoadInfo.getDistinctByName(searchResults.keySet())
                .forEach(infoByName -> outputLine("- Found: " + infoByName));

        // Flatten values in searchResults into a list
        Collection<RoadSegment> highlightedRoadSegments = searchResults.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        highlightData = new HighlightData(
                null, highlightedRoadSegments, null
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
        outputLine("Loading data");

        MapDataLoader.OnFinishLoad onFinishLoad = (mapModel, loadDuration) -> {
            this.mapModel = mapModel;
            clearHighlightingData();
            this.drawer = drawerFactory.create(this.mapModel, view);
            this.redraw();

            outputLine("Loading finished (took " + loadDuration + " ms)");
        };
        mapDataLoader.load(nodes, roads, segments, polygons, onFinishLoad);
    }

    @Override
    protected void onEnterDirectionsClick() {
        state = MapGUIState.ENTER_ROUTE_START_NODE;
        outputLine("Click on an intersection to define the start");

        // Clear route
        clearHighlightingData();
        redraw();
    }

    @Override
    protected MouseAdapter getMouseListener() {
        if (drawingMouseListener == null) {
            drawingMouseListener = new DrawingMouseListener();
        }
        return drawingMouseListener;
    }

    /**
     * Called after the mouse has been clicked on the graphics pane and then
     * released.
     */
    private void onClick(MouseEvent e) {
        if (mapModel == null) return;

        Location clickLocation = view.getLocationFromPoint(
                new Point(e.getX(), e.getY())
        );
        ClickSelection selection = selectClosestNodeTo(clickLocation);
        // Show information and set highlightData
        applyClickSelection(selection, state);

        if (state == MapGUIState.NORMAL) return;
        if (selection.selectedNode == null) return;

        if (state == MapGUIState.ENTER_ROUTE_START_NODE) {
            setRouteStartNode(selection.selectedNode);
        } else if (state == MapGUIState.ENTER_ROUTE_LAST_NODE) {
            setRouteEndNode(selection.selectedNode);
            state = MapGUIState.NORMAL;
        }
    }

    private void setRouteStartNode(Node routeStartNode) {
        this.routeStartNode = routeStartNode;
        state = MapGUIState.ENTER_ROUTE_LAST_NODE;
        // Hack to show just one circle (for the route start)
        highlightData = highlightData.getNewWithRoute(new Route(
                Collections.singletonList(routeStartNode),
                Collections.emptyList()
        ));
        outputLine("Now select the node for the end of your route");
    }

    private void setRouteEndNode(Node routeEndNode) {
        this.routeEndNode = routeEndNode;

        outputLine("Finding route from " + routeStartNode.id +
                " to " + routeEndNode.id + " ...");
        highlightData = highlightData.getNewWithRoute(new Route(
                // Fake route
                Arrays.asList(routeStartNode, routeEndNode),
                // Don't draw segments because we don't know the route yet
                Collections.emptyList()
        ));
        redraw(); // Show the selection before searching for better UX

        Route route = mapModel.findRouteBetween(routeStartNode, routeEndNode);
        showRoute(route);
        // redraw will be called automatically
    }

    private void showRoute(Route route) {
        highlightData = highlightData.getNewWithRoute(route);

        if (route == null) {
            outputLine("No route found");
            return;
        }

        outputLine("Route found");
        outputLine("Instructions:");

        RoadInfo previousRoadInfo = null;
        for (int i = 0; i < route.segments.size(); i++) {
            Node node = route.nodes.get(i);
            RoadSegment segment = route.segments.get(i);
            RoadInfo roadInfo = mapModel.findRoadInfoForSegment(segment);

            if (previousRoadInfo != null) {
                List<RoadInfo> roadInfos = Arrays.asList(
                        roadInfo, previousRoadInfo
                );
                if (RoadInfo.getDistinctByName(roadInfos).size() != 2) {
                    // The previous had the same name, so don't bother printing
                    // the name again
                    previousRoadInfo = roadInfo;
                    continue;
                }
            }

            String distance = String.format("%.2f", segment.length);
            outputLine("\t- Go from intersection " + node + "\n" +
                    "\t  on " + roadInfo + " for " + distance + " km");
            previousRoadInfo = roadInfo;
        }

        // There is one more node than segment
        outputLine("\t- Then end your journey at " + routeEndNode);
    }

    private void clearHighlightingData() {
        highlightData = new HighlightData(null, null, null);
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
    private void applyClickSelection(ClickSelection selection, MapGUIState state) {
        highlightData = new HighlightData(
                selection.selectedNode, // these fields may be null
                selection.connectedSegments,
                null
        );
        if (selection.selectedNode == null) {
            outputLine("There are no nodes there");
            return;
        }

        if (state != MapGUIState.NORMAL) return;

        // Show selected node details
        outputLine("Found an intersection: " + selection.selectedNode);
        selection.segmentInfosByName.forEach(
                roadInfoByName -> outputLine("- Connected to: " + roadInfoByName)
        );
    }

    private ClickSelection selectClosestNodeTo(Location location) {
        // 'Node' means 'intersection' here
        Node selectedNode = mapModel.findNodeNearLocation(location);
        if (selectedNode == null) {
            return new ClickSelection(null, null, null, null);
        }

        // Find roadSegments (for highlighting) and roadInfos (for printing)
        Collection<RoadSegment> roadSegments =
                mapModel.findRoadSegmentsForNode(selectedNode);
        Collection<RoadInfo> roadInfos = roadSegments.stream()
                .map(segment -> mapModel.findRoadInfoForSegment(segment))
                .collect(Collectors.toList());

        // Print road label/city (and avoid printing duplicate names)
        Collection<RoadInfoByName> roadInfosByName =
                RoadInfo.getDistinctByName(roadInfos);

        return new ClickSelection(
                selectedNode,
                roadSegments,
                roadInfos,
                roadInfosByName
        );
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

            // Allow zooming at the mouse position
            Dimension fakeDimension = new Dimension(e.getX() * 2, e.getY() * 2);

            for (int i = 0; i < Math.abs(zooms); i++) {
                view.applyMove(move, fakeDimension);
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
