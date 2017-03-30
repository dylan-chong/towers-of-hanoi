package main.mapdata.model;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import main.mapdata.location.LatLong;
import main.gui.MapController;
import main.mapdata.*;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadInfoByName;
import main.mapdata.roads.RoadSegment;
import main.datastructures.Graph;
import main.mapdata.Route;
import main.datastructures.Trie;
import main.mapdata.location.Location;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Dylan on 15/03/17.
 * <p>
 * For storing data about the map and finding data
 */
public class MapDataModel {

    public final MapDataContainer data;

    @Inject
    public MapDataModel(@Assisted MapDataContainer data) {
        this.data = data;
    }

    /**
     * Find the nearest {@link Node} near the given location
     */
    public Node findNodeNearLocation(Location location) {
        // Hack to avoid converting a Location to a LatLong
        Node fakeNode = new Node(-1, new LatLong(-1, -1) {
            @Override
            public Location asLocation() {
                // Actual location
                return location;
            }
        });
        Node closestNode = data.getNodeTree().closestDataTo(fakeNode);

        if (closestNode == null) return null;
        return closestNode;
    }

    public Map<RoadInfo, Collection<RoadSegment>> findRoadSegmentsByString(
            String searchTerm, int maxResults) {
        Trie<RoadInfo> matchingTrie = data.getRoadInfoLabelsTrie()
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
        return data.getRoadSegments().stream()
                .filter(segment -> segment.roadId == roadInfo.id)
                .collect(Collectors.toList());
    }

    /**
     * Selects roads segments connected to node
     */
    public List<RoadSegment> findRoadSegmentsForNode(Node nodeInfo) {
        MapGraph.Node graphNode = data.getMapGraph().getNodeForNodeInfo(nodeInfo);
        return graphNode.getEdges()
                .stream()
                .map(Graph.Edge::getInfo)
                .collect(Collectors.toList());
    }

    public RoadInfo findRoadInfoForSegment(RoadSegment segment) {
        return data.getRoadInfos().get(segment.roadId);
    }

    public Route findRouteBetween(Node routeStartNode,
                                  Node routeEndNode) {
        Comparator<NodeState> comparator = Comparator.comparingDouble(
                (nodeState) -> {
                    double estimate = nodeState.getNode()
                            .latLong.estimatedDistanceInKmTo(routeEndNode.latLong);
                    return nodeState.getDistanceFromStart() + estimate;
                }
        );
        NavigableSet<NodeState> nodesToCheck = new TreeSet<>(comparator);
        // For fast lookup
        Map<Node, NodeState> nodeStateMap = new HashMap<>();

        { // Add routeStartNode
            NodeState startState = new NodeState(routeStartNode, null, null, 0);
            nodesToCheck.add(startState);
            nodeStateMap.put(routeStartNode, startState);
        }

        while (!nodesToCheck.isEmpty()) {
            NodeState currentNodeState = nodesToCheck.pollFirst();

            assert !currentNodeState.hasCheckedChildren();
            currentNodeState.setHasCheckedChildren(true);

            // Segments leaving nodeState
            List<RoadSegment> currentSegments = findRoadSegmentsForNode(
                    currentNodeState.getNode()
            );
            List<Node> neighbourNodes = currentSegments.stream()
                    .map(segment ->
                            findOtherNode(segment, currentNodeState.getNode())
                    )
                    .collect(Collectors.toList());

            for (int i = 0; i < neighbourNodes.size(); i++) {
                Node neighbourNode = neighbourNodes.get(i);
                // Connects neighbourNode and nodeState.node
                RoadSegment connectingSegment = currentSegments.get(i);

                if (findRoadInfoForSegment(connectingSegment).isOneWay) {
                    Node currentNode = currentNodeState.getNode();
                    if (connectingSegment.node1ID != currentNode.id) {
                        // Wrong way
                        continue;
                    }
                }

                NodeState neighbourState = nodeStateMap.computeIfAbsent(
                        neighbourNode,
                        key -> new NodeState(
                                neighbourNode,
                                connectingSegment,
                                currentNodeState,
                                Double.POSITIVE_INFINITY
                        )
                );

                if (neighbourState.hasCheckedChildren()) continue;

                nodesToCheck.add(neighbourState);

                // Distance up to neighbourNode
                double newDistanceFromStart = currentNodeState
                        .getDistanceFromStart() + connectingSegment.length;
                if (newDistanceFromStart >= neighbourState.getDistanceFromStart()) {
                    continue;
                }

                // Remove (if necessary) before changing priority
                nodesToCheck.remove(neighbourState);

                neighbourState.setDistanceFromStart(newDistanceFromStart);
                neighbourState.setSegmentToPreviousNode(connectingSegment);
                neighbourState.setPreviousNodeState(currentNodeState);

                // Add back with new priority
                nodesToCheck.add(neighbourState);
            }

            // Check if priority queue is in order
            assert isSorted(nodesToCheck, comparator);

            if (currentNodeState.getNode().equals(routeEndNode)) {
                break;
            }
        }

        NodeState lastNodeState = nodeStateMap.get(routeEndNode);
        if (lastNodeState == null) return null; // No route
        Route route = Route.newFromNodeState(lastNodeState);
        if (route.nodes.get(0) != routeStartNode) return null;
        return route;
    }

    public MapController.ClickSelection getClickSelection(Node selectedNode) {
        // Find roadSegments (for highlighting) and roadInfos (for printing)
        Collection<RoadSegment> roadSegments =
                findRoadSegmentsForNode(selectedNode);
        Collection<RoadInfo> roadInfos = roadSegments.stream()
                .map(this::findRoadInfoForSegment)
                .collect(Collectors.toList());

        // Print road label/city (and avoid printing duplicate names)
        Collection<RoadInfoByName> roadInfosByName =
                RoadInfo.getDistinctByName(roadInfos);

        return new MapController.ClickSelection(
                selectedNode,
                roadSegments,
                roadInfos,
                roadInfosByName
        );
    }

    public Collection<Node> getNeighbours(Node node) {
        return findRoadSegmentsForNode(node)
                .stream()
                .map(segment -> findOtherNode(segment, node))
                .collect(Collectors.toList());
    }

    public Set<Node> findArticulationPoints(Node startNode) {
        Set<Node> articulationPoints = new HashSet<>();

        Map<Node, Integer> counts = new HashMap<>();
        AtomicInteger lastCount = new AtomicInteger(1);
        counts.put(startNode, lastCount.get());
        int subTreesOfStart = 0;

        for (Node neighbour : getNeighbours(startNode)) {
            if (counts.get(neighbour) != null) continue;

            subTreesOfStart++;

            addArticulationPoints(
                    neighbour, startNode, articulationPoints, lastCount, counts
            );
        }

        if (subTreesOfStart > 1) {
            articulationPoints.add(startNode);
        }

        return articulationPoints;
    }

    /**
     * To be called from {@link MapDataModel#findArticulationPoints()}
     */
    private void addArticulationPoints(Node node,
                                       Node root,
                                       Set<Node> articulationPoints,
                                       AtomicInteger lastCount,
                                       Map<Node, Integer> counts) {
        Deque<ArticulationPointsState> stateStack = new ArrayDeque<>();
        stateStack.add(new ArticulationPointsState(
                node, root)
        );
        Map<Node, Integer> reachBacks = new HashMap<>();
        reachBacks.put(root, counts.get(root));

        while (!stateStack.isEmpty()) {
            ArticulationPointsState currentState = stateStack.peek();
            Node currentNode = currentState.node;

            if (counts.get(currentNode) == null) {
                counts.put(currentNode, lastCount.incrementAndGet());
                reachBacks.put(currentNode, lastCount.get());

                for (Node neighbour : getNeighbours(currentNode)) {
                    if (neighbour == currentState.previousNode) continue;
                    if (counts.get(neighbour) != null) {
                        reachBacks.compute(currentNode, (currNode, currReach) ->
                                Math.min(currReach, counts.get(neighbour))
                        );
                        continue;
                    }
                    stateStack.push(new ArticulationPointsState(
                            neighbour, currentNode)
                    );
                }

                continue;
            }

            for (Node neighbour : getNeighbours(currentNode)) {
                if (neighbour == currentState.previousNode) continue;
                if (reachBacks.get(neighbour) < counts.get(currentNode)) {
                    reachBacks.compute(currentNode, (currNode, currReach) ->
                            Math.min(currReach, reachBacks.get(neighbour))
                    );
                    continue;
                }

                articulationPoints.add(currentNode);
            }

            stateStack.pop();
        }
    }

    private <T> boolean isSorted(Collection<? extends T> data,
                                 Comparator<? super T> comparator) {
        @SuppressWarnings("unchecked")
        T[] dataArray = data.toArray((T[]) new Object[data.size()]);
        T[] dataSorted = Arrays.copyOf(
                dataArray, dataArray.length
        );
        Arrays.sort(dataSorted, comparator);
        return Arrays.equals(dataArray, dataSorted);
    }

    /**
     * Useful function for getting the {@link Node} at the opposite end of
     * a {@link RoadSegment}.
     */
    private Node findOtherNode(RoadSegment segment, Node oneEnd) {
        return Stream.of(segment.node1ID, segment.node2ID)
                .map(nodeId -> data.getNodeInfos().get(nodeId))
                .filter(node -> !oneEnd.equals(node))
                .findFirst()
                .orElse(null);
    }

    public interface Factory {
        MapDataModel create(MapDataContainer mapDataContainer);
    }

    /**
     * Essentially represents an item on the call stack for the articulation
     * points algorithm. This is necessary for making the algorithm iterative.
     */
    private static class ArticulationPointsState {
        public final Node node, previousNode;

        public ArticulationPointsState(Node node, Node previousNode) {
            this.node = node;
            this.previousNode = previousNode;
        }
    }

}
