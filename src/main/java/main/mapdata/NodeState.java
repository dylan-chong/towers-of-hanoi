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
    private double costFromStart;

    private boolean hasCheckedChildren = false;

    public NodeState(Node node,
                     boolean isStartNode,
                     CostHeuristic heuristic) {
        this.node = node;
        this.heuristic = heuristic;
        this.costFromStart = isStartNode ? 0 : Double.POSITIVE_INFINITY;
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

    public int compareTo(NodeState other, Node routeEndNode) {
        double costA = heuristic.getCostPlusEstimate(this, routeEndNode);
        double costB = heuristic.getCostPlusEstimate(other, routeEndNode);
        if (costA < costB) return -1;
        if (costA == costB) return 0;
        return 1;
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
        default double getCostFromStart(NodeState toState) {
            return toState.costFromStart;
        }

        double getCostForSegment(RoadSegment roadSegment,
                                 RoadInfo roadInfoForSegment);

        double getEstimate(Node from, Node to);
    }

    /**
     * Takes into account distance and the speed limit
     */
    public static class TimeHeuristic implements CostHeuristic {

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

    public static class DistanceHeuristic implements CostHeuristic {

        @Override
        public double getCostForSegment(RoadSegment roadSegment,
                                        RoadInfo roadInfoForSegment) {
            return roadSegment.length;
        }

        @Override
        public double getEstimate(Node from, Node to) {
            return from.latLong.estimatedDistanceInKmTo(to.latLong);
        }
    }
}

