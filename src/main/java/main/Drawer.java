package main;

import main.mapdata.MapData;
import main.mapdata.Node;
import main.mapdata.RoadSegment;

import java.awt.*;

/**
 * Created by Dylan on 15/03/17.
 */
public class Drawer {

    private static final int NODE_RADIUS_PX = 2;

    private static final Color ROADS_COLOR = Color.BLACK;
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
     */
    public void draw(Graphics graphics, Node highlightedNode) {
        graphics.setColor(ROADS_COLOR);

        // Optimisation ideas:
        // - Use Java shapes rather than drawOval?
        // - Clipping area (avoid drawing unnecessarily)

        mapData.roadSegments.forEach(roadSegment ->
                drawRoadSegment(graphics, roadSegment)
        );

        mapData.nodes.forEach(node -> {
            if (node == highlightedNode) graphics.setColor(NODE_HIGHLIGHT_COLOR);

            Point p = view.getPointFromLatLong(node.latLong);
            graphics.drawOval(
                    p.x - NODE_RADIUS_PX,
                    p.y - NODE_RADIUS_PX,
                    NODE_RADIUS_PX * 2,
                    NODE_RADIUS_PX * 2
            );

            if (node == highlightedNode) graphics.setColor(ROADS_COLOR);
        });
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
