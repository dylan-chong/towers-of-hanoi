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

    private Edge getEdge(int y) {
        return edges[edgeIndex(y)];
    }

    private int edgeIndex(int y) {
        return y - startY;
    }

    private static class Edge {
        private float xLeft;
        private float zLeft;
        private float xRight;
        private float zRight;
    }
}

// code for comp261 assignment
