package main;

import main.mapdata.Node;
import main.mapdata.RoadSegment;

import java.util.Collection;
import java.util.Collections;

/**
 * Struct for information about what should be highlighted
 */
public class HighlightData {
    /**
     * For when the user clicks on a Node
     */
    public final Node highlightedNode;
    /**
     * For when the user clicks on a Node
     */
    public final Collection<RoadSegment> highlightedRoadSegments;
    public final Node routeStart;
    public final Node routeEnd;

    /**
     * Any parameter can be null
     */
    public HighlightData(Node highlightedNode,
                         Collection<RoadSegment> highlightedRoadSegments,
                         Node routeStart,
                         Node routeEnd) {
        this.highlightedNode = highlightedNode;
        this.routeStart = routeStart;
        this.routeEnd = routeEnd;

        if (highlightedRoadSegments == null) {
            this.highlightedRoadSegments = null;
        } else {
            this.highlightedRoadSegments = Collections.unmodifiableCollection(
                    highlightedRoadSegments
            );
        }
    }

    public HighlightData getNewWithRoute(Node routeStart, Node routeEnd) {
        return new HighlightData(
                highlightedNode,
                highlightedRoadSegments,
                routeStart,
                routeEnd
        );
    }
}

