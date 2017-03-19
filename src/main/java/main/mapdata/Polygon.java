package main.mapdata;

import main.LatLong;

import java.awt.*;
import java.util.List;

/**
 * Created by Dylan on 18/03/17.
 */
public class Polygon {
    /**
     * Also represents the colour
     */
    public final String type;
    /**
     * Can be null
     */
    public final String label;
    public final List<LatLong> latLongs;
    public final Integer endLevel;
    public final Integer cityIdx;

    private int[] xPoints, yPoints;
    private Color color;

    public Polygon(String type,
                   String label,
                   List<LatLong> points,
                   Integer endLevel,
                   Integer cityIdx) {
        this.type = type;
        this.label = label;
        this.latLongs = points;
        this.endLevel = endLevel;
        this.cityIdx = cityIdx;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof Polygon)) {
            return false;
        }

        Polygon other = (Polygon) obj;

        return other.type.equals(type) &&
                equalsOrBothNull(other.label, label) &&
                other.latLongs.equals(latLongs) &&
                other.endLevel.equals(endLevel) &&
                other.cityIdx.equals(cityIdx);
    }

    private boolean equalsOrBothNull(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false; // Only 1 is null

        return a.equals(b);
    }

    public Color getColor() {
        int color8Bit = Integer.decode(type);
        float r = ((color8Bit & 0b11100000) >> 5) / 7f;
        float g = ((color8Bit & 0b00011100) >> 2) / 7f;
        float b = (color8Bit & 0b00000011) / 3f;
        return new Color(r, g, b);
    }
}
