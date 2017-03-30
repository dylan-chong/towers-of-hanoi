package main.mapdata.location;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 26/03/17.
 */
public class Rectangle {
    public final Location bottomLeft, topRight;

    /**
     * @param bottomLeft Low x, high y
     * @param topRight High x, low y
     */
    public Rectangle(Location bottomLeft, Location topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    /**
     * @return true if any of the points is this {@link Rectangle} or if
     *         any of the lines between any two points could cross through
     *         this.
     */
    public boolean couldPolygonBeInside(List<Location> polygonVertices) {
        if (polygonVertices.isEmpty()) return false;

        if (polygonVertices.stream()
                .allMatch(location -> location.x < bottomLeft.x)) {
            return false;
        }
        if (polygonVertices.stream()
                .allMatch(location -> location.x > topRight.x)) {
            return false;
        }
        if (polygonVertices.stream()
                .allMatch(location -> location.y > bottomLeft.y)) {
            return false;
        }
        if (polygonVertices.stream()
                .allMatch(location -> location.y < topRight.y)) {
            return false;
        }

        return true;
    }

    public boolean couldCircleBeInside(Location location, double radius) {
        return couldPolygonBeInside(Arrays.asList(
                new Location(location.x - radius, location.y - radius),
                new Location(location.x + radius, location.y - radius),
                new Location(location.x + radius, location.y + radius),
                new Location(location.x - radius, location.y + radius)
        ));
    }
}
