package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import main.async.AsyncTask;
import main.async.AsyncTaskQueues;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    private static final int MAX_SEARCH_RESULTS = 10;

    private final MapDataParser dataParser;
    private final View view;
    private final MapData.Factory mapDataFactory;
    private final Drawer.Factory drawerFactory;
    private final AsyncTaskQueues asyncTaskQueues;

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
                  Drawer.Factory drawerFactory,
                  AsyncTaskQueues asyncTaskQueues) {
        this.dataParser = dataParser;
        this.view = view;
        this.mapDataFactory = mapDataFactory;
        this.drawerFactory = drawerFactory;
        this.asyncTaskQueues = asyncTaskQueues;
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

        searchResults =
                mapData.findRoadSegmentsByString(searchTerm, MAX_SEARCH_RESULTS);
        searchResults.entrySet()
                .forEach(entry -> {
                    RoadInfo info = entry.getKey();
                    outputLine("- Found: " + info);
                });

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

            // Use atomic to allow assigning the values from within lambdas
            AtomicReference<Collection<Node>> parsedNodes
                    = new AtomicReference<>();
            AtomicReference<Collection<RoadSegment>> parsedRoadSegments
                    = new AtomicReference<>();

            // Called when any task has completed
            Runnable onTaskCompletion = () -> {
                if (parsedNodes.get() == null) return;
                if (parsedRoadSegments.get() == null) return;

                // All tasks have been completed
                onParse(parsedNodes.get(),
                        parsedRoadSegments.get(),
                        () -> dataParser.parseRoadInfo(roadInfoScanner));

                long duration = System.currentTimeMillis() - loadStartTime;
                outputLine("Loading finished (took " + duration + "ms)");

                redraw();
            };

            // Queue up tasks to work in parallel
            asyncTaskQueues.addTask(new AsyncTask(
                    () -> parsedNodes.set(dataParser.parseNodes(nodesScanner)),
                    onTaskCompletion
            ));
            asyncTaskQueues.addTask(new AsyncTask(
                    () -> parsedRoadSegments.set(
                            dataParser.parseRoadSegments(segmentsScanner)
                    ),
                    onTaskCompletion
            ));
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Call this to collect data about what has been parsed from files
     */
    private void onParse(Collection<Node> nodes,
                         Collection<RoadSegment> roadSegments,
                         Supplier<Collection<RoadInfo>> roadInfoSupplier) {
        mapData = mapDataFactory.create(
                nodes,
                roadSegments,
                roadInfoSupplier
        );
        drawer = drawerFactory.create(mapData, view);
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
