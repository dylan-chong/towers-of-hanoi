package junit.mapdata;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import main.LatLong;
import main.mapdata.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 15/03/17.
 */
public class MapDataTest {

    private final Runnable noop = () -> {
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


    private interface ModelFacadeFactory {
        MapDataModel create(
                Runnable finishLoadingCallback,
                Supplier<Collection<Node>> nodes,
                Supplier<Collection<RoadSegment>> roadSegments,
                Supplier<Collection<RoadInfo>> roadInfosSupplier,
                Supplier<Collection<Polygon>> polygonsSupplier);
    }
}
