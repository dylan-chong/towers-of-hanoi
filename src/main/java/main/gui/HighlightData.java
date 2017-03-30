package main.gui;

import main.mapdata.roads.Node;
import main.mapdata.roads.RoadSegment;
import main.mapdata.Route;

import java.util.Collection;
import java.util.Collections;

/**
 * Struct for information about what should be highlighted. Any of the fields
 * can be null, unless it is a collection, then it is empty
 */
public class HighlightData {
    /**
     * For when the user clicks on a Node
     */
    public final Collection<Node> highlightedNodes;
    /**
     * For when the user clicks on a Node
     */
    public final Collection<RoadSegment> highlightedRoadSegments;
    public final Route route;

    /**
     * Any parameter can be null
     */
    public HighlightData(Collection<Node> highlightedNodes,
                         Collection<RoadSegment> highlightedRoadSegments,
                         Route route) {
        this.highlightedNodes = emptyOrUnmodifiable(highlightedNodes);
        this.highlightedRoadSegments = emptyOrUnmodifiable(highlightedRoadSegments);
        this.route = route;
    }

    public HighlightData getNewWithRoute(Route route) {
        return new HighlightData(
                highlightedNodes,
                highlightedRoadSegments,
                route
        );
    }

    private static <T> Collection<T> emptyOrUnmodifiable(Collection<T> toWrap) {
        if (toWrap == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableCollection(toWrap);
    }
}

