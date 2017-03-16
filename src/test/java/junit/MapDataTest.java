package junit;

import main.LatLong;
import main.async.AsyncTaskQueues;
import main.mapdata.MapData;
import main.mapdata.Node;
import main.mapdata.RoadInfo;
import main.mapdata.RoadSegment;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Dylan on 15/03/17.
 */
public class MapDataTest {
    @Test
    public void findNodeNearLocation_withOneNodeAtExactLocation_returnsLocation() {
        LatLong expectedLatLong = new LatLong(0, 1);
        MapData mapData = new MapData.Factory(new AsyncTaskQueues()).create(
                Collections.singletonList(
                        new Node(1, expectedLatLong)
                ),
                Collections.emptyList(), Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                expectedLatLong.asLocation(),
                TestUtils.roughLocationDistance(0)
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneNodeCloseToClick_returnsLocation() {
        AsyncTaskQueues asyncTaskQueues = new AsyncTaskQueues();
        MapData mapData = new MapData.Factory(asyncTaskQueues).create(
                Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections.emptyList(), Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(5, 8).asLocation(),
                TestUtils.roughLocationDistance(2)
        );
        assertEquals(1, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_oneNodeFarFromClick_returnsNull() {
        MapData mapData = new MapData.Factory(new AsyncTaskQueues()).create(
                Collections.singletonList(
                        new Node(1, new LatLong(4, 7))
                ),
                Collections.emptyList(), Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(50, 80).asLocation(),
                TestUtils.roughLocationDistance(2)
        );
        assertNull(foundNode);
    }

    @Test
    public void findNodeNearLocation_oneCloseNodeOneFarNode_returnsClose() {
        MapData mapData = new MapData.Factory(new AsyncTaskQueues()).create(
                Arrays.asList(
                        new Node(1, new LatLong(4, 7)),
                        new Node(2, new LatLong(15, 15))
                ),
                Collections.emptyList(), Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(14, 15).asLocation(),
                TestUtils.roughLocationDistance(2)
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_twoCloseNodes_returnsClosest() {
        MapData mapData = new MapData.Factory(new AsyncTaskQueues()).create(
                Arrays.asList(
                        new Node(1, new LatLong(4, 7)),
                        new Node(2, new LatLong(15, 15))
                ),
                Collections.emptyList(), Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(12, 14).asLocation(),
                TestUtils.roughLocationDistance(50)
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findNodeNearLocation_twoCloseNodesInDifferentOrder_returnsClosest() {
        MapData mapData = new MapData.Factory(new AsyncTaskQueues()).create(
                Arrays.asList(
                        new Node(2, new LatLong(15, 15)),
                        new Node(1, new LatLong(4, 7))
                ),
                Collections.emptyList(), Collections::emptyList
        );
        Node foundNode = mapData.findNodeNearLocation(
                new LatLong(12, 14).asLocation(),
                TestUtils.roughLocationDistance(50)
        );
        assertEquals(2, foundNode.id);
    }

    @Test
    public void findRoadSegmentsByString_emptyData_returnsEmpty() {
        MapData mapData = new MapData.Factory(new AsyncTaskQueues()).create(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections::emptyList
        );
        Map<RoadInfo, Collection<RoadSegment>> roadSegments =
                mapData.findRoadSegmentsByString("");
        assertEquals(Collections.emptyMap(), roadSegments);
    }

}
