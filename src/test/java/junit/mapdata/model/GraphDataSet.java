package junit.mapdata.model;

import main.mapdata.location.LatLong;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadSegment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Dylan on 27/03/17.
 *
 * Sets of data for use in tests
 */
public enum GraphDataSet {
    A_SHORT_LINEAR_GRAPH(
            1,
            3,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3)
            )
    ),
    B_TRIANGLE_GRAPH(
            1,
            3,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3),
                    segmentWithIdConnectedToNodes(103, 1, 3)
            )
    );

    public final List<RoadSegment> modelRoadSegments;
    public final List<Node> modelNodes;

    GraphDataSet(int firstNodeId,
                 int lastNodeId,
                 List<RoadSegment> modelRoadSegments) {
        this.modelNodes = Collections.unmodifiableList(
                IntStream.range(firstNodeId, lastNodeId + 1)
                        .mapToObj(GraphDataSet::nodeWithId)
                        .collect(Collectors.toList())
        );
        this.modelRoadSegments = Collections.unmodifiableList(
                modelRoadSegments
        );
    }

    public RoadSegment getSegmentById(int id) {
        return modelRoadSegments.stream()
                .filter(segment -> segment.roadId == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "RoadSegment with id " + id + " doesn't exist")
                );
    }

    public Node getNodeById(int id) {
        return modelNodes.stream()
                .filter(node -> node.id == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Node with id " + id + " doesn't exist")
                );
    }

    /**
     * Creates a {@link Node} with a default location, so that nodes with the
     * same id can be {@link Node#equals(Object)} to each other.
     */
    private static Node nodeWithId(int id) {
        return new Node(id, new LatLong(0, 0));
    }

    /**
     * Similar to nodeWithId
     */
    private static RoadSegment segmentWithIdConnectedToNodes(int id,
                                                      int nodeId1,
                                                      int nodeId2) {
        return new RoadSegment(id, 1, nodeId1, nodeId2, Collections.emptyList());
    }
}

