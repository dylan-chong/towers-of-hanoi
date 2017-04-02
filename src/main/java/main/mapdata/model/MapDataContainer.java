package main.mapdata.model;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import main.async.AsyncTask;
import main.async.AsyncTaskQueue;
import main.mapdata.*;
import main.mapdata.roads.Node;
import main.mapdata.roads.Restriction;
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
    private Set<Restriction> restrictions;


    @Inject
    public MapDataContainer(
            AsyncTaskQueue asyncTaskQueue,
            @Assisted Runnable finishLoadingCallback,
            @Assisted Supplier<Collection<Node>> nodeInfosSupplier,
            @Assisted Supplier<Collection<RoadSegment>> roadSegmentsSupplier,
            @Assisted Supplier<Collection<RoadInfo>> roadInfosSupplier,
            @Assisted Supplier<Collection<Polygon>> polygonsSupplier,
            @Assisted Supplier<Collection<Restriction>> restrictionsSupplier) {

        // Load segments and polygons first
        AtomicInteger criticalTasksLeft = new AtomicInteger(2);

        Runnable loadNonCriticalTasks = () -> {
            asyncTaskQueue.addTask(new AsyncTask(
                    "Parse node infos",
                    () -> {
                        setNodeInfos(nodeInfosSupplier.get());

                        asyncTaskQueue.addTask(new AsyncTask(
                                "Create graph",
                                this::setMapGraph
                        ));

                        asyncTaskQueue.addTask(new AsyncTask(
                                "Create node quad tree",
                                this::setNodeTree
                        ));

                        asyncTaskQueue.addTask(new AsyncTask(
                                "Parse restrictions",
                                () -> setRestrictions(restrictionsSupplier.get())
                        ));
                    }
            ));
            asyncTaskQueue.addTask(new AsyncTask(
                    "Parse road infos", () -> {
                setRoadInfos(roadInfosSupplier.get());

                asyncTaskQueue.addTask(new AsyncTask(
                        "Create Trie",
                        this::setRoadInfoLabelsTrie
                ));
            }
            ));
        };

        Runnable onCompleteCriticalTask = () -> {
            if (criticalTasksLeft.decrementAndGet() > 0) return;
            finishLoadingCallback.run();

            // Load data in a new thread to prevent blocking user input
            // The 'top-level' addTask calls are the first non-critical tasks
            // to be run. the inner tasks are started after a top-level one is
            // completed
            loadNonCriticalTasks.run();
        };

        // Critical tasks

        asyncTaskQueue.addTask(new AsyncTask("Parse road segments", () -> {
            setRoadSegments(roadSegmentsSupplier.get());
            onCompleteCriticalTask.run();
        }));

        asyncTaskQueue.addTask(new AsyncTask("Parse polygons", () -> {
            setPolygons(polygonsSupplier.get());
            onCompleteCriticalTask.run();
        }));
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

    private void setRestrictions(Collection<Restriction> restrictions) {
        this.restrictions = new HashSet<>(restrictions);
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

    public Set<Restriction> getRestrictions() {
        return waitForLoad(() -> restrictions);
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
        MapDataContainer create(
                Runnable finishLoadingCallback,
                Supplier<Collection<Node>> nodes,
                Supplier<Collection<RoadSegment>> roadSegments,
                Supplier<Collection<RoadInfo>> roadInfosSupplier,
                Supplier<Collection<Polygon>> polygonsSupplier,
                Supplier<Collection<Restriction>> restrictionsSupplier);
    }
}
