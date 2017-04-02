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

    private MapDataModelTestUtils.ModelFactoryFacade mapDataFactory =
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
        GraphDataSet dataSet = GraphDataSet.SHORT_LINEAR_GRAPH;
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
        GraphDataSet dataSet = GraphDataSet.SHORT_LINEAR_GRAPH;
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
        GraphDataSet dataSet = GraphDataSet.SHORT_LINEAR_GRAPH;
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
        GraphDataSet dataSet = GraphDataSet.SHORT_LINEAR_GRAPH;
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
        GraphDataSet dataSet = GraphDataSet.TRIANGLE_GRAPH;
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
        GraphDataSet dataSet = GraphDataSet.TRIANGLE_GRAPH_WITH_A_ONE_WAY_ROUTE;
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
        GraphDataSet dataSet = GraphDataSet.ONE_WAY_PATHS_ONLY;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                null,
                null,
                dataSet.getNodeById(3),
                dataSet.getNodeById(1),
                dataSet,
                Assert::assertNull
        );
    }

    @Test
    public void findRouteBetween_twoWeightedPaths_returnsShortest() {
        GraphDataSet dataSet = GraphDataSet.WEIGHTED_TWO_DIFFERENT_LENGTH_PATHS;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(3),
                        dataSet.getNodeById(4)
                ),
                Arrays.asList(
                        dataSet.getSegmentById(103),
                        dataSet.getSegmentById(104)
                ),
                dataSet.getNodeById(1),
                dataSet.getNodeById(4),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_twoWeightedPathsSwapped_returnsShortest() {
        // Same test as above with the short path swapped with the long one
        GraphDataSet dataSet =
                GraphDataSet.WEIGHTED_TWO_DIFFERENT_LENGTH_PATHS_SWAPPED;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(2),
                        dataSet.getNodeById(4)
                ),
                Arrays.asList(
                        dataSet.getSegmentById(101),
                        dataSet.getSegmentById(102)
                ),
                dataSet.getNodeById(1),
                dataSet.getNodeById(4),
                dataSet,
                null
        );
    }

    @Test
    public void findRouteBetween_manyWeightedPaths_returnsShortest() {
        GraphDataSet dataSet = GraphDataSet.WEIGHTED_MANY_PATHS;
        MapDataModelTestUtils.testFindRouteBetweenOnDataSet(
                Arrays.asList(
                        dataSet.getNodeById(1),
                        dataSet.getNodeById(4),
                        dataSet.getNodeById(7)
                ),
                Arrays.asList(
                        dataSet.getSegmentById(105),
                        dataSet.getSegmentById(106)
                ),
                dataSet.getNodeById(1),
                dataSet.getNodeById(7),
                dataSet,
                null
        );
    }

    @Test
    public void findArticulationPoints_emptyData_returnsEmpty() {
        MapDataModelTestUtils.testFindArticulationPoints(
                GraphDataSet.EMPTY,
                Collections.emptySet()
        );
    }

    @Test
    public void findArticulationPoints_triangleGraph_returnsEmpty() {
        MapDataModelTestUtils.testFindArticulationPoints(
                GraphDataSet.TRIANGLE_GRAPH,
                Collections.emptySet()
        );
    }

    @Test
    public void findArticulationPoints_linearWithThreeNodes_returnsMiddleNode() {
        GraphDataSet dataSet = GraphDataSet.SHORT_LINEAR_GRAPH;
        MapDataModelTestUtils.testFindArticulationPoints(
                dataSet,
                Collections.singleton(dataSet.getNodeById(2))
        );
    }

    @Test
    public void findArticulationPoints_ethaneStructure_returnsMiddleNode() {
        // See Enum definition for diagram
        GraphDataSet dataSet = GraphDataSet.ETHANE_STRUCTURE;
        MapDataModelTestUtils.testFindArticulationPoints(
                dataSet,
                new HashSet<>(Arrays.asList(
                        dataSet.getNodeById(4),
                        dataSet.getNodeById(5)
                ))
        );
    }

    @Test
    public void findArticulationPoints_ethanolStructure_returnsMiddleNode() {
        // See Enum definition for diagram
        GraphDataSet dataSet = GraphDataSet.ETHANOL_ALCOHOL_STRUCTURE;
        MapDataModelTestUtils.testFindArticulationPoints(
                dataSet,
                new HashSet<>(Arrays.asList(
                        dataSet.getNodeById(4),
                        dataSet.getNodeById(5),
                        dataSet.getNodeById(6)
                ))
        );
    }

    @Test
    public void findArticulationPoints_circleStructure_returnsEmpty() {
        // See Enum definition for diagram
        GraphDataSet dataSet = GraphDataSet.CIRCLE_STRUCTURE;
        MapDataModelTestUtils.testFindArticulationPoints(
                dataSet,
                new HashSet<>()
        );
    }
}
