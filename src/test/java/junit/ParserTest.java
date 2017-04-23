package junit;

import org.junit.Test;
import renderer.Parser;
import renderer.Polygon;
import renderer.Scene;
import renderer.Vector3D;

import java.awt.*;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 12/04/17.
 */
public class ParserTest {
    @Test
    public void parse_noTriangles_returnsOnlyLightDirection() {
        testParse("0 1 2", new Vector3D(0, 1, 2), Collections.emptyList());
    }

    @Test
    public void parse_oneTriangle_returnsOneTriangle() {
        testParse(
                "0 1 2\n" +
                        "3 4 5 6 7 8 9 10 11 12 13 14",
                new Vector3D(0, 1, 2),
                Arrays.asList(new Polygon(
                        new Vector3D(3, 4, 5),
                        new Vector3D(6, 7, 8),
                        new Vector3D(9, 10, 11),
                        new Color(12, 13, 14)
                ))
        );
    }

    private void testParse(String input,
                           Vector3D expectedLightDirection,
                           List<Polygon> expectedTriangles) {
        Scene scene = new Parser()
                .parse(new BufferedReader(new StringReader(input)));

        assertEquals(scene.getLightDirection(), expectedLightDirection);
        assertEquals(scene.getPolygons(), expectedTriangles);
    }
}

