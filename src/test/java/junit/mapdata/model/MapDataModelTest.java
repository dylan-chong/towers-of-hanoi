package junit.mapdata.model;

import main.mapdata.location.LatLong;
import main.mapdata.model.MapDataModel;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 15/03/17.
 */
public class MapDataModelTest {

    private MapDataModelTestUtils.ModelFacadeFactory mapDataFactory =
            MapDataModelTestUtils.MAP_DATA_FACTORY;

    @Test
    public void findNodeNearLocation_withOneNodeAtExactLocation_returnsLocation() {
        LatLong expectedLatLong = new LatLong(0, 1);
        MapDataModel mapModel = mapDataFactory.create(
                () -> Collections.singletonList(
                        new Node(1, expectedLatLong)
                ),
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapModel.findNodeNearLocation(
                expectedLatLong.asLocation()
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneNodeCloseToClick_returnsLocation() {
        MapDataModel mapModel = mapDataFactory.create(
                () -> Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapModel.findNodeNearLocation(
                new LatLong(5, 8).asLocation()
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneNodeFarFromClick_returnsNodeAnyway() {
        MapDataModel mapModel = mapDataFactory.create(
                () -> Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapModel.findNodeNearLocation(
                new LatLong(50, 80).asLocation()
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneCloseNodeOneFarNode_returnsClose() {
        MapDataModel mapModel = mapDataFactory.create(
                () -> Arrays.asList(
                        new Node(1, new LatLong(4, 7)),
                        new Node(2, new LatLong(15, 15))
                ),
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapModel.findNodeNearLocation(
                new LatLong(14, 15).asLocation()
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneFarOneClose_returnsClosest() {
        MapDataModel mapModel = mapDataFactory.create(
                () -> Arrays.asList(
                        new Node(2, new LatLong(15, 15)),
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapModel.findNodeNearLocation(
                new LatLong(12, 14).asLocation()
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findRoadSegmentsByString_emptyData_returnsEmpty() {
        MapDataModel mapModel = mapDataFactory.create(
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Map<RoadInfo, Collection<RoadSegment>> roadSegments =
                mapModel.findRoadSegmentsByString(" ", Integer.MAX_VALUE);
        assertEquals(Collections.emptyMap(), roadSegments);
    }

    @Test
    public void findRouteBetween_sameNode_returnsEmptySegmentsAndOneNode() {
        GraphDataSet dataSet = GraphDataSet.A_SHORT_LINEAR_GRAPH;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Collections.singletonList(dataSet.getNodeById(1)),
                Collections.emptyList(),
                dataSet.getNodeById(1),
                dataSet.getNodeById(1),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_oneEdgeApart_findsTheEnd() {
        GraphDataSet dataSet = GraphDataSet.A_SHORT_LINEAR_GRAPH;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(2)
                ),
                Collections.singletonList(dataSet.getSegmentById(101)),
                dataSet.getNodeById(1),
                dataSet.getNodeById(2),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_twoEdgesApart_findsTheEnd() {
        GraphDataSet dataSet = GraphDataSet.A_SHORT_LINEAR_GRAPH;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                null,
                null,
                dataSet.getNodeById(1),
                dataSet.getNodeById(3),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_twoEdgesApart_findsEndWithoutReversing() {
        GraphDataSet dataSet = GraphDataSet.A_SHORT_LINEAR_GRAPH;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(2),
                        dataSet.getNodeById(3)
                ),
                Arrays.asList(
                        dataSet.getSegmentById(101),
                        dataSet.getSegmentById(102)
                ),
                dataSet.getNodeById(1),
                dataSet.getNodeById(3),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_triangleGraph_findsShortestRoute() {
        GraphDataSet dataSet = GraphDataSet.B_TRIANGLE_GRAPH;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(3)
                ),
                Collections.singletonList(
                        dataSet.getSegmentById(103)
                ),
                dataSet.getNodeById(1),
                dataSet.getNodeById(3),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_triangleGraphWithAOneWay_findsOnlyRoute() {
        GraphDataSet dataSet = GraphDataSet.C_TRIANGLE_GRAPH_WITH_A_ONE_WAY_ROUTE;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(2),
                        dataSet.getNodeById(3)
                ),
                Arrays.asList(
                        dataSet.getSegmentById(101),
                        dataSet.getSegmentById(102)
                ),
                dataSet.getNodeById(1),
                dataSet.getNodeById(3),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_oneWayBlocksAllRoutes_returnsNull() {
        GraphDataSet dataSet = GraphDataSet.D_ONE_WAY_PATHS_ONLY;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                null,
                null,
                dataSet.getNodeById(3),
                dataSet.getNodeById(1),
                dataSet,
                Assert::assertNull
        );
    }

}
