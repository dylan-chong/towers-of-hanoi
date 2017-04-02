package main.gui;

import com.google.inject.Inject;
import main.gui.helpers.Drawer;
import main.gui.helpers.MapMouseListener;
import main.gui.helpers.RouteOutputter;
import main.gui.helpers.View;
import main.mapdata.Route;
import main.mapdata.location.Location;
import main.mapdata.model.MapDataLoader;
import main.mapdata.model.MapDataModel;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadInfoByName;
import main.mapdata.roads.RoadSegment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
public class MapController extends GUI
        implements MapMouseListener.CallbackReceiver {

    private static final int MAX_SEARCH_RESULTS = Integer.MAX_VALUE;

    private final View view;
    private final Drawer.Factory drawerFactory;
    private final MapDataLoader mapDataLoader;
    private final MapMouseListener.Factory mouseListenerFactory;
    private final RouteOutputter routeOutputter;

    private Drawer drawer;
    private MapDataModel mapModel;
    /**
     * Should never be null. Stuff to highlight
     */
    private HighlightData highlightData;

    private Map<RoadInfo, Collection<RoadSegment>> searchResults;
    private MapMouseListener mouseListener;

    private MapState state = MapState.NORMAL;
    private Node routeStartNode;
    private DirectionsMode directionsMode;

    @Inject
    public MapController(View view,
                         Drawer.Factory drawerFactory,
                         MapDataLoader mapDataLoader,
                         MapMouseListener.Factory mouseListenerFactory,
                         RouteOutputter routeOutputter) {
        this.view = view;
        this.drawerFactory = drawerFactory;
        this.mapDataLoader = mapDataLoader;
        this.mouseListenerFactory = mouseListenerFactory;
        this.routeOutputter = routeOutputter;

        initialise();
        view.setOriginalComponentSize(getDrawingAreaDimension());
    }

    @Override
    protected void redraw(Graphics graphics) {
        if (!isLoaded()) return;

        drawer.draw(graphics, highlightData, getDrawingAreaDimension());
    }

    @Override
    protected void onSearch() {
        if (!isLoaded()) return;

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
     * Called after the mouse has been clicked on the graphics pane and then
     * released.
     */
    @Override
    public void onClick(Point point) {
        if (!isLoaded()) return;
        Location clickLocation = view.getLocationFromPoint(point);
        ClickSelection selection = getClickSelectionForClosestNodeTo(clickLocation);

        // Show information and set highlightData
        applyClickSelection(selection, state);
        redraw();

        if (state == MapState.NORMAL) return;
        if (selection.selectedNode == null) return;

        // Give time to redraw
        SwingUtilities.invokeLater(() -> {
            if (state == MapState.ENTER_ROUTE_START_NODE) {
                setRouteStartNode(selection.selectedNode);
                state = MapState.ENTER_ROUTE_LAST_NODE;
            } else if (state == MapState.ENTER_ROUTE_LAST_NODE) {
                setRouteEndNode(selection.selectedNode);
                state = MapState.NORMAL;
            }
        });
        // Redraw after above runnable finishes
        SwingUtilities.invokeLater(this::redraw);
    }

    @Override
    public void onDrag(int dx, int dy) {
        if (!isLoaded()) return;
        view.applyDrag(dx, dy);
        redraw();
    }

    @Override
    public void onScroll(boolean isZoomIn, Point mousePosition) {
        if (!isLoaded()) return;
        // Allow zooming at the mouse position
        Dimension fakeDimension = new Dimension(
                mousePosition.x * 2, mousePosition.y * 2
        );
        view.applyMove(isZoomIn ? Move.ZOOM_IN : Move.ZOOM_OUT, fakeDimension);
        redraw();
    }

    /**
     * @param polygons a File for polygon-shapes.mp (map be null)
     */
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        this.mapModel = null;
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
    protected void onArticulationPointsClick() {
        if (!isLoaded()) return;

        outputLine("Finding articulation points...");
        SwingUtilities.invokeLater(() -> {
            Collection<Node> articulationPoints =
                    mapModel.findAllArticulationPoints();
            highlightData = new HighlightData(articulationPoints, null, null);
            outputLine("Found " + articulationPoints.size() +
                    " articulation points");
            redraw();
        });
    }

    @Override
    protected void onEnterDirectionsClick() {
        if (!isLoaded()) return;

        state = MapState.ENTER_ROUTE_START_NODE;
        outputLine("Click on an intersection to define the start");

        // Clear route
        clearHighlightingData();
        redraw();
    }

    @Override
    protected void onDirectionsModeClick(ActionEvent event,
                                         JButton directionsModeButton) {
        List<DirectionsMode> modes = Arrays.asList(DirectionsMode.values());
        int currentIndex = (modes.indexOf(directionsMode) + 1) % modes.size();
        setDirectionsMode(directionsModeButton, modes.get(currentIndex));
    }

    @Override
    protected void setDirectionsMode(JButton directionsModeButton,
                                     DirectionsMode mode) {
        directionsMode = mode;
        directionsModeButton.setText(
                DIRECTIONS_PRIORITY_BUTTON_TEXT + mode.getTitle()
        );
    }

    @Override
    protected MouseAdapter getMouseListener() {
        if (mouseListener == null) {
            mouseListener = mouseListenerFactory.create(this);
        }
        return mouseListener;
    }

    private boolean isLoaded() {
        return mapModel != null;
    }

    private void setRouteStartNode(Node routeStartNode) {
        this.routeStartNode = routeStartNode;
        // Hack to show just one circle (for the route start)
        highlightData = highlightData.getNewWithRoute(new Route(
                Collections.singletonList(routeStartNode),
                Collections.emptyList()
        ));
        outputLine("Now select the node for the end of your route");
    }

    private void setRouteEndNode(Node routeEndNode) {
        outputLine("Finding route from " + routeStartNode.id +
                " to " + routeEndNode.id + " ...");
        highlightData = highlightData.getNewWithRoute(
                // Make fake so we an draw without knowing the route
                Route.makeFakeRoute(routeStartNode, routeEndNode)
        );
        redraw(); // Show the selection before searching for better UX

        // Give time to redraw and update text area
        SwingUtilities.invokeLater(() -> {
            Route route = mapModel.findRouteBetween(
                    routeStartNode,
                    routeEndNode,
                    directionsMode.getCostHeuristic()
            );
            // redraw will be called automatically

            highlightData = highlightData.getNewWithRoute(route);
            if (route == null) {
                outputLine("No route found");
                return;
            }

            outputLine("Route found");
            routeOutputter.outputRoute(route, mapModel, this::outputLine);

            redraw();
        });
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
    private void applyClickSelection(ClickSelection selection, MapState state) {
        highlightData = new HighlightData(
                // these fields may be null
                Collections.singleton(selection.selectedNode),
                selection.connectedSegments,
                null
        );
        if (selection.selectedNode == null) {
            outputLine("There are no nodes there");
            return;
        }

        if (state != MapState.NORMAL) return;

        // Show selected node details
        outputLine("Found an intersection: " + selection.selectedNode);
        selection.segmentInfosByName.forEach(
                roadInfoByName -> outputLine("- Connected to: " + roadInfoByName)
        );
    }

    private ClickSelection getClickSelectionForClosestNodeTo(Location location) {
        // 'Node' means 'intersection' here
        Node selectedNode = mapModel.findNodeNearLocation(location);
        if (selectedNode == null) {
            return new ClickSelection(null, null, null, null);
        }
        return mapModel.getClickSelection(selectedNode);
    }

    /**
     * Stores information that is found when finding the node that was
     * clicked
     */
    public static class ClickSelection {
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
