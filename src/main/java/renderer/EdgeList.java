package renderer;

import java.util.stream.Stream;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 * <p>
 * This should act like a mutable struct, so calculations should not be here.
 */
public class EdgeList {
    private final int startY;
    private final int endY;

    /**
     * Edges where index zero returns the edge at startY
     */
    private final Edge[] edges;

    public EdgeList(int startY, int endY) {
        this.startY = startY;
        this.endY = endY;

        this.edges = Stream.generate(Edge::new)
                .limit(endY - startY + 1) // size
                .toArray(Edge[]::new);
    }

    @Override
    public String toString() {
        StringBuilder edgesStr = new StringBuilder("\n");
        for (Edge edge : edges) {
            edgesStr.append(edge.toString())
                    .append('\n');
        }

        return "EdgeList{" +
                "startY=" + startY +
                ", endY=" + endY +
                ",\nedges=" + edgesStr +
                '}';
    }

    public int getStartY() {
        return startY;
    }

    public int getEndY() {
        return endY;
    }

    public float getLeftX(int y) {
        return getEdge(y).xLeft;
    }

    public float getRightX(int y) {
        return getEdge(y).xRight;
    }

    public float getLeftZ(int y) {
        return getEdge(y).zLeft;
    }

    public float getRightZ(int y) {
        return getEdge(y).zRight;
    }

    public Vector3D getLeftColor(int y) {
        return getEdge(y).colorLeft;
    }

    public Vector3D getRightColor(int y) {
        return getEdge(y).colorRight;
    }

    public float setLeftX(int y, float x) {
        return getEdge(y).xLeft = x;
    }

    public float setRightX(int y, float x) {
        return getEdge(y).xRight = x;
    }

    public float setLeftZ(int y, float z) {
        return getEdge(y).zLeft = z;
    }

    public float setRightZ(int y, float z) {
        return getEdge(y).zRight = z;
    }

    public void setLeftColor(int y, Vector3D color) {
        if (!isValid(color))
            throw new AssertionError();
        getEdge(y).colorLeft = color;
    }

    public void setRightColor(int y, Vector3D color) {
        if (!isValid(color))
            throw new AssertionError();
        getEdge(y).colorRight = color;
    }

    private Edge getEdge(int y) {
        return edges[edgeIndex(y)];
    }

    private int edgeIndex(int y) {
        return y - startY;
    }

    private boolean isValid(Vector3D v) {
        return Stream.of(v.x, v.y, v.z)
                .noneMatch(ordinate -> ordinate < 0 || ordinate > 1);
    }

    private static class Edge {
        private float xLeft;
        private float zLeft;
        private float xRight;
        private float zRight;
        private Vector3D colorLeft; // x: red, y: green, z: blue
        private Vector3D colorRight; // x: red, y: green, z: blue

        @Override
        public String toString() {
            return String.format(
                    "Edge{xLeft=%.3f,\tzLeft=%.3f,\txRight=%.3f,\tzRight=%.3f}",
                    xLeft, zLeft, xRight, zRight
            );
        }
    }
}

// code for comp261 assignment
