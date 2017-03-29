package main.mapdata;

import main.mapdata.roads.Node;
import main.mapdata.roads.RoadSegment;

/**
 * Used for route-finding using Djikstra/A* algorithm. This represents
 * one item in the {@link java.util.PriorityQueue}
 */
public class NodeState {
    private final Node node;

    private RoadSegment segmentToPreviousNode;
    private NodeState previousNodeState; // Linked list as a path

    private boolean hasCheckedChildren = false;
    private double distanceFromStart;

    // public final boolean hasCheckedChildren;

    public NodeState(Node node,
                     RoadSegment segmentToPreviousNode,
                     NodeState previousNodeState,
                     double distanceFromStart) {
        this.node = node;
        this.segmentToPreviousNode = segmentToPreviousNode;
        this.previousNodeState = previousNodeState;
        this.distanceFromStart = distanceFromStart;
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

    public double getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
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
