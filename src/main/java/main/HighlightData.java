package main;

import main.mapdata.Node;
import main.mapdata.RoadSegment;
import main.structures.Route;

import java.util.Collection;
import java.util.Collections;

/**
 * Struct for information about what should be highlighted. Any of the fields
 * can be null
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
    public final Route route;

    /**
     * Any parameter can be null
     */
    public HighlightData(Node highlightedNode,
                         Collection<RoadSegment> highlightedRoadSegments,
                         Route route) {
        this.highlightedNode = highlightedNode;
        this.route = route;

        if (highlightedRoadSegments == null) {
            this.highlightedRoadSegments = null;
        } else {
            this.highlightedRoadSegments = Collections.unmodifiableCollection(
                    highlightedRoadSegments
            );
        }
    }

    public HighlightData getNewWithRoute(Route route) {
        return new HighlightData(
                highlightedNode,
                highlightedRoadSegments,
                route
        );
    }
}

