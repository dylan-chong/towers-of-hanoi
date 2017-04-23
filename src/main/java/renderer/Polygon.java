package renderer;

import java.awt.*;
import java.util.Arrays;

/**
 * Polygon stores data about a single polygon in a scene, keeping track of
 * (at least!) its three vertices and its reflectance.
 * <p>
 * This class has been done for you.
 */
public class Polygon {
    private final Vector3D[] vertices;
    /**
     * The color
     */
    private final Color reflectance;

    /**
     * @param points An array of floats with 9 elements, corresponding to the
     *               (x,y,z) coordinates of the three vertices that make up
     *               this polygon. If the three vertices are A, B, C then the
     *               array should be [A_x, A_y, A_z, B_x, B_y, B_z, C_x, C_y,
     *               C_z].
     * @param color  An array of three ints corresponding to the RGB values of
     *               the polygon, i.e. [r, g, b] where all values are between 0
     *               and 255.
     */
    public Polygon(float[] points, int[] color) {
        this.vertices = new Vector3D[3];

        float x, y, z;
        for (int i = 0; i < 3; i++) {
            x = points[i * 3];
            y = points[i * 3 + 1];
            z = points[i * 3 + 2];
            this.vertices[i] = new Vector3D(x, y, z);
        }

        int r = color[0];
        int g = color[1];
        int b = color[2];
        this.reflectance = new Color(r, g, b);
    }

    /**
     * An alternative constructor that directly takes three Vector3D objects
     * and a Color object.
     */
    public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color) {
        this.vertices = new Vector3D[]{a, b, c};
        this.reflectance = color;
    }

    public Vector3D[] getVertices() {
        return vertices;
    }

    public Color getReflectance() {
        return reflectance;
    }

    /**
     * @return That cross products of two of the edges in this polygon
     */
    public Vector3D getNormal() {
        Vector3D edge2 = vertices[2].minus(vertices[1]);
        Vector3D edge1 = vertices[1].minus(vertices[0]);

        return edge1.crossProduct(edge2);
    }

    public Edge[] getEdges() {
        return new Edge[]{
                new Edge(vertices[0], vertices[1]),
                new Edge(vertices[1], vertices[2]),
                new Edge(vertices[2], vertices[0])
        };
    }

    public int getMinX() {
        return Arrays.stream(vertices)
                .mapToInt(vertex -> Math.round(vertex.y))
                .min()
                .orElseThrow(AssertionError::new);
    }

    public int getMaxX() {
        return Arrays.stream(vertices)
                .mapToInt(vertex -> Math.round(vertex.y))
                .max()
                .orElseThrow(AssertionError::new);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("polygon:");

        for (Vector3D p : vertices)
            str.append("\n  ").append(p.toString());

        str.append("\n  ").append(reflectance.toString());

        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polygon polygon = (Polygon) o;

        if (!Arrays.equals(vertices, polygon.vertices)) return false;

        if (reflectance == null) return polygon.reflectance == null;
        else return reflectance.equals(polygon.reflectance);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(vertices);
        result = 31 * result + (reflectance != null ? reflectance.hashCode() : 0);
        return result;
    }

    public static class Edge {
        public final Vector3D start, end;

        Edge(Vector3D start, Vector3D end) {
            this.start = start;
            this.end = end;
        }
    }

}

