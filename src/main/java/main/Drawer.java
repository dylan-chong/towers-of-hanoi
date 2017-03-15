package main;

import main.mapdata.MapData;
import main.mapdata.RoadSegment;

import java.awt.*;

/**
 * Created by Dylan on 15/03/17.
 */
public class Drawer {

    private static final int NODE_RADIUS_PX = 2;

    private final MapData mapData;
    private final View view;

    private Drawer(MapData mapData, View view) {
        this.mapData = mapData;
        this.view = view;
    }

    /**
     * @param graphics Draws a single frame onto graphics
     */
    public void draw(Graphics graphics) {
        graphics.setColor(Color.BLACK);

        // Optimisation ideas:
        // - Use Java shapes rather than drawOval?
        // - Clipping area (avoid drawing unnecessarily)

        mapData.nodes.forEach(node -> {
            Point p = view.getPointFromLatLong(node.latLong);
            graphics.drawOval(
                    p.x - NODE_RADIUS_PX,
                    p.y - NODE_RADIUS_PX,
                    NODE_RADIUS_PX * 2,
                    NODE_RADIUS_PX * 2
            );
        });

        mapData.roadSegments.forEach(roadSegment ->
                drawRoadSegment(graphics, roadSegment)
        );
    }

    private void drawRoadSegment(Graphics graphics, RoadSegment roadSegment) {
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
