package junit;

import main.structures.Graph;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Created by Dylan on 15/03/17.
 */
public class GraphTest {

    @Test
    public void edgeConstructor_withNoNodes_creationIsAllowed() {
        Graph.Edge<Object, Object> edge = new Graph.Edge<>(null, null, null);
        assertNull(edge.getNode1());
        assertNull(edge.getNode2());
    }

    @Test
    public void edgeConstructor_withTwoNodes_storesNodesInOrder() {
        Graph.Node<Object, Object> node1 = new Graph.Node<>(null);
        Graph.Node<Object, Object> node2 = new Graph.Node<>(null);
        Graph.Edge<Object, Object> edge = new Graph.Edge<>(node1, node2, null);
        assertEquals(node1, edge.getNode1());
        assertEquals(node2, edge.getNode2());
    }

    @Test
    public void edgeConstructor_withTwoNodes_nodesShouldConnectToEdge() {
        Graph.Node<Object, Object> node1 = new Graph.Node<>(null);
        Graph.Node<Object, Object> node2 = new Graph.Node<>(null);
        Graph.Edge<Object, Object> edge = new Graph.Edge<>(node1, node2, null);
        assertEquals(Collections.singleton(edge), new HashSet<>(node1.getEdges()));
        assertEquals(Collections.singleton(edge), new HashSet<>(node2.getEdges()));
    }

    @Test
    public void edgeConstructor_withOneNodes_allowsCreation() {
        Graph.Node<Object, Object> node = new Graph.Node<>(null);
        Graph.Edge<Object, Object> edge = new Graph.Edge<>(node, null, null);
        assertEquals(Collections.singleton(edge), new HashSet<>(node.getEdges()));
    }
}
