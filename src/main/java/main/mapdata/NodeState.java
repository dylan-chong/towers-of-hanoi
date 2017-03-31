package main.mapdata;

import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;

/**
 * Used for route-finding using Djikstra/A* algorithm. This represents
 * one item in the {@link java.util.PriorityQueue}
 */
public class NodeState {
    private final Node node;

    private RoadSegment segmentToPreviousNode;
    private RoadInfo roadInfoForSegment;
    private NodeState previousNodeState; // Linked list as a path

    private boolean hasCheckedChildren = false;
    private double timeFromStart;

    // public final boolean hasCheckedChildren;

    public NodeState(Node node,
                     RoadSegment segmentToPreviousNode,
                     RoadInfo roadInfoForSegment,
                     NodeState previousNodeState,
                     double timeFromStart) {
        this.node = node;
        this.segmentToPreviousNode = segmentToPreviousNode;
        this.roadInfoForSegment = roadInfoForSegment;
        this.previousNodeState = previousNodeState;
        this.timeFromStart = timeFromStart;
    }

    /**
     * Takes into account the distance and speed so far, and the estimated
     * best cost for getting to the end
     */
    public double getPriority(Node routeEndNode) {
        double estimateDistanceToEnd = node.latLong.estimatedDistanceInKmTo(
                routeEndNode.latLong
        );

        double speed = roadInfoForSegment == null ?
                RoadInfo.SpeedLimit.getFastest().speedKMpH :
               roadInfoForSegment.speedLimit.speedKMpH;
        return timeFromStart + (estimateDistanceToEnd / speed);
    }

    public Node getNode() {
        return node;
    }

    public RoadSegment getSegmentToPreviousNode() {
        return segmentToPreviousNode;
    }

    public NodeState getPreviousNodeState() {
        return previousNodeState;
    }

    public double getTimeFromStart() {
        return timeFromStart;
    }

    public void setTimeFromStart(double timeFromStart) {
        this.timeFromStart = timeFromStart;
    }

    public boolean hasCheckedChildren() {
        return hasCheckedChildren;
    }

    public void setHasCheckedChildren(boolean hasCheckedChildren) {
        this.hasCheckedChildren = hasCheckedChildren;
    }

    public void setSegmentToPreviousNode(RoadSegment segmentToPreviousNode) {
        this.segmentToPreviousNode = segmentToPreviousNode;
    }

    public void setPreviousNodeState(NodeState previousNodeState) {
        this.previousNodeState = previousNodeState;
    }
}
