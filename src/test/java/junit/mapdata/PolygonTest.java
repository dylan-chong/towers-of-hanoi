package junit.mapdata;

import main.mapdata.Polygon;
import org.junit.Test;

import java.awt.*;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 19/03/17.
 *
 * Test loading 8 bit hexadecimal strings as colours
 */
public class PolygonTest {
    @Test
    public void getColor_0x0_returnsBlack() {
        Polygon polygon = newPolygon("0x0");
        assertEquals(Color.BLACK, polygon.getColor());
    }

    @Test
    public void getColor_0xff_returnsWhite() {
        Polygon polygon = newPolygon("0xff");
        assertEquals(Color.WHITE, polygon.getColor());
    }

    @Test
    public void getColor_redHex_returnsRed() {
        Polygon polygon = newPolygon("0xe0");
        assertEquals(Color.RED, polygon.getColor());
    }

    @Test
    public void getColor_greenHex_returnsGreen() {
        Polygon polygon = newPolygon("0x1c");
        assertEquals(Color.GREEN, polygon.getColor());
    }

    @Test
    public void getColor_blueHex_returnsBlue() {
        Polygon polygon = newPolygon("0x3");
        assertEquals(Color.BLUE, polygon.getColor());
    }

    @Test
    public void getColor_dullPurple_returnsDullPurple() {
        // From binary 8 bit code: 0b10010010
        Polygon polygon = newPolygon("0x92");
        assertEquals(
                new Color(
                        4f / 7, // Taken from binary code
                        4f / 7,
                        2f / 3
                ),
                polygon.getColor()
        );
    }

    private Polygon newPolygon(String hexColor) {
        return new Polygon(
                hexColor,
                "",
                Collections.emptyList(),
                0,
                0
        );
    }
}
