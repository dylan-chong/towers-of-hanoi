package junit.mapdata.model;

import junit.async.StubTaskQueue;
import main.mapdata.NodeState;
import main.mapdata.Route;
import main.mapdata.model.MapDataContainer;
import main.mapdata.model.MapDataModel;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 30/03/17.
 */
public class MapDataModelTestUtils {

    /**
     * This is required because I refactored the MapModel into 2 classes:
     * {@link MapDataModel} and {@link MapDataContainer}, so I used a facade
     * to avoid tediously the tests
     */
    public static final ModelFactoryFacade
            MAP_DATA_FACTORY = (nodes,
                              roadSegments,
                              roadInfosSupplier) ->
            new MapDataModel(new MapDataContainer(
                    new StubTaskQueue(),
                    () -> {},
                    nodes,
                    roadSegments,
                    roadInfosSupplier,
                    Collections::emptyList,
                    Collections::emptyList
            ));

    /**
     * @param expectedNodes Set to null if you only care about the start and end
     * @param expectedSegments Set to null if you only care about the start and
     *                         end
     * @param routeMatcher nullable. Use for custom asserts for route
     */
    public static void testFindRouteBetweenOnDataSet(
            List<Node> expectedNodes,
            List<RoadSegment> expectedSegments,
            Node routeStart,
            Node routeEnd,
            GraphDataSet graphDataSet,
            Consumer<Route> routeMatcher) {

        MapDataModel mapModel = MAP_DATA_FACTORY.create(
                () -> graphDataSet.nodes,
                () -> graphDataSet.roadSegments,
                () -> graphDataSet.roadInfos
        );
        Route route = mapModel.findRouteBetween(
                routeStart, routeEnd, new NodeState.TimeHeuristic() //todo use distance
        );

        if (routeMatcher != null) {
            routeMatcher.accept(route);
            return;
        }

        if (expectedNodes != null) assertEquals(expectedNodes, route.nodes);
        if (expectedSegments != null) assertEquals(expectedSegments, route.segments);

        assertEquals(route.nodes.get(0), routeStart);
        assertEquals(route.nodes.get(route.nodes.size() - 1), routeEnd);
    }

    public static void testFindArticulationPoints(GraphDataSet dataSet,
                                                  Set<Node> expectedPoints) {
        MapDataModel model = MAP_DATA_FACTORY.create(
                () -> dataSet.nodes,
                () -> dataSet.roadSegments,
                () -> dataSet.roadInfos
        );
        Set<Node> articulationPoints = model.findAllArticulationPoints();
        assertEquals(expectedPoints, articulationPoints);
    }

    public interface ModelFactoryFacade {
        MapDataModel create(
                Supplier<Collection<Node>> nodes,
                Supplier<Collection<RoadSegment>> roadSegments,
                Supplier<Collection<RoadInfo>> roadInfosSupplier);
    }
}
