package main.structures;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @param <NodeInfoT> The type of info stored in a {@link Node}
 * @param <EdgeInfoT> The type of info stored in an {@link Edge}
 */
public class Graph<NodeInfoT, EdgeInfoT> {
    // private HashMap<NodeInfoT, Node> nodesForNodeInfo = new HashMap<>();
    // private HashMap<EdgeInfoT, Edge> edgesForEdgeInfo = new HashMap<>();

    public Graph() {
    }

    public Edge createEdge(Node node1,
                           Node node2,
                           EdgeInfoT info) {
        return new Edge(node1, node2, info);
    }

    public Node createNode(NodeInfoT info) {
        return new Node(info);
    }

    public class Node {
        private final NodeInfoT info;

        private Collection<Edge> edges;

        private Node(NodeInfoT info) {
            this.edges = new HashSet<>();
            this.info = info;
        }

        public Collection<Edge> getEdges() {
            return Collections.unmodifiableCollection(edges);
        }

        public NodeInfoT getInfo() {
            return info;
        }

        /**
         * This is called when creating an edge
         */
        private void addEdge(Edge edge) {
            if (edges.contains(edge)) {
                throw new IllegalStateException("Already added");
            }

            edges.add(edge);
        }
    }

    public class Edge {
        private final EdgeInfoT info;
        /**
         * Either of these can be null
         */
        private final Node node1, node2;

        /**
         * Creates an Edge and connects it to two nodes (which can be null)
         */
        private Edge(Node node1,
                     Node node2,
                     EdgeInfoT info) {
            this.node1 = node1;
            this.node2 = node2;
            this.info = info;
            if (node1 != null) node1.addEdge(this);
            if (node2 != null) node2.addEdge(this);
        }

        public Node getNode1() {
            return node1;
        }

        public Node getNode2() {
            return node2;
        }

        public EdgeInfoT getInfo() {
            return info;
        }
    }
}
