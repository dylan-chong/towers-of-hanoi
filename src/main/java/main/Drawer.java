package main;

import main.mapdata.MapData;
import main.mapdata.Node;
import main.mapdata.RoadInfo;
import main.mapdata.RoadSegment;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Dylan on 15/03/17.
 */
public class Drawer {

    private static final int NODE_RADIUS_PX = 2;

    private static final Color ROADS_COLOR = Color.BLACK;
    private static final Color ROADS_HIGHLIGHT_COLOR = Color.RED;
    private static final Color NODE_HIGHLIGHT_COLOR = Color.RED;

    private final MapData mapData;
    private final View view;

    private Drawer(MapData mapData, View view) {
        this.mapData = mapData;
        this.view = view;
    }

    /**
     * @param graphics Draws a single frame onto graphics
     * @param highlightedNode The node that has been clicked by the user
     * @param searchResults Contains {@link RoadSegment} objects to highlight
     */
    public void draw(Graphics graphics,
                     Node highlightedNode,
                     Map<RoadInfo, Collection<RoadSegment>> searchResults) {
        graphics.setColor(ROADS_COLOR);

        // Optimisation ideas:
        // - Clipping area (avoid drawing unnecessarily)

        mapData.roadSegments.forEach(roadSegment ->
                drawRoadSegment(graphics, roadSegment, false)
        );
        if (searchResults != null) {
            graphics.setColor(ROADS_HIGHLIGHT_COLOR);
            searchResults.forEach((roadInfo, roadSegments) ->
                    roadSegments.forEach(roadSegment ->
                            drawRoadSegment(graphics, roadSegment, true)
                    )
            );
        }

        if (highlightedNode != null) {
            Point p = view.getPointFromLatLong(highlightedNode.latLong);
            graphics.setColor(NODE_HIGHLIGHT_COLOR);
            graphics.drawOval(
                    p.x - NODE_RADIUS_PX,
                    p.y - NODE_RADIUS_PX,
                    NODE_RADIUS_PX * 2,
                    NODE_RADIUS_PX * 2
            );
        }
    }

    private void drawRoadSegment(Graphics graphics,
                                 RoadSegment roadSegment,
                                 boolean shouldHighlight) {
        // Draw pairs of latLongs
        LatLong previousPoint = null;
        for (LatLong point : roadSegment.points) {
            if (previousPoint == null) {
                previousPoint = point;
                continue;
            }

            Point previousPointLocation = view.getPointFromLatLong(previousPoint);
            Point pointLocation = view.getPointFromLatLong(point);
            graphics.drawLine(
                    previousPointLocation.x,
                    previousPointLocation.y,
                    pointLocation.x,
                    pointLocation.y
            );
        }
    }

    public static class Factory {
        public Drawer create(MapData mapData, View view) {
            return new Drawer(mapData, view);
        }
    }
}
