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
    public Collection<RoadSegment> findRoadSegmentsForNode(Node nodeInfo) {
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
        LinkedList<Node> routeNodes = new LinkedList<>();
        LinkedList<RoadSegment> routeSegments = new LinkedList<>();
        routeNodes.add(routeStartNode);

        while (!routeNodes.getLast().equals(routeEndNode)) {
            List<RoadSegment> segmentsForNode = new ArrayList<>(
                    findRoadSegmentsForNode(routeNodes.getLast())
            );
            RoadSegment nextSegment = segmentsForNode.get(
                    (int) (Math.random() * segmentsForNode.size())
            );
            Node nextNode = getOtherNode(nextSegment, routeNodes.getLast());

            routeNodes.add(nextNode);
            routeSegments.add(nextSegment);
        }

        return new Route(routeNodes, routeSegments);
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
