package main.mapdata.model;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import main.async.AsyncTask;
import main.async.AsyncTaskQueue;
import main.mapdata.*;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;
import main.datastructures.QuadTree;
import main.datastructures.Trie;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Wraps the data and loads and indexes it asynchronously to prevent blocking
 * the UI. Blocks any threads until data is loaded
 */
public class MapDataContainer {

    /**
     * Node id to Node (intersection/nodeInfo). It is called nodeInfos to avoid
     * confusion with {@link main.datastructures.Graph.Node}
     */
    private volatile Map<Long, Node> nodeInfos;
    private volatile Collection<RoadSegment> roadSegments;
    /**
     * RoadId to RoadInfo
     */
    private volatile Map<Long, RoadInfo> roadInfos;
    private volatile MapGraph mapGraph;
    private volatile Trie<RoadInfo> roadInfoLabelsTrie;
    private volatile QuadTree<Node> nodeTree;
    /**
     * endLevel to polygons
     */
    private volatile Map<Integer, List<Polygon>> polygons;


    @Inject
    public MapDataContainer(
            AsyncTaskQueue asyncTaskQueue,
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

        asyncTaskQueue.addTask(new AsyncTask(() -> {
            setRoadSegments(roadSegmentsSupplier.get());
            onCompleteCriticalTask.run();

            // Only load other stuff when when segments are loaded
            asyncTaskQueue.addTask(new AsyncTask(
                    () -> {
                        setNodeInfos(nodeInfosSupplier.get());

                        asyncTaskQueue.addTask(new AsyncTask(
                                this::setMapGraph,
                                "Create graph"
                        ));

                        asyncTaskQueue.addTask(new AsyncTask(
                                this::setNodeTree,
                                "Create node quad tree"
                        ));
                    },
                    "Parse node infos" // Critical task
            ));
            asyncTaskQueue.addTask(new AsyncTask(
                    () -> {
                        setRoadInfos(roadInfosSupplier.get());

                        asyncTaskQueue.addTask(new AsyncTask(
                                this::setRoadInfoLabelsTrie,
                                "Create Trie"
                        ));
                    }, "Parse road infos"
            ));
        }, "Parse road segments"));

        asyncTaskQueue.addTask(new AsyncTask(
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
        this.polygons = polygons.stream()
                .collect(Collectors.groupingBy(
                        polygon -> {
                            if (polygon.endLevel == null) {
                                return Integer.MIN_VALUE;
                            }
                            return polygon.endLevel;
                        },
                        HashMap::new,
                        Collectors.toList()
                ));
    }

    private void setNodeTree() {
        QuadTree<Node> nodeTree = new QuadTree<>(
                (a, b) -> a.latLong.asLocation().y - b.latLong.asLocation().y,
                (a, b) -> a.latLong.asLocation().x - b.latLong.asLocation().x
        );
        getNodeInfos().values()
                .forEach(nodeTree::add);
        this.nodeTree = nodeTree;
    }

    /**
     * @return roadInfos when ready
     */
    public Map<Long, RoadInfo> getRoadInfos() {
        return waitForLoad(() -> roadInfos);
    }

    public MapGraph getMapGraph() {
        return waitForLoad(() -> mapGraph);
    }

    public Trie<RoadInfo> getRoadInfoLabelsTrie() {
        return waitForLoad(() -> roadInfoLabelsTrie);
    }

    public Map<Long, Node> getNodeInfos() {
        return waitForLoad(() -> nodeInfos);
    }

    public QuadTree<Node> getNodeTree() {
        return waitForLoad(() -> nodeTree);
    }

    public Collection<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public Map<Integer, List<Polygon>> getPolygons() {
        return waitForLoad(() -> polygons);
    }

    private <T> T waitForLoad(Supplier<T> fieldWithLoadingInProgress) {
        while (fieldWithLoadingInProgress.get() == null) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
        return fieldWithLoadingInProgress.get();
    }

    public interface Factory {
        /**
         * Should have the same parameter names as the MapDataModel constructor (this
         * is used by Guice). This calls the constructor for {@link MapDataModel}.
         */
        MapDataContainer create(Runnable finishLoadingCallback,
                                Supplier<Collection<Node>> nodes,
                                Supplier<Collection<RoadSegment>> roadSegments,
                                Supplier<Collection<RoadInfo>> roadInfosSupplier,
                                Supplier<Collection<Polygon>> polygonsSupplier);
    }
}
