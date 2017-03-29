package main;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import main.mapdata.model.MapDataModel;
import main.mapdata.Node;
import main.mapdata.Polygon;
import main.mapdata.RoadSegment;
import slightlymodifiedtemplate.Location;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Dylan on 15/03/17.
 */
public class Drawer {

    private static final int NODE_RADIUS_PX = 5;
    private static final int ROUTE_NODE_RADIUS_PX = 8;

    private static final int MAX_FRAMES_PER_AVERAGE = 10;

    private static final Color ROADS_COLOR = Color.DARK_GRAY;
    private static final Color ROADS_HIGHLIGHT_COLOR = Color.RED;
    private static final Color NODE_HIGHLIGHT_COLOR = ROADS_HIGHLIGHT_COLOR;
    private static final Color ROUTE_COLOR = ROADS_HIGHLIGHT_COLOR;

    private final MapDataModel mapModel;
    private final View view;

    private List<Long> frameDurations = new ArrayList<>();

    @Inject
    private Drawer(@Assisted MapDataModel mapModel,
                   @Assisted View view) {
        this.mapModel = mapModel;
        this.view = view;
    }

    public void draw(Graphics graphics,
                     HighlightData highlightData,
                     Dimension drawAreaPixels) {
        long frameStart = System.currentTimeMillis();

        Rectangle drawArea = view.getDrawingArea(drawAreaPixels);

        // Polygons
        mapModel.data.getPolygons()
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
        mapModel.data
                .getRoadSegments()
                .parallelStream()
                .forEach(roadSegment ->
                        drawRoadSegment(graphics, roadSegment, drawArea)
                );

        // Highlighted RoadSegments
        if (highlightData.highlightedRoadSegments != null &&
                highlightData.route == null) {
            graphics.setColor(ROADS_HIGHLIGHT_COLOR);
            highlightData.highlightedRoadSegments.forEach(roadSegment ->
                    drawRoadSegment(graphics, roadSegment, drawArea)
            );
        }

        // Nodes
        if (highlightData.highlightedNode != null) {
            graphics.setColor(NODE_HIGHLIGHT_COLOR);
            drawNode(graphics, highlightData.highlightedNode, NODE_RADIUS_PX);
        }
        if (highlightData.route != null) {
            graphics.setColor(ROUTE_COLOR);
            List<Node> nodes = highlightData.route.nodes;

            // Only draw first and last node
            Arrays.asList(
                    nodes.get(0),
                    nodes.get(nodes.size() - 1)
            ).forEach(node -> drawNode(graphics, node, ROUTE_NODE_RADIUS_PX));

            highlightData.route.segments.forEach(roadSegment ->
                    drawRoadSegment(graphics, roadSegment, drawArea)
            );
        }

        long frameEnd = System.currentTimeMillis();
        frameDurations.add(frameEnd - frameStart);
        if (frameDurations.size() == MAX_FRAMES_PER_AVERAGE) {
            long averageFrameDuration = frameDurations.stream()
                    .mapToLong(l -> l)
                    .sum() / frameDurations.size();
            System.out.println("Frames took: " + averageFrameDuration + " ms");
            frameDurations.clear();
        }
    }

    private void drawNode(Graphics graphics, Node node, int radius) {
        Point p = view.getPointFromLatLong(node.latLong);
        graphics.drawOval(
                p.x - radius,
                p.y - radius,
                radius * 2,
                radius * 2
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

    public interface Factory {
        Drawer create(MapDataModel mapModel, View view);
    }
}
