package main;

import slightlymodifiedtemplate.Location;

import java.awt.*;

/**
 * Created by Dylan on 14/03/17.
 */
public class LatLong {
    public final double latitude, longitude;

    // Conversions
    private Location location;

    public LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof LatLong)) {
            return false;
        }

        LatLong otherLatLong = (LatLong) obj;
        return otherLatLong.latitude == latitude &&
                otherLatLong.longitude == longitude;
    }

    public Location asLocation() {
        if (location == null) {
            location = Location.newFromLatLong(this);
        }

        return location;
    }

    public Point asPoint(Location origin, double scale) {
        return asLocation().asPoint(origin, scale);
    }

    @Override
    public String toString() {
        return String.format(
                "{ latitude: %f, longitude: %f }",
                latitude, longitude
        );
    }
}
