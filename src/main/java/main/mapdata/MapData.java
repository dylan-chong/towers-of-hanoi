package main.mapdata;

import com.google.inject.Inject;
import main.async.AsyncTask;
import main.async.AsyncTaskQueues;
import main.structures.Graph;
import slightlymodifiedtemplate.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Dylan on 15/03/17.
 * <p>
 * For storing data about the map and finding data
 */
public class MapData {
    /**
     * Node id to Node (intersection/nodeInfo). It is called nodeInfos to avoid
     * confusion with {@link main.structures.Graph.Node}
     */
    private final Map<Long, Node> nodeInfos;
    private final Collection<RoadSegment> roadSegments;

    // Don't access any of these variables directly. Use the getter, because
    // these are loaded in a separate thread
    /**
     * RoadId to RoadInfo
     */
    private Map<Long, RoadInfo> roadInfos;
    private MapGraph mapGraph;

    private MapData(AsyncTaskQueues asyncTaskQueues,
                    Collection<Node> nodes,
                    Collection<RoadSegment> roadSegments,
                    Supplier<Collection<RoadInfo>> roadInfosSupplier) {
        nodeInfos = nodes.stream()
                .collect(Collectors.toMap(
                        (node) -> node.id,
                        (node) -> node
                ));

        this.roadSegments = Collections.unmodifiableCollection(roadSegments);

        // Load data in a new thread to prevent blocking user input
        asyncTaskQueues.addTask(new AsyncTask(
                () -> setRoadInfos(roadInfosSupplier.get()),
                () -> {} // No callback required
        ));
        asyncTaskQueues.addTask(new AsyncTask(
                this::setMapGraph,
                () -> {}
        ));
    }

    private void setRoadInfos(Collection<RoadInfo> roadInfosCollection) {
        this.roadInfos = roadInfosCollection.stream()
            .collect(Collectors.toMap(
                    (roadInfo) -> roadInfo.id,
                    (roadInfo) -> roadInfo
            ));
    }

    private void setMapGraph() {
        MapGraph mapGraph = new MapGraph();
        // Create nodes
        nodeInfos.values().forEach(mapGraph::createNode);
        // Create edges
        roadSegments.forEach(segment -> mapGraph.createEdge(
                mapGraph.getNodeForNodeInfo(nodeInfos.get(segment.node1ID)),
                mapGraph.getNodeForNodeInfo(nodeInfos.get(segment.node2ID)),
                segment
        ));
        this.mapGraph = mapGraph;
    }

    /**
     * @return roadInfos when ready
     */
    private Map<Long, RoadInfo> getRoadInfos() {
        while (roadInfos == null) {
            waitForLoad();
        }
        return roadInfos;
    }

    private MapGraph getMapGraph() {
        while (roadInfos == null) {
            waitForLoad();
        }
        return mapGraph;
    }

    private void waitForLoad() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    public Collection<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    /**
     * Find the nearest {@link Node} near the given location, within a circle
     * with a radius defined by locationUnits.
     *
     * @param locationUnits The number of units in the {@link Location}
     *                      coordinate system.
     */
    public Node findNodeNearLocation(Location location, double locationUnits) {
        Stream<Node> stream = nodeInfos.values().stream();

        // Find all nodeInfos within range
        stream = stream.filter(node -> node.latLong
                .asLocation().isCloseFast(location, locationUnits));

        // Find closest node within range
        Node closestNode = stream.reduce((node1, node2) -> {
            double dist1 = node1.latLong.asLocation().distance(location);
            double dist2 = node2.latLong.asLocation().distance(location);
            if (dist1 < dist2) return node1;
            return node2;
        }).orElse(null);

        if (closestNode == null) return null;
        if (closestNode.latLong.asLocation().distance(location) > locationUnits) {
            return null;
        }
        return closestNode;
    }

    public Map<RoadInfo, Collection<RoadSegment>> findRoadSegmentsByString(
            String searchTerm, int maxResults) {

        // TODO trie
        Collection<RoadInfo> matchingRoadInfos = getRoadInfos().values()
                .stream()
                .filter(roadInfo -> searchMatches(roadInfo.label, searchTerm) ||
                        searchMatches(roadInfo.city, searchTerm))
                .limit(maxResults)
                .collect(Collectors.toList());

        Map<RoadInfo, Collection<RoadSegment>> result = new HashMap<>();
        matchingRoadInfos.forEach(roadInfo ->
                result.put(roadInfo, findRoadSegmentsForRoadInfo(roadInfo))
        );
        return result;
    }

    public Collection<RoadSegment> findRoadSegmentsForRoadInfo(RoadInfo roadInfo) {
        return roadSegments.stream()
                .filter(segment ->
                        segment.roadId == roadInfo.id
                )
                .collect(Collectors.toList());
    }

    private boolean searchMatches(String stringToCheck, String searchTerm) {
        return stringToCheck.toLowerCase().startsWith(searchTerm.toLowerCase());
    }

    /**
     * Selects roads segments connected to node
     */
    public Collection<RoadSegment> findRoadSegmentsForNode(Node nodeInfo) {
        MapGraph.Node graphNode = getMapGraph().getNodeForNodeInfo(nodeInfo);
        return graphNode.getEdges().stream()
                .map(Graph.Edge::getInfo)
                .collect(Collectors.toList());
    }

    public RoadInfo findRoadInfoForSegment(RoadSegment segment) {
        return this.getRoadInfos().get(segment.roadId);
    }

    public static class Factory {
        private final AsyncTaskQueues asyncTaskQueues;

        @Inject
        public Factory(AsyncTaskQueues asyncTaskQueues) {
            this.asyncTaskQueues = asyncTaskQueues;
        }

        /**
         * Must have the same parameter names as the MapData constructor (this
         * is used by Guice)
         */
        public MapData create(Collection<Node> nodes,
                       Collection<RoadSegment> roadSegments,
                       Supplier<Collection<RoadInfo>> roadInfosSupplier) {
            return new MapData(
                    asyncTaskQueues,
                    nodes,
                    roadSegments,
                    roadInfosSupplier
            );
        }
    }
}
