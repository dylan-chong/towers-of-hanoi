package main;

import main.mapdata.MapData;
import main.mapdata.Node;
import main.mapdata.Polygon;
import main.mapdata.RoadSegment;
import slightlymodifiedtemplate.Location;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Dylan on 15/03/17.
 */
public class Drawer {

    private static final int NODE_RADIUS_PX = 5;

    private static final Color ROADS_COLOR = Color.DARK_GRAY;
    private static final Color ROADS_HIGHLIGHT_COLOR = Color.RED;
    private static final Color NODE_HIGHLIGHT_COLOR = Color.RED;

    private final MapData mapData;
    private final View view;

    private Drawer(MapData mapData, View view) {
        this.mapData = mapData;
        this.view = view;
    }

    public void draw(Graphics graphics,
                     HighlightData highlightData,
                     Dimension drawAreaPixels) {
        Rectangle drawArea = view.getDrawingArea(drawAreaPixels);

        // Polygons
        mapData.getPolygons()
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEachOrdered(entry ->
                        entry.getValue().forEach(polygon ->
                                drawPolygon(graphics, polygon, drawArea)
                        )
                );

        // RoadSegments
        graphics.setColor(ROADS_COLOR);
        mapData.getRoadSegments().forEach(roadSegment ->
                drawRoadSegment(graphics, roadSegment, drawArea)
        );

        // Highlighted RoadSegments
        if (highlightData.highlightedRoadSegments != null) {
            graphics.setColor(ROADS_HIGHLIGHT_COLOR);
            highlightData.highlightedRoadSegments.forEach(roadSegment ->
                    drawRoadSegment(graphics, roadSegment, drawArea)
            );
        }

        // Node
        if (highlightData.highlightedNode != null) {
            drawNode(graphics, highlightData.highlightedNode);
        }
    }

    private void drawNode(Graphics graphics, Node node) {
        Point p = view.getPointFromLatLong(node.latLong);
        graphics.setColor(NODE_HIGHLIGHT_COLOR);
        graphics.drawOval(
                p.x - NODE_RADIUS_PX,
                p.y - NODE_RADIUS_PX,
                NODE_RADIUS_PX * 2,
                NODE_RADIUS_PX * 2
        );
    }

    private void drawRoadSegment(Graphics graphics,
                                 RoadSegment roadSegment,
                                 Rectangle drawArea) {

        // Draw pairs of latLongs
        LatLong previousPoint = null;
        for (LatLong point : roadSegment.points) {
            if (previousPoint == null) {
                previousPoint = point;
                continue;
            }

            if (!drawArea.couldPolygonBeInside(
                    Stream.of(point, previousPoint)
                            .map(LatLong::asLocation)
                            .collect(Collectors.toList()))
                    ) {
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

    private void drawPolygon(Graphics graphics,
                             Polygon polygon,
                             Rectangle drawArea) {
        graphics.setColor(polygon.getMorphedColor());

        List<Location> locations = polygon.latLongs.stream()
                .map(LatLong::asLocation)
                .collect(Collectors.toList());
        if (!drawArea.couldPolygonBeInside(locations)) {
            // All points are outside the draw area
            return;
        }

        List<Point> points = polygon.latLongs
                .stream()
                .map(view::getPointFromLatLong)
                .collect(Collectors.toList());
        int[] xPoints = points.stream()
                .mapToInt(p -> p.x)
                .toArray();
        int[] yPoints = points.stream()
                .mapToInt(p -> p.y)
                .toArray();

        graphics.fillPolygon(xPoints, yPoints, points.size());
    }

    public static class Factory {
        public Drawer create(MapData mapData, View view) {
            return new Drawer(mapData, view);
        }
    }
}
