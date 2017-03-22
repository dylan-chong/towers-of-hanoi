package junit.mapdata;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import main.LatLong;
import main.mapdata.MapData;
import main.mapdata.Node;
import main.mapdata.RoadInfo;
import main.mapdata.RoadSegment;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 15/03/17.
 */
public class MapDataTest {

    private final Runnable noop = () -> {};
    private MapData.Factory mapDataFactory;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(binder ->
                binder.install(new FactoryModuleBuilder()
                        .implement(MapData.class, MapData.class)
                        .build(MapData.Factory.class))
        );
        mapDataFactory = injector.getInstance(MapData.Factory.class);
    }

    @Test
    public void findNodeNearLocation_withOneNodeAtExactLocation_returnsLocation() {
        LatLong expectedLatLong = new LatLong(0, 1);
        MapData mapData = mapDataFactory.create(
                noop,
                () -> Collections.singletonList(
                        new Node(1, expectedLatLong)
                ),
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                expectedLatLong.asLocation()
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneNodeCloseToClick_returnsLocation() {
        MapData mapData = mapDataFactory.create(
                noop,
                () -> Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(5, 8).asLocation()
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneNodeFarFromClick_returnsNodeAnyway() {
        MapData mapData = mapDataFactory.create(
                noop,
                () -> Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(50, 80).asLocation()
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneCloseNodeOneFarNode_returnsClose() {
        MapData mapData = mapDataFactory.create(
                noop,
                () -> Arrays.asList(
                        new Node(1, new LatLong(4, 7)),
                        new Node(2, new LatLong(15, 15))
                ),
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(14, 15).asLocation()
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneFarOneClose_returnsClosest() {
        MapData mapData = mapDataFactory.create(
                noop,
                () -> Arrays.asList(
                        new Node(2, new LatLong(15, 15)),
                        new Node(1, new LatLong(4, 7))
                ),
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(12, 14).asLocation()
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findRoadSegmentsByString_emptyData_returnsEmpty() {
        MapData mapData = mapDataFactory.create(
                noop,
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList,
                Collections::emptyList
        );
        Map<RoadInfo, Collection<RoadSegment>> roadSegments =
                mapData.findRoadSegmentsByString(" ", Integer.MAX_VALUE);
        assertEquals(Collections.emptyMap(), roadSegments);
    }
}
