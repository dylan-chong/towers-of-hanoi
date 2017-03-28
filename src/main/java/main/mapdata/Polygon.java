package main.mapdata;

import main.LatLong;

import java.awt.*;
import java.util.List;

/**
 * Created by Dylan on 18/03/17.
 */
public class Polygon {
    private final static int MIN_BRIGHTNESS = 220;
    private final static int MAX_BRIGHTNESS = 255;
    private static final int RED_REDUCTION = 10;

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

    private Color morphedColor;

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

    /**
     * Hack to make the sea look blue (because the type is not meant to
     * be used as a color)
     */
    public Color getMorphedColor() {
        if (morphedColor != null) return morphedColor;

        Color color = getColor();
        int r = morph(color.getBlue(),
                MIN_BRIGHTNESS - RED_REDUCTION,
                MAX_BRIGHTNESS - RED_REDUCTION);
        int g = morph(color.getRed(), MIN_BRIGHTNESS, MAX_BRIGHTNESS);
        int b = morph(color.getGreen(), MIN_BRIGHTNESS, MAX_BRIGHTNESS);
        morphedColor = new Color(r, g, b);
        return morphedColor;
    }

    private int morph(int colorElement, int minBrightness, int maxBrightness) {
        colorElement *= 1.5f;
        colorElement = colorElement % 255;

        float gradient = (maxBrightness - minBrightness) / 255f;
        colorElement = (int) (gradient * colorElement + minBrightness);

        return colorElement;
    }
}
