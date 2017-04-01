package junit.mapdata.model;

import main.mapdata.location.LatLong;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Dylan on 27/03/17.
 * <p>
 * Sets of data for use in tests.
 * <p>
 * Use single digit numbers for node ids, and triple digit numbers for
 * segment ids
 */
public enum GraphDataSet {
    EMPTY(
            0,
            0,
            Collections.emptyList(),
            Collections.emptyList()
    ),
    SHORT_LINEAR_GRAPH(
            1,
            3,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3)
            ),
            null
    ),
    TRIANGLE_GRAPH(
            1,
            3,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3),
                    segmentWithIdConnectedToNodes(103, 1, 3)
            ),
            null
    ),
    TRIANGLE_GRAPH_WITH_A_ONE_WAY_ROUTE(
            1,
            3,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3),
                    segmentWithIdConnectedToNodes(103, 3, 1) // one way
            ),
            Arrays.asList(
                    roadInfoForSegmentId(101, false),
                    roadInfoForSegmentId(102, false),
                    roadInfoForSegmentId(103, true)
            )
    ),
    ONE_WAY_PATHS_ONLY(
            1,
            3,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3),
                    segmentWithIdConnectedToNodes(103, 1, 3)
            ),
            Arrays.asList(
                    roadInfoForSegmentId(101, true),
                    roadInfoForSegmentId(102, true),
                    roadInfoForSegmentId(103, true)
            )
    ),
    ETHANE_STRUCTURE(
            // Diagram of node ids
            //     1   2
            //     |   |
            // 3 - 4 - 5 - 6
            //     |   |
            //     7   8
            1,
            8,
            Arrays.asList(
                    // Top vertical segments (see diagram)
                    segmentWithIdConnectedToNodes(101, 1, 4),
                    segmentWithIdConnectedToNodes(102, 2, 5),
                    // Horizontal segments
                    segmentWithIdConnectedToNodes(103, 3, 4),
                    segmentWithIdConnectedToNodes(104, 4, 5),
                    segmentWithIdConnectedToNodes(105, 5, 6),
                    // Bottom vertical segments
                    segmentWithIdConnectedToNodes(106, 4, 7),
                    segmentWithIdConnectedToNodes(107, 5, 8)
            ),
            null
    ),
    ETHANOL_ALCOHOL_STRUCTURE(
            // Diagram of node ids
            //     1   2
            //     |   |
            // 3 - 4 - 5 - 6 - 7
            //     |   |
            //     8   9
            1,
            9,
            Arrays.asList(
                    // Top vertical segments (see diagram)
                    segmentWithIdConnectedToNodes(101, 1, 4),
                    segmentWithIdConnectedToNodes(102, 2, 5),
                    // Horizontal segments
                    segmentWithIdConnectedToNodes(103, 3, 4),
                    segmentWithIdConnectedToNodes(104, 4, 5),
                    segmentWithIdConnectedToNodes(105, 5, 6),
                    segmentWithIdConnectedToNodes(106, 6, 7),
                    // Bottom vertical segments
                    segmentWithIdConnectedToNodes(107, 4, 8),
                    segmentWithIdConnectedToNodes(108, 5, 9)
            ),
            null
    ),
    CIRCLE_STRUCTURE(
            1,
            5,
            Arrays.asList(
                    segmentWithIdConnectedToNodes(101, 1, 2),
                    segmentWithIdConnectedToNodes(102, 2, 3),
                    segmentWithIdConnectedToNodes(103, 3, 4),
                    segmentWithIdConnectedToNodes(104, 4, 5),
                    segmentWithIdConnectedToNodes(105, 5, 1)
            ),
            null
    ),
    WEIGHTED_TWO_DIFFERENT_LENGTH_PATHS(
            // Diagram of node ids
            //  /- 2 -\
            // 1 ----- 4
            //  \- 3 -/
            Arrays.asList(
                    // LatLongs are important for weighted test (they are used
                    // for the estimates)
                    new Node(1, new LatLong(0, 0)),
                    new Node(2, new LatLong(1, 1)),
                    new Node(3, new LatLong(1, -1)),
                    new Node(4, new LatLong(2, 0))
            ),
            Arrays.asList(
                    // Long path
                    segmentWithIdConnectedToNodesWeighted(101, 1, 2, 1.0),
                    segmentWithIdConnectedToNodesWeighted(102, 2, 4, 2.0),
                    // Short path
                    segmentWithIdConnectedToNodesWeighted(103, 1, 3, 1.0),
                    segmentWithIdConnectedToNodesWeighted(104, 3, 4, 1.0)
            ),
            null
    ),
    WEIGHTED_TWO_DIFFERENT_LENGTH_PATHS_SWAPPED(
            // Duplicate of above, with short path swapped with long
            Arrays.asList( // Locations are important for weighted test
                    new Node(1, new LatLong(0, 0)),
                    new Node(2, new LatLong(1, 1)),
                    new Node(3, new LatLong(1, -1)),
                    new Node(4, new LatLong(2, 0))
            ),
            Arrays.asList(
                    // Short path
                    segmentWithIdConnectedToNodesWeighted(101, 1, 2, 1.0),
                    segmentWithIdConnectedToNodesWeighted(102, 2, 4, 1.0),
                    // Long path
                    segmentWithIdConnectedToNodesWeighted(103, 1, 3, 1.0),
                    segmentWithIdConnectedToNodesWeighted(104, 3, 4, 2.0)
            ),
            null
    ),
    WEIGHTED_MANY_PATHS(
            // Diagram of node ids (not perfectly to scale)
            //     /- 2 -\
            //    /       \
            //   /--- 3 ---\
            //  /           \
            // 1 ---- 4 ---- 7 // shortest path
            //  \           /
            //   \--- 5 ---/
            //    \       /
            //     \- 6 -/
            Arrays.asList(
                    // Start
                    new Node(1, new LatLong(0, 0)),
                    // Middle nodes
                    new Node(2, new LatLong(1, 2)),
                    new Node(3, new LatLong(1, 1)),
                    new Node(4, new LatLong(1, 0)),
                    new Node(5, new LatLong(1, -1)),
                    new Node(6, new LatLong(1, -2)),
                    // End node
                    new Node(7, new LatLong(2, 0))
            ),
            Arrays.asList(
                    // Path 2
                    segmentWithIdConnectedToNodesWeighted(101, 1, 2, 3.0),
                    segmentWithIdConnectedToNodesWeighted(102, 2, 7, 3.0),
                    // Path 3
                    segmentWithIdConnectedToNodesWeighted(103, 1, 3, 2.0),
                    segmentWithIdConnectedToNodesWeighted(104, 3, 7, 2.0),
                    // Path 4
                    segmentWithIdConnectedToNodesWeighted(105, 1, 4, 1.0),
                    segmentWithIdConnectedToNodesWeighted(106, 4, 7, 1.0),
                    // Path 5
                    segmentWithIdConnectedToNodesWeighted(107, 1, 5, 2.0),
                    segmentWithIdConnectedToNodesWeighted(108, 5, 7, 2.0),
                    // Path 6
                    segmentWithIdConnectedToNodesWeighted(109, 1, 6, 3.0),
                    segmentWithIdConnectedToNodesWeighted(110, 6, 7, 3.0)
            ),
            null
    );


    public final List<RoadSegment> roadSegments;
    public final List<Node> nodes;
    public final List<RoadInfo> roadInfos;

    GraphDataSet(int firstNodeId,
                 int lastNodeId,
                 List<RoadSegment> roadSegments,
                 List<RoadInfo> roadInfos) {
        this(
                IntStream.range(firstNodeId, lastNodeId + 1)
                        .mapToObj(GraphDataSet::nodeWithId)
                        .collect(Collectors.toList()),
                roadSegments,
                roadInfos
        );
    }

    GraphDataSet(List<Node> nodes,
                 List<RoadSegment> roadSegments,
                 List<RoadInfo> roadInfos) {
        this.nodes = Collections.unmodifiableList(nodes);
        this.roadSegments = Collections.unmodifiableList(roadSegments);
        this.roadInfos = Collections.unmodifiableList(
                roadInfos == null ? generateBasicRoadInfo(roadSegments) :
                        roadInfos);
    }

    public RoadSegment getSegmentById(int id) {
        return roadSegments.stream()
                .filter(segment -> segment.roadId == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "RoadSegment with id " + id + " doesn't exist")
                );
    }

    public Node getNodeById(int id) {
        return nodes.stream()
                .filter(node -> node.id == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Node with id " + id + " doesn't exist")
                );
    }

    private static List<RoadInfo> generateBasicRoadInfo(
            List<RoadSegment> modelRoadSegments) {
        return modelRoadSegments.stream()
                .map(segment -> roadInfoForSegmentId(segment.roadId, null))
                .collect(Collectors.toList());
    }

    private static RoadInfo roadInfoForSegmentId(long roadId,
                                                 Boolean isOneWay) {
        return new RoadInfo(
                roadId,
                0,
                "label",
                "city",
                (isOneWay == null) ? false : isOneWay,
                RoadInfo.SpeedLimit.SL_110,
                RoadInfo.RoadClass.MAJOR_HIGHWAY,
                false,
                false,
                false
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
        return segmentWithIdConnectedToNodesWeighted(
                id, nodeId1, nodeId2, 1
        );
    }

    private static RoadSegment segmentWithIdConnectedToNodesWeighted(
            int id,
            int nodeId1,
            int nodeId2,
            double length) {
        return new RoadSegment(
                id, length, nodeId1, nodeId2, Collections.emptyList()
        );
    }
}

