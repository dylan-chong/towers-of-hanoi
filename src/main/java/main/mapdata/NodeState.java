package main.mapdata;

import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;

/**
 * Used for route-finding using Dijkstra/A* algorithm. This represents
 * one item in the {@link java.util.PriorityQueue}
 */
public class NodeState {
    private final Node node;
    private final CostHeuristic heuristic;

    private RoadSegment segmentToPreviousNode;
    private NodeState previousNodeState; // Linked list as a path
    private double costFromStart = Double.POSITIVE_INFINITY;

    private boolean hasCheckedChildren = false;

    /**
     *
     * @param segmentToPreviousNode Set to null if this is the start node
     * @param roadInfoForSegment Set to null if this is the start node
     * @param previousNodeState Set to null if this is the start node
     */
    public NodeState(Node node,
                     RoadSegment segmentToPreviousNode,
                     RoadInfo roadInfoForSegment,
                     NodeState previousNodeState,
                     CostHeuristic heuristic) {
        this.node = node;
        this.heuristic = heuristic;

        if (previousNodeState == null) {
            costFromStart = 0;
            return;
        }
        setPreviousNodeState(
                previousNodeState, segmentToPreviousNode, roadInfoForSegment
        );
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

    public boolean hasCheckedChildren() {
        return hasCheckedChildren;
    }

    public void setHasCheckedChildren(boolean hasCheckedChildren) {
        this.hasCheckedChildren = hasCheckedChildren;
    }

    public void setPreviousNodeState(NodeState previousNodeState,
                                     RoadSegment segmentToPreviousNode,
                                     RoadInfo roadInfoForSegment) {
        this.previousNodeState = previousNodeState;
        this.segmentToPreviousNode = segmentToPreviousNode;

        costFromStart = heuristic.getCostFromStart(previousNodeState) +
                heuristic.getCostForSegment(segmentToPreviousNode,
                        roadInfoForSegment);
    }

    /**
     * Strategy pattern for calculating cost and estimate. Implementations
     * should be admissible and consistent. They should also be stateless
     */
    public interface CostHeuristic {
        /**
         * Used as the priority for A* search.
         */
        default double getCostPlusEstimate(NodeState state, Node endNode) {
            return getCostFromStart(state) + getEstimate(state.node, endNode);
        }

        /**
         * The cost should be saved in the {@link NodeState} for efficiency,
         * but this method is in this interface to be consistent with other
         * methods
         */
        double getCostFromStart(NodeState toState);

        double getCostForSegment(RoadSegment roadSegment,
                                 RoadInfo roadInfoForSegment);

        double getEstimate(Node from, Node to);
    }

    /**
     * Takes into account distance and the speed limit
     */
    public static class TimeHeuristic implements CostHeuristic {
        @Override
        public double getCostFromStart(NodeState toState) {
            return toState.costFromStart;
        }

        @Override
        public double getCostForSegment(RoadSegment roadSegment,
                                        RoadInfo roadInfoForSegment) {
            double distance = roadSegment.length;
            double speed = roadInfoForSegment.speedLimit.speedKMpH;
            return distance / speed;
        }

        @Override
        public double getEstimate(Node from, Node to) {
            double estimateDistanceToEnd = from.latLong.estimatedDistanceInKmTo(
                    to.latLong
            );
            double speed = RoadInfo.SpeedLimit.getFastest().speedKMpH;
            return estimateDistanceToEnd / speed;
        }
    }

}

