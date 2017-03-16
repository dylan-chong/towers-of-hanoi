package junit;

import main.structures.Graph;
import main.mapdata.MapGraph;
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
        MapGraph.Edge edge = new MapGraph().createEdge(null, null, null);
        assertNull(edge.getInfo());
    }

    @Test
    public void createNode_nullInfo_allowsCreation() {
        MapGraph.Node node = new MapGraph().createNode(null);
        assertNull(node.getInfo());
    }

    @Test
    public void createEdge_withNoNodes_creationIsAllowed() {
        Graph.Edge edge = new MapGraph()
                .createEdge(null, null, null);
        assertNull(edge.getNode1());
        assertNull(edge.getNode2());
    }

    @Test
    public void createEdge_withTwoNodes_storesNodesInOrder() {
        MapGraph graph = new MapGraph();
        MapGraph.Node node1 = graph.createNode(null);
        MapGraph.Node node2 = graph.createNode(null);
        MapGraph.Edge edge = graph.createEdge(node1, node2, null);
        assertEquals(node1, edge.getNode1());
        assertEquals(node2, edge.getNode2());
    }

    @Test
    public void createEdge_withTwoNodes_nodesShouldConnectToEdge() {
        MapGraph graph = new MapGraph();
        MapGraph.Node node1 = graph.createNode(null);
        MapGraph.Node node2 = graph.createNode(null);
        MapGraph.Edge edge = graph.createEdge(node1, node2, null);
        assertEquals(Collections.singleton(edge), new HashSet<>(node1.getEdges()));
        assertEquals(Collections.singleton(edge), new HashSet<>(node2.getEdges()));
    }

    @Test
    public void createEdge_withOneNodes_allowsCreation() {
        MapGraph graph = new MapGraph();
        MapGraph.Node node = graph.createNode(null);
        MapGraph.Edge edge = graph.createEdge(node, null, null);
        assertEquals(Collections.singleton(edge), new HashSet<>(node.getEdges()));
    }
}
