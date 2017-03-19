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
import java.util.concurrent.atomic.AtomicInteger;
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
    private Collection<Polygon> polygons;

    @Inject
    private MapData(AsyncTaskQueues asyncTaskQueues,
                    @Assisted Runnable finishLoadingCallback,
                    @Assisted Supplier<Collection<Node>> nodeInfosSupplier,
                    @Assisted Supplier<Collection<RoadSegment>> roadSegmentsSupplier,
                    @Assisted Supplier<Collection<RoadInfo>> roadInfosSupplier,
                    @Assisted Supplier<Collection<Polygon>> polygonsSupplier) {

        // Load segments and polygons first
        AtomicInteger criticalTasksLeft = new AtomicInteger(2);

        Runnable onCompleteCriticalTask = () -> {
            if (criticalTasksLeft.decrementAndGet() > 0) return;
            finishLoadingCallback.run();
        };

        // Load data in a new thread to prevent blocking user input

        // The 'top-level' addTask calls are the first tasks to be run. The
        // inner tasks are started after a top-level one is completed

        asyncTaskQueues.addTask(new AsyncTask(() -> {
            setRoadSegments(roadSegmentsSupplier.get());
            onCompleteCriticalTask.run();

            // Only load other stuff when when segments are loaded
            asyncTaskQueues.addTask(new AsyncTask(
                    () -> {
                        setNodeInfos(nodeInfosSupplier.get());

                        asyncTaskQueues.addTask(new AsyncTask(
                                this::setMapGraph,
                                "Create graph"
                        ));
                    },
                    "Parse node infos" // Critical task
            ));
            asyncTaskQueues.addTask(new AsyncTask(
                    () -> {
                        setRoadInfos(roadInfosSupplier.get());

                        asyncTaskQueues.addTask(new AsyncTask(
                                this::setRoadInfoLabelsTrie,
                                "Create Trie"
                        ));
                    }, "Parse road infos"
            ));
        }, "Parse road segments"));

        asyncTaskQueues.addTask(new AsyncTask(
                () -> {
                    setPolygons(polygonsSupplier.get());
                    onCompleteCriticalTask.run();
                },
                "Parse polygons" // Critical task
        ));
    }

    private void setRoadSegments(Collection<RoadSegment> roadSegments) {
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
        getNodeInfos().values().forEach(mapGraph::createNode);
        // Create edges
        getRoadSegments().forEach(segment -> mapGraph.createEdge(
                mapGraph.getNodeForNodeInfo(getNodeInfos().get(segment.node1ID)),
                mapGraph.getNodeForNodeInfo(getNodeInfos().get(segment.node2ID)),
                segment
        ));
        this.mapGraph = mapGraph;
    }

    private void setRoadInfoLabelsTrie() {
        Collection<RoadInfo> roadInfos = getRoadInfos().values();
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

    private void setPolygons(Collection<Polygon> polygons) {
        this.polygons = polygons;
    }

    /**
     * @return roadInfos when ready
     */
    public Map<Long, RoadInfo> getRoadInfos() {
        while (roadInfos == null) {
            waitForLoad();
        }
        return roadInfos;
    }

    public MapGraph getMapGraph() {
        while (mapGraph == null) {
            waitForLoad();
        }
        return mapGraph;
    }

    public Trie<RoadInfo> getRoadInfoLabelsTrie() {
        while (roadInfoLabelsTrie == null) {
            waitForLoad();
        }
        return roadInfoLabelsTrie;
    }

    public Map<Long, Node> getNodeInfos() {
        while (nodeInfos == null) {
            waitForLoad();
        }
        return nodeInfos;
    }

    public Collection<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public Collection<Polygon> getPolygons() {
        return polygons;
    }

    private void waitForLoad() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Find the nearest {@link Node} near the given location, within a circle
     * with a radius defined by locationUnits.
     *
     * @param locationUnits The number of units in the {@link Location}
     *                      coordinate system.
     */
    public Node findNodeNearLocation(Location location, double locationUnits) {
        Stream<Node> stream = getNodeInfos()
                .values()
                .stream();

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
        if (matchingTrie == null) {
            return Collections.emptyMap(); // No matches
        }

        Collection<RoadInfo> matchingRoadInfos;
        if (matchingTrie.getDatas().isEmpty()) {
            // Look for prefix matches
            matchingRoadInfos = matchingTrie.getDatasRecursive(maxResults);
        } else {
            // We have exact matches, so use those instead
            matchingRoadInfos = matchingTrie.getDatas();
        }

        Map<RoadInfo, Collection<RoadSegment>> result = new HashMap<>();
        matchingRoadInfos.forEach(roadInfo ->
                result.put(roadInfo, findRoadSegmentsForRoadInfo(roadInfo))
        );
        return result;
    }

    public Collection<RoadSegment> findRoadSegmentsForRoadInfo(RoadInfo roadInfo) {
        return getRoadSegments().stream()
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
                       Supplier<Collection<RoadInfo>> roadInfosSupplier,
                       Supplier<Collection<Polygon>> polygonsSupplier);
    }
}
