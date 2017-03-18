package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import main.mapdata.*;
import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    private static final int MAX_SEARCH_RESULTS = Integer.MAX_VALUE;//10;

    private final MapDataParser dataParser;
    private final View view;
    private final MapData.Factory mapDataFactory;
    private final Drawer.Factory drawerFactory;

    private Drawer drawer;
    private MapData mapData;
    /**
     * Should never be null. Stuff to highlight
     */
    private HighlightData highlightData = new HighlightData(null, null);

    private Map<RoadInfo, Collection<RoadSegment>> searchResults;

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

        drawer.draw(graphics, highlightData);
    }

    /**
     * Called after the mouse has been clicked on the graphics pane and then
     * released.
     */
    @Override
    protected void onClick(MouseEvent e) {
        if (mapData == null) return;

        Location clickLocation = view.getLocationFromPoint(
                new Point(e.getX(), e.getY())
        );

        double maxDistance = view.getClickRadius();
        // 'Node' means 'intersection' here
        Node highlightedNode = mapData.findNodeNearLocation(clickLocation, maxDistance);
        if (highlightedNode == null) {
            highlightData = new HighlightData(null, null);
            outputLine("There are no nodes close to where you clicked");
            return;
        }

        outputLine("Found an intersection: " + highlightedNode.latLong);

        // Find roadSegments (for highlighting) and roadInfos (for printing)
        Collection<RoadSegment> roadSegments =
                mapData.findRoadSegmentsForNode(highlightedNode);
        Collection<RoadInfo> roadInfos = roadSegments.stream()
                .map(segment -> mapData.findRoadInfoForSegment(segment))
                .collect(Collectors.toList());

        // Print road label/city (and avoid printing duplicate names)
        roadInfos.stream()
                .map(roadInfo -> new RoadInfoByName(roadInfo.label, roadInfo.city))
                .distinct()
                .forEach(roadInfo -> outputLine("- Connected to: " + roadInfo));

        highlightData = new HighlightData(highlightedNode, roadSegments);
    }

    @Override
    protected void onSearch() {
        String searchTerm = getSearchBox().getText();
        if (searchTerm.isEmpty()) {
            highlightData = new HighlightData(null, null);
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
        if (searchResults.size() == MAX_SEARCH_RESULTS) {
            // Is a bit misleading when size actually equals MAX_SEARCH_RESULTS
            outputLine("- ... too many results");
        }

        // Flatten values in searchResults into a list
        Collection<RoadSegment> highlightedRoadSegments = searchResults.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        highlightData = new HighlightData(null, highlightedRoadSegments);
    }

    @Override
    protected void onMove(Move m) {
        view.applyMove(m);
    }

    /**
     * @param polygons a File for polygon-shapes.mp (map be null)
     */
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        try {
            outputLine("Loading data");
            long loadStartTime = System.currentTimeMillis();

            Scanner nodesScanner = new Scanner(nodes);
            Scanner segmentsScanner = new Scanner(segments);
            Scanner roadInfoScanner = new Scanner(roads);

            AtomicReference<MapData> mapDataRef = new AtomicReference<>();

            mapDataRef.set(mapDataFactory.create(
                    () -> onFinishLoad(mapDataRef.get(), loadStartTime),
                    () -> dataParser.parseNodes(nodesScanner),
                    () -> dataParser.parseRoadSegments(segmentsScanner),
                    () -> dataParser.parseRoadInfo(roadInfoScanner)
            ));
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private void onFinishLoad(MapData mapData, long loadStartTime) {
        this.mapData = mapData;
        this.highlightData = new HighlightData(null, null);
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
}
