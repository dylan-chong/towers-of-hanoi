package junit;

import org.junit.Test;
import renderer.Pipeline;
import renderer.Polygon;
import renderer.Vector3D;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 29/04/17.
 */
public class GoraundTests {
    @Test
    public void getPolygonsConnectedToVertex_onePolygon_returnsPoly() {
        Vector3D vertex = new Vector3D(1, 2, 3);
        Polygon polygon = new Polygon(
                vertex,
                new Vector3D(4, 5, 6),
                new Vector3D(7, 8, 9),
                new Color(0, 0, 0)
        );
        Collection<Polygon> polygonsConnectedToVertex =
                Pipeline.getPolygonsConnectedToVertex(
                        Collections.singletonList(polygon),
                        vertex
                );
        assertEquals(
                new HashSet<>(polygonsConnectedToVertex),
                new HashSet<>(Arrays.asList(polygon))
        );
    }

    @Test
    public void getPolygonsConnectedToVertex_vertexNotNearPoly_returnsEmpty() {
        Vector3D vertex = new Vector3D(10, 2, 3);
        Polygon polygon = new Polygon(
                new Vector3D(1, 2, 3),
                new Vector3D(4, 5, 6),
                new Vector3D(7, 8, 9),
                new Color(0, 0, 0)
        );
        Collection<Polygon> polygonsConnectedToVertex =
                Pipeline.getPolygonsConnectedToVertex(
                        Collections.singletonList(polygon),
                        vertex
                );
        assertEquals(
                new HashSet<>(polygonsConnectedToVertex),
                new HashSet<>()
        );
    }

    @Test
    public void getPolygonsConnectedToVertex_twoPolygons_returnsBoth() {
        Vector3D vertex = new Vector3D(1, 2, 3);
        Polygon polygon1 = new Polygon(
                vertex,
                new Vector3D(4, 5, 6),
                new Vector3D(7, 8, 9),
                new Color(0, 0, 0)
        );
        Polygon polygon2 = new Polygon(
                vertex,
                new Vector3D(40, 5, 6),
                new Vector3D(7, 8, 9),
                new Color(0, 0, 0)
        );
        Collection<Polygon> polygonsConnectedToVertex =
                Pipeline.getPolygonsConnectedToVertex(
                        Arrays.asList(polygon1, polygon2),
                        vertex
                );
        assertEquals(
                new HashSet<>(polygonsConnectedToVertex),
                new HashSet<>(Arrays.asList(polygon1, polygon2))
        );
    }
}
