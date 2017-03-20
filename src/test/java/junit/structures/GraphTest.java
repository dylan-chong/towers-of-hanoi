package junit.structures;

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
    public void createEdge_nullInfo_allowsCreation() {
        ObjGraph.Edge edge = new ObjGraph().createEdge(null, null, null);
        assertNull(edge.getInfo());
    }

    @Test
    public void createNode_nullInfo_allowsCreation() {
        ObjGraph.Node node = new ObjGraph().createNode(null);
        assertNull(node.getInfo());
    }

    @Test
    public void createEdge_withNoNodes_creationIsAllowed() {
        Graph.Edge edge = new ObjGraph()
                .createEdge(null, null, null);
        assertNull(edge.getNode1());
        assertNull(edge.getNode2());
    }

    @Test
    public void createEdge_withTwoNodes_storesNodesInOrder() {
        ObjGraph graph = new ObjGraph();
        ObjGraph.Node node1 = graph.createNode(new Object());
        ObjGraph.Node node2 = graph.createNode(new Object());
        ObjGraph.Edge edge = graph.createEdge(node1, node2, null);
        assertEquals(node1, edge.getNode1());
        assertEquals(node2, edge.getNode2());
    }

    @Test
    public void createEdge_withTwoNodes_nodesShouldConnectToEdge() {
        ObjGraph graph = new ObjGraph();
        ObjGraph.Node node1 = graph.createNode(new Object());
        ObjGraph.Node node2 = graph.createNode(new Object());
        ObjGraph.Edge edge = graph.createEdge(node1, node2, new Object());
        assertEquals(Collections.singleton(edge), new HashSet<>(node1.getEdges()));
        assertEquals(Collections.singleton(edge), new HashSet<>(node2.getEdges()));
    }

    @Test
    public void createEdge_withOneNodes_allowsCreation() {
        ObjGraph graph = new ObjGraph();
        ObjGraph.Node node = graph.createNode(new Object());
        ObjGraph.Edge edge = graph.createEdge(node, null, new Object());
        assertEquals(Collections.singleton(edge), new HashSet<>(node.getEdges()));
    }

    public static class ObjGraph extends Graph<Object, Object> {
    }
}
