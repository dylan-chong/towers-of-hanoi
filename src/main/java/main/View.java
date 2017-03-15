package main;

import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import java.awt.*;

/**
 * Created by Dylan on 14/03/17.
 *
 * Deals with scale and originOnScreen so that the map can be viewed at different
 * zoom levels and positions
 */
public class View {
    /**
     * A number controlling how far to move when a {@link GUI.Move} is applied.
     * Prefer using the {@link View::distanceToMoveBy()} method instead of this.
     */
    private static final int MOVE_DISTANCE = 60;

    /**
     * How close a user has to click to a {@link Point} (in screen pixels)
     * in order to select whatever is at the Point.
     */
    private static final int CLICK_RADIUS_PX = 14;

    /**
     * Adjust this so that the map can be panned around.
     */
    private Location originOnScreen = Location.CITY_CENTER.asLocation();

    /**
     * How zoomed in the map is (higher scale means closer zoom)
     */
    private double scale = 60;

    public void applyMove(GUI.Move move) {
        if (move == GUI.Move.ZOOM_IN || move == GUI.Move.ZOOM_OUT) {
            double zoomChangeFactor = 1.3; // Greater than 1 for zooming in
            if (move == GUI.Move.ZOOM_OUT) zoomChangeFactor = 1 / zoomChangeFactor;
            scale *= zoomChangeFactor;
            return;

            // TODO SOMETIME zoom in center
        }

        double dx = 0;
        if (move == GUI.Move.WEST) {
            dx = -distanceToMoveBy();
        } else if (move == GUI.Move.EAST) {
            dx = distanceToMoveBy();
        }

        double dy = 0;
        if (move == GUI.Move.NORTH) {
            dy = distanceToMoveBy();
        } else if (move == GUI.Move.SOUTH) {
            dy = -distanceToMoveBy();
        }

        originOnScreen = originOnScreen.moveBy(dx, dy);
    }

    public Point getPointFromLatLong(LatLong latLong) {
        return latLong.asPoint(
                originOnScreen,
                scale
        );
    }

    public Location getLocationFromPoint(Point point) {
        return Location.newFromPoint(
                point,
                originOnScreen,
                scale
        );
    }

    private double distanceToMoveBy() {
        return MOVE_DISTANCE / scale;
    }

    /**
     *
     * @return A value in the {@link Location} coordinate system.
     */
    public double getClickRadius() {
        // Convert CLICK_RADIUS_PX into Location units
        Point pointA = new Point(0, 0);
        Point pointB = new Point(0, CLICK_RADIUS_PX);
        Location locationA = getLocationFromPoint(pointA);
        Location locationB = getLocationFromPoint(pointB);
        return locationA.distance(locationB);
    }
}
