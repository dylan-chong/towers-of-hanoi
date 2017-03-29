package main.mapdata;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import main.LatLong;
import main.structures.Graph;
import main.structures.Route;
import main.structures.Trie;
import slightlymodifiedtemplate.Location;

import java.util.*;
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
        return graphNode.getEdges().stream()
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
                            getOtherNode(segment, currentNodeState.getNode())
                    )
                    .collect(Collectors.toList());

            for (int i = 0; i < neighbourNodes.size(); i++) {
                Node neighbourNode = neighbourNodes.get(i);
                // Connects neighbourNode and nodeState.node
                RoadSegment connectingSegment = currentSegments.get(i);

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
        return Route.newFromNodeState(lastNodeState);
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
    private Node getOtherNode(RoadSegment segment, Node oneEnd) {
        return Stream.of(segment.node1ID, segment.node2ID)
                .map(nodeId -> data.getNodeInfos().get(nodeId))
                .filter(node -> !oneEnd.equals(node))
                .findFirst()
                .orElse(null);
    }

    public interface Factory {
        MapDataModel create(MapDataContainer mapDataContainer);
    }
}
