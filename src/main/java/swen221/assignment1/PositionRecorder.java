package swen221.assignment1;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dylan on 21/03/17.
 */
public class PositionRecorder {

    private Point currentPoint;

    private Map<Point, Set<DirectionUseful>> travelledDirectionsFromPoint
            = new HashMap<>();
    private Set<DirectionUseful> usedDirs;

    public PositionRecorder(Point p) {
        currentPoint = p;
    }

    public PositionRecorder() {
        currentPoint = new Point(0, 0);
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void move(DirectionUseful direction) {
        int dx = 0, dy = 0;
        switch (direction) {
            case WEST:
                dx = -1;
                break;
            case EAST:
                dx = 1;
                break;
            case NORTH:
                dy = 1;
                break;
            case SOUTH:
                dy = -1;
                break;
        }

        Set<DirectionUseful> travelledDirections = travelledDirectionsFromPoint
                .computeIfAbsent(currentPoint, p -> new HashSet<>());
        travelledDirections.add(direction);

        currentPoint = new Point(currentPoint.x + dx, currentPoint.y + dy);
    }


    public Set<DirectionUseful> getUsedDirs() {
        return travelledDirectionsFromPoint.computeIfAbsent(
                currentPoint,
                point -> new HashSet<>()
        );
    }
}
