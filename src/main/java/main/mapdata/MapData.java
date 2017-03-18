package main.mapdata;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import main.async.AsyncTask;
import main.async.AsyncTaskQueues;
import main.structures.Graph;
import main.structures.Trie;
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

    // Don't access any of these variables directly. Use the getter, because
    // these are loaded in a separate thread. The getters should block the
    // thread until it is loaded.
    /**
     * Node id to Node (intersection/nodeInfo). It is called nodeInfos to avoid
     * confusion with {@link main.structures.Graph.Node}
     */
    private Map<Long, Node> nodeInfos;
    private Collection<RoadSegment> roadSegments;
    /**
     * RoadId to RoadInfo
     */
    private Map<Long, RoadInfo> roadInfos;
    private MapGraph mapGraph;
    private Trie<RoadInfo> roadInfoLabelsTrie;

    @Inject
    private MapData(AsyncTaskQueues asyncTaskQueues,
                    @Assisted Runnable finishLoadingCallback,
                    @Assisted Supplier<Collection<Node>> nodeInfosSupplier,
                    @Assisted Supplier<Collection<RoadSegment>> roadSegmentsSupplier,
                    @Assisted Supplier<Collection<RoadInfo>> roadInfosSupplier) {
        // Load data in a new thread to prevent blocking user input
        asyncTaskQueues.addTask(new AsyncTask(
                () -> {
                    setRoadSegments(roadSegmentsSupplier.get());
                    finishLoadingCallback.run();

                    asyncTaskQueues.addTask(new AsyncTask(
                            () -> {
                                setNodeInfos(nodeInfosSupplier.get());
                                asyncTaskQueues.addTask(new AsyncTask(
                                        this::setMapGraph,
                                        "Create graph"
                                ));
                            },
                            "Parse node infos"
                    ));
                    asyncTaskQueues.addTask(new AsyncTask(
                            () -> {
                                setRoadInfos(roadInfosSupplier.get());
                                asyncTaskQueues.addTask(new AsyncTask(
                                        () -> setRoadInfoLabelsTrie(getRoadInfos().values()),
                                        "Create Trie"
                                ));
                            },
                            "Parse road infos"
                    ));
                },
                "Parse road segments"
        ));
    }

    public void setRoadSegments(Collection<RoadSegment> roadSegments) {
        this.roadSegments = roadSegments;
    }

    private void setNodeInfos(Collection<Node> nodes) {
        this.nodeInfos = Collections.unmodifiableMap(
                nodes.stream()
                        .collect(Collectors.toMap(
                                node -> node.id,
                                node -> node
                        ))
        );
    }

    private void setRoadInfos(Collection<RoadInfo> roadInfosCollection) {
        this.roadInfos = Collections.unmodifiableMap(
                roadInfosCollection.stream()
                        .collect(Collectors.toMap(
                                roadInfo -> roadInfo.id,
                                roadInfo -> roadInfo
                        ))
        );
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

    public void setRoadInfoLabelsTrie(Collection<RoadInfo> roadInfos) {
        Trie<RoadInfo> rootTrie = new Trie<>();

        roadInfos.forEach(roadInfo -> {
            String label = roadInfo.label;
            // label = label.trim(); // TODO
            Trie<RoadInfo> nextTrieToUse = rootTrie;

            for (int c = 0; c < label.length(); c++) {
                char character = label.charAt(c);
                boolean isLastChar = c == label.length() - 1;
                // Set next Trie to sub-Trie
                nextTrieToUse.addNextChar(
                        character,
                        // Only save data at the end of the string
                        isLastChar ? roadInfo : null
                );
                nextTrieToUse = nextTrieToUse.getTrieForNextChar(character);
            }
        });

        this.roadInfoLabelsTrie = rootTrie;
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
        while (mapGraph == null) {
            waitForLoad();
        }
        return mapGraph;
    }

    private Trie<RoadInfo> getRoadInfoLabelsTrie() {
        while (roadInfoLabelsTrie == null) {
            waitForLoad();
        }
        return roadInfoLabelsTrie;
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

        Trie<RoadInfo> matchingTrie = getRoadInfoLabelsTrie()
                .getSubTrieForChars(searchTerm);
        Collection<RoadInfo> matchingRoadInfos =
                matchingTrie.getDatasRecursive(maxResults);

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

    public interface Factory {
        /**
         * Should have the same parameter names as the MapData constructor (this
         * is used by Guice). This calls the constructor for {@link MapData}.
         */
        MapData create(Runnable finishLoadingCallback,
                       Supplier<Collection<Node>> nodes,
                       Supplier<Collection<RoadSegment>> roadSegments,
                       Supplier<Collection<RoadInfo>> roadInfosSupplier);
    }
}
