package main.structures;

import main.mapdata.Node;
import main.mapdata.NodeState;
import main.mapdata.RoadSegment;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dylan on 27/03/17.
 */
public class Route {
    public final List<Node> nodes;
    public final List<RoadSegment> segments;

    public Route(List<Node> nodes, List<RoadSegment> segments) {
        this.nodes = Collections.unmodifiableList(nodes);
        this.segments = Collections.unmodifiableList(segments);
    }

    /**
     * @param lastNodeState Essentially a linked list of nodes and segments
     */
    public static Route newFromNodeState(NodeState lastNodeState) {
        LinkedList<Node> nodes = new LinkedList<>();
        LinkedList<RoadSegment> segments = new LinkedList<>();

        NodeState currentNodeState = lastNodeState;
        while (currentNodeState != null) {
            nodes.addFirst(currentNodeState.getNode());
            if (currentNodeState.getSegmentToPreviousNode() != null) {
                segments.addFirst(currentNodeState.getSegmentToPreviousNode());
            }

            // May be null
            currentNodeState = currentNodeState.getPreviousNodeState();
        }

        return new Route(nodes, segments);
    }

    /**
     * Fake route so start and end can be drawn on the map before knowing the
     * actual {@link Route}
     */
    public static Route makeFakeRoute(Node start, Node end) {
        return new Route(
                Arrays.asList(start, end),
                Collections.emptyList()
        );
    }
}
