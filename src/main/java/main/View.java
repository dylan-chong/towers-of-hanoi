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

    private static final double ZOOM_CHANGE_FACTOR = 1.1;

    /**
     * Adjust this so that the map can be panned around.
     */
    private Location originOnScreen = Location.CITY_CENTER.asLocation();

    /**
     * How zoomed in the map is (higher scale means closer zoom)
     */
    private double scale = 60;

    public void applyMove(GUI.Move move, Dimension componentDimension) {
        if (move == GUI.Move.ZOOM_IN || move == GUI.Move.ZOOM_OUT) {
            double oldScale = scale;

            double zoomChangeFactor = ZOOM_CHANGE_FACTOR; // Greater than 1 for zooming in
            if (move == GUI.Move.ZOOM_OUT) zoomChangeFactor = 1 / zoomChangeFactor;
            scale *= zoomChangeFactor;

            // Make the zoom from the center by shifting the origin

            // Convert from Point coordinate system to Location
            double componentLWidth = convertFromPointSystemToLocationSystem(
                    componentDimension.width
            );
            double componentLHeight = convertFromPointSystemToLocationSystem(
                    componentDimension.height
            );

            originOnScreen = new Location(
                    originOnScreen.x -
                            (componentLWidth / 2.0 * (1 - scale / oldScale)),
                    originOnScreen.y +
                            (componentLHeight / 2.0 * (1 - scale / oldScale))
            );
            return;
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

    /**
     * @param dxPointCoord How far the mouse moved in the {@link Point}
     *                     coordinate system
     * @param dyPointCoord In the {@link Point} coordinate system
     */
    public void applyDrag(int dxPointCoord, int dyPointCoord) {
        // Convert the Location coordinate system
        double dxLocation = convertFromPointSystemToLocationSystem(dxPointCoord);
        double dyLocation = convertFromPointSystemToLocationSystem(dyPointCoord);
        originOnScreen = originOnScreen.moveBy(-dxLocation, dyLocation);
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

    private double convertFromPointSystemToLocationSystem(int points) {
        Location fakeStart = Location.newFromPoint(
                new Point(0, 0),
                originOnScreen,
                scale
        );
        Location fakeEnd = Location.newFromPoint(
                new Point(points, 0),
                originOnScreen,
                scale
        );
        double distance = fakeStart.distance(fakeEnd);
        if (points < 0) distance *= -1;
        return distance;
    }

    private double distanceToMoveBy() {
        return MOVE_DISTANCE / scale;
    }
}
