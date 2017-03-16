package main.structures;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A class just used for namespacing, and allowing the inner classes to call
 * private methods on each other
 */
public class Graph {

    /**
     * No instances are required
     */
    private Graph() {
    }

    /**
     * This class represents a graph where each Node contains a class containing
     * info about the Node ({@link Graph}), and a {@link java.util.Set} of
     * {@link Edge} objects.
     *
     * @param <NodeInfoType> Type to store information in this {@link Node}
     * @param <EdgeInfoType> Type to store information in an {@link Edge} that is
     *                       connected to this {@link Node}
     */
    public static class Node<NodeInfoType, EdgeInfoType> {
        public final NodeInfoType info;

        private Collection<Edge<NodeInfoType, EdgeInfoType>> edges;

        public Node(NodeInfoType info) {
            this.edges = new HashSet<>();
            this.info = info;
        }

        public Collection<Edge<NodeInfoType, EdgeInfoType>> getEdges() {
            return Collections.unmodifiableCollection(edges);
        }

        /**
         * This is called when creating an edge
         */
        private void addEdge(Edge<NodeInfoType, EdgeInfoType> edge) {
            if (edges.contains(edge)) {
                throw new IllegalStateException("Already added");
            }

            edges.add(edge);
        }
    }

    /**
     * @param <NodeInfoType> The type of info stored in a {@link Graph} connected
     *                       to this {@link Edge}
     * @param <EdgeInfoType> The type of info stored this {@link Edge}
     */
    public static class Edge<NodeInfoType, EdgeInfoType> {
        public final EdgeInfoType info;
        /**
         * Either of these can be null
         */
        public final Node<NodeInfoType, EdgeInfoType> node1, node2;

        /**
         * Creates an Edge and connects it to two nodes (which can be null)
         */
        public Edge(Node<NodeInfoType, EdgeInfoType> node1,
                    Node<NodeInfoType, EdgeInfoType> node2,
                    EdgeInfoType info) {
            this.node1 = node1;
            this.node2 = node2;
            this.info = info;
            if (node1 != null) node1.addEdge(this);
            if (node2 != null) node2.addEdge(this);
        }

        public Node<NodeInfoType, EdgeInfoType> getNode1() {
            return node1;
        }

        public Node<NodeInfoType, EdgeInfoType> getNode2() {
            return node2;
        }
    }
}
