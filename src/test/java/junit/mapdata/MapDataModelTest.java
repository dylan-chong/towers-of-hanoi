package junit.mapdata;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import main.LatLong;
import main.mapdata.*;
import main.structures.Route;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 15/03/17.
 */
public class MapDataModelTest {

    private static final Runnable noop = () -> {
    };

    /**
     * This is required because I refactored the MapModel into 2 classes:
     * {@link MapDataModel} and {@link MapDataContainer}, so I used a facade
     * to avoid tediously the tests
     */
    private ModelFacadeFactory mapDataFactory;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(binder -> {
            binder.install(new FactoryModuleBuilder()
                    .implement(MapDataModel.class, MapDataModel.class)
                    .build(MapDataModel.Factory.class));
            binder.install(new FactoryModuleBuilder()
                    .implement(MapDataContainer.class, MapDataContainer.class)
                    .build(MapDataContainer.Factory.class));
        });

        MapDataModel.Factory modelFactory = injector.getInstance(
                MapDataModel.Factory.class
        );
        MapDataContainer.Factory containerFactory = injector.getInstance(
                MapDataContainer.Factory.class
        );

        mapDataFactory = (finishLoadingCallback,
                          nodes,
                          roadSegments,
                          roadInfosSupplier,
                          polygonsSupplier) ->
                modelFactory.create(containerFactory.create(
                        finishLoadingCallback,
                        nodes,
                        roadSegments,
                        roadInfosSupplier,
                        polygonsSupplier
                ));
    }

    @Test
    public void findNodeNearLocation_withOneNodeAtExactLocation_returnsLocation() {
        LatLong expectedLatLong = new LatLong(0, 1);
        MapDataModel mapModel = mapDataFactory.create(
                noop,
                () -> Collections.singletonList(
                        new Node(1, expectedLatLong)
                ),
                Collections::emptyList,
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
                noop,
                () -> Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
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
                noop,
                () -> Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
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
                noop,
                () -> Arrays.asList(
                        new Node(1, new LatLong(4, 7)),
                        new Node(2, new LatLong(15, 15))
                ),
                Collections::emptyList,
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
                noop,
                () -> Arrays.asList(
                        new Node(2, new LatLong(15, 15)),
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
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
                noop,
                Collections::emptyList,
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
        testFindRouteBetweenOnDefaultData(
                Collections.singletonList(nodeWithId(1)),
                Collections.emptyList(),
                nodeWithId(1),
                nodeWithId(1)
        );
    }

    @Test
    public void findRouteBetween_oneEdgeApart_findsTheEnd() {
        testFindRouteBetweenOnDefaultData(
                Arrays.asList(
                        nodeWithId(1),
                        nodeWithId(2)
                ),
                Collections.singletonList(segmentWithIdConnectedToNodes(101, 1, 2)),
                nodeWithId(1),
                nodeWithId(2)
        );
    }

    @Test
    public void findRouteBetween_twoEdgesApart_findsTheEnd() {
        testFindRouteBetweenOnDefaultData(
                null,
                null,
                nodeWithId(1),
                nodeWithId(3)
        );
    }

    @Test
    public void findRouteBetween_twoEdgesApart_findsEndWithoutReversing() {
        testFindRouteBetweenOnDefaultData(
                Arrays.asList(
                        nodeWithId(1),
                        nodeWithId(2),
                        nodeWithId(3)
                ),
                Arrays.asList(
                        segmentWithIdConnectedToNodes(101, 1, 2),
                        segmentWithIdConnectedToNodes(102, 2, 3)
                ),
                nodeWithId(1),
                nodeWithId(3)
        );
    }

    /**
     * @param expectedNodes Set to null if you only care about the start and end
     * @param expectedSegments Set to null if you only care about the start and
     *                         end
     */
    private void testFindRouteBetweenOnDefaultData(List<Node> expectedNodes,
                                                   List<RoadSegment> expectedSegments,
                                                   Node routeStart,
                                                   Node routeEnd) {
        MapDataModel mapModel = mapDataFactory.create(
                noop,
                () -> IntStream.range(1, 4) // generate nodes by incrementing ids
                        .mapToObj(this::nodeWithId)
                        .collect(Collectors.toList()),
                () -> Arrays.asList(
                        // Be careful with changing these as it may break
                        // multiple tests
                        segmentWithIdConnectedToNodes(101, 1, 2),
                        segmentWithIdConnectedToNodes(102, 2, 3)
                ),
                Collections::emptyList,
                Collections::emptyList
        );
        Route route = mapModel.findRouteBetween(routeStart, routeEnd);

        if (expectedNodes != null) assertEquals(expectedNodes, route.nodes);
        if (expectedSegments != null) assertEquals(expectedSegments, route.segments);

        assertEquals(route.nodes.get(0), routeStart);
        assertEquals(route.nodes.get(route.nodes.size() - 1), routeEnd);
    }

    /**
     * Creates a {@link Node} with a default location, so that nodes with the
     * same id can be {@link Node#equals(Object)} to each other.
     */
    private Node nodeWithId(int id) {
        return new Node(id, new LatLong(0, 0));
    }

    /**
     * Similar to nodeWithId
     */
    private RoadSegment segmentWithIdConnectedToNodes(int id,
                                                      int nodeId1,
                                                      int nodeId2) {
        return new RoadSegment(id, 1, nodeId1, nodeId2, Collections.emptyList());
    }

    private interface ModelFacadeFactory {
        MapDataModel create(
                Runnable finishLoadingCallback,
                Supplier<Collection<Node>> nodes,
                Supplier<Collection<RoadSegment>> roadSegments,
                Supplier<Collection<RoadInfo>> roadInfosSupplier,
                Supplier<Collection<Polygon>> polygonsSupplier);
    }
}
