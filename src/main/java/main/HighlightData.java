package main;

/**
 * Created by Dylan on 16/03/17.
 */

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

    /**
     * Any parameter can be null
     */
    public HighlightData(Node highlightedNode,
                         Collection<RoadSegment> highlightedRoadSegments) {
        this.highlightedNode = highlightedNode;
        this.highlightedRoadSegments =
                (highlightedRoadSegments == null) ? null :
                        Collections.unmodifiableCollection(highlightedRoadSegments);
    }
}

