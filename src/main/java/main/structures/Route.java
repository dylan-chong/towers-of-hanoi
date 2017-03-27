package main.structures;

import main.mapdata.Node;
import main.mapdata.RoadSegment;

import java.util.Collections;
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
}
