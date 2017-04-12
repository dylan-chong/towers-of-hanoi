package renderer;

import com.google.gson.Gson;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by Dylan on 12/04/17.
 */
public class Triangle {
    /**
     * Stored in anticlockwise order
     */
    private final Point[] points;
    private final Color color;

    public Triangle(Point[] points, Color color) {
        this.points = points;
        this.color = color;
    }

    public Triangle(Point a, Point b, Point c, Color color) {
        this.color = color;
        this.points = new Point[]{a, b, c};
    }

    public Point pointAt(int index) {
        return points[index];
    }

    public Stream<Point> points() {
        return Arrays.stream(points);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triangle triangle = (Triangle) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(points, triangle.points)) return false;
        return color != null ? color.equals(triangle.color) : triangle.color == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(points);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
