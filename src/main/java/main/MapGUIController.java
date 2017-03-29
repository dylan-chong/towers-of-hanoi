package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import main.mapdata.Node;
import main.mapdata.RoadInfo;
import main.mapdata.RoadInfoByName;
import main.mapdata.RoadSegment;
import main.mapdata.model.MapDataLoader;
import main.mapdata.model.MapDataModel;
import main.structures.Route;
import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUIController extends GUI
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

    private MapGUIState state = MapGUIState.NORMAL;
    private Node routeStartNode;

    @Inject
    public MapGUIController(View view,
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
     * Called after the mouse has been clicked on the graphics pane and then
     * released.
     */
    @Override
    public void onClick(Point point) {
        if (mapModel == null) return;
        reactToOnClick(point);
        redraw();
    }

    /**
     * To be called from onClick
     */
    private void reactToOnClick(Point point) {
        Location clickLocation = view.getLocationFromPoint(point);
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

    @Override
    public void onDrag(int dx, int dy) {
        if (view == null) return;
        view.applyDrag(dx, dy);
        redraw();
    }

    @Override
    public void onScroll(boolean isZoomIn, Point mousePosition) {
        // Allow zooming at the mouse position
        Dimension fakeDimension = new Dimension(mousePosition.x, mousePosition.y);
        view.applyMove(isZoomIn ? Move.ZOOM_IN : Move.ZOOM_OUT, fakeDimension);
        redraw();
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
        if (mouseListener == null) {
            mouseListener = mouseListenerFactory.create(this);
        }
        return mouseListener;
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
        outputLine("Finding route from " + routeStartNode.id +
                " to " + routeEndNode.id + " ...");
        highlightData = highlightData.getNewWithRoute(
                // Make fake so we an draw without knowing the route
                Route.makeFakeRoute(routeStartNode, routeEndNode)
        );
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
        routeOutputter.outputRoute(route, mapModel, this::outputLine);
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
