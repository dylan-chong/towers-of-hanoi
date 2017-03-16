package main;

import main.mapdata.MapData;
import main.mapdata.RoadSegment;

import java.awt.*;

/**
 * Created by Dylan on 15/03/17.
 */
public class Drawer {

    private static final int NODE_RADIUS_PX = 5;

    private static final Color ROADS_COLOR = Color.BLACK;
    private static final Color ROADS_HIGHLIGHT_COLOR = Color.RED;
    private static final Color NODE_HIGHLIGHT_COLOR = Color.RED;

    private final MapData mapData;
    private final View view;

    private Drawer(MapData mapData, View view) {
        this.mapData = mapData;
        this.view = view;
    }

    public void draw(Graphics graphics,
                     HighlightData highlightData) {
        // Optimisation ideas:
        // - Clipping area (avoid drawing unnecessarily)

        // Road Segments
        graphics.setColor(ROADS_COLOR);
        mapData.roadSegments.forEach(roadSegment ->
                drawRoadSegment(graphics, roadSegment)
        );
        if (highlightData.highlightedRoadSegments != null) {
            graphics.setColor(ROADS_HIGHLIGHT_COLOR);
            highlightData.highlightedRoadSegments.forEach(roadSegment ->
                    drawRoadSegment(graphics, roadSegment)
            );
        }

        // Node
        if (highlightData.highlightedNode != null) {
            Point p = view.getPointFromLatLong(highlightData.highlightedNode.latLong);
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
                                 RoadSegment roadSegment) {
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
            previousPoint = point;
        }
    }

    public static class Factory {
        public Drawer create(MapData mapData, View view) {
            return new Drawer(mapData, view);
        }
    }
}
