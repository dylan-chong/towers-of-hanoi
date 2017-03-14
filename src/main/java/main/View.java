package main;

import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import java.awt.*;

/**
 * Created by Dylan on 14/03/17.
 *
 * Deals with scale and origin so that the map can be viewed at different
 * zoom levels and positions
 */
public class View {
    private Location origin = Location.CITY_CENTER.asLocation();
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
            dx = -1;
        } else if (move == GUI.Move.EAST) {
            dx = 1;
        }

        double dy = 0;
        if (move == GUI.Move.NORTH) {
            dy = 1;
        } else if (move == GUI.Move.SOUTH) {
            dy = -1;
        }

        origin = origin.moveBy(dx, dy);
    }

    public Point getPointFromLatLong(LatLong latLong) {
        return latLong.asPoint(
                origin,
                scale
        );
    }
}
