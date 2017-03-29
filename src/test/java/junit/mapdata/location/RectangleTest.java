package junit.mapdata.location;

import main.mapdata.location.Rectangle;
import org.junit.Test;
import main.mapdata.location.Location;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 26/03/17.
 */
public class RectangleTest {
    @Test
    public void couldPolygonBeInside_emptyList_returnsFalse() {
        testCouldPolygonBeInside(Arrays.asList(), false);
    }

    @Test
    public void couldPolygonBeInside_onePointInside_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(5, 2)
        ), true);
    }

    @Test
    public void couldPolygonBeInside_onePointOutsideRight_returnsFalse() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(500, 2)
        ), false);
    }

    @Test
    public void couldPolygonBeInside_onePointOutsideLeft_returnsFalse() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(-500, 2)
        ), false);
    }

    @Test
    public void couldPolygonBeInside_onePointOutsideUp_returnsFalse() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(5, -200)
        ), false);
    }

    @Test
    public void couldPolygonBeInside_onePointOutsideDown_returnsFalse() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(5, 200)
        ), false);
    }

    @Test
    public void couldPolygonBeInside_oneOutsideOneInside_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(500, 2),
                new Location(5, 2)
        ), true);
    }

    @Test
    public void couldPolygonBeInside_lineThatGoesThrough_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(500, 2),
                new Location(-500, 2)
        ), true);
    }

    @Test
    public void couldPolygonBeInside_lineThatDoesntGoThrough_returnsFalse() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(500, 2),
                new Location(600, 2)
        ), false);
    }

    @Test
    public void couldPolygonBeInside_triangleInside_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(4, 1),
                new Location(7, 2),
                new Location(5, 3)
        ), true);
    }

    @Test
    public void couldPolygonBeInside_trianglePartiallyInside_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(4, -10),
                new Location(7, 2),
                new Location(5, 3)
        ), true);
    }

    @Test
    public void couldPolygonBeInside_triangleAround_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(5, -10),
                new Location(-100, 6),
                new Location(100, 6)
        ), true);
    }

    @Test
    public void couldPolygonBeInside_2triangleAround_returnsTrue() {
        testCouldPolygonBeInside(Arrays.asList(
                new Location(5, -10),
                new Location(-100, 6),
                new Location(100, 6)
        ), true);
    }

    private void testCouldPolygonBeInside(List<Location> polygonVertices,
                                          boolean expected) {
        Rectangle rectangle = new Rectangle(
                new Location(3, 5),
                new Location(8, 1)
        );
        assertEquals(expected, rectangle.couldPolygonBeInside(polygonVertices));
    }
}
