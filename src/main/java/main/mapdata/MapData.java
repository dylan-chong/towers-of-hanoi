package main.mapdata;

import com.google.inject.Inject;
import main.async.AsyncTask;
import main.async.AsyncTaskQueues;
import slightlymodifiedtemplate.Location;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Dylan on 15/03/17.
 * <p>
 * For storing data about the map and finding data
 */
public class MapData {
    public final Collection<Node> nodes;
    public final Collection<RoadSegment> roadSegments;

    /**
     * Don't access this variable directly. Use the getter
     */
    private Collection<RoadInfo> roadInfos;

    private final AsyncTaskQueues asyncTaskQueues;


    @Inject
    private MapData(AsyncTaskQueues asyncTaskQueues,
                    Collection<Node> nodes,
                    Collection<RoadSegment> roadSegments,
                    Supplier<Collection<RoadInfo>> roadInfosSupplier) {
        this.asyncTaskQueues = asyncTaskQueues;
        this.nodes = Collections.unmodifiableCollection(nodes);
        this.roadSegments = Collections.unmodifiableCollection(roadSegments);

        asyncTaskQueues.addTask(new AsyncTask(
                () -> roadInfos = roadInfosSupplier.get(),
                () -> {} // Needs no callback
        ));
    }

    public Collection<RoadInfo> getRoadInfos() {
        while (roadInfos == null) {
            try {
                Thread.sleep(75);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return roadInfos;
    }

    /**
     * Find the nearest {@link Node} near the given location, within a circle
     * with a radius defined by locationUnits.
     *
     * @param locationUnits The number of units in the {@link Location}
     *                      coordinate system.
     */
    public Node findNodeNearLocation(Location location, double locationUnits) {
        Stream<Node> stream = nodes.stream();

        // Find all nodes within range
        stream = stream.filter(node -> node.latLong
                .asLocation().isCloseFast(location, locationUnits));

        // Find closest node within range
        Node closestNode = stream.reduce((node1, node2) -> {
            double dist1 = node1.latLong.asLocation().distance(location);
            double dist2 = node2.latLong.asLocation().distance(location);
            if (dist1 < dist2) return node1;
            return node2;
        }).orElse(null);

        if (closestNode == null) return null;
        if (closestNode.latLong.asLocation().distance(location) > locationUnits) {
            return null;
        }
        return closestNode;
    }

    public Map<RoadInfo, Collection<RoadSegment>> findRoadSegmentsByString(
            String searchTerm) {

        Collection<RoadInfo> matchingRoadInfos = getRoadInfos().stream()
                .filter(roadInfo -> searchMatches(roadInfo.label, searchTerm) ||
                        searchMatches(roadInfo.city, searchTerm))
                .collect(Collectors.toList());

        Map<RoadInfo, Collection<RoadSegment>> result = new HashMap<>();
        matchingRoadInfos.forEach(roadInfo ->
                result.put(roadInfo, findRoadSegmentsForRoadInfo(roadInfo))
        );
        return result;
    }

    public Collection<RoadSegment> findRoadSegmentsForRoadInfo(RoadInfo roadInfo) {
        return roadSegments.stream()
                .filter(segment ->
                        segment.roadId == roadInfo.id
                )
                .collect(Collectors.toList());
    }

    private boolean searchMatches(String stringToCheck, String searchTerm) {
        return stringToCheck.toLowerCase().startsWith(searchTerm.toLowerCase());
    }

    /**
     * Selects roads segments connected to node
     */
    public Collection<RoadSegment> findRoadSegmentsForNode(Node node) {
         return roadSegments.stream()
                .filter(segment ->
                        node.id == segment.node1ID || node.id == segment.node2ID
                )
                .collect(Collectors.toList());
    }

    /**
     * Finds {@link RoadSegment} objects connected to node, then finds the
     * {@link RoadInfo} for each {@link RoadSegment}.
     */
    public Collection<RoadInfo> findRoadsConnectedToNode(Node node) {
        Collection<RoadSegment> roadSegmentsForNode = findRoadSegmentsForNode(node);
        return roadSegmentsForNode.stream()
                .map(this::findRoadInfoForSegment)
                .collect(Collectors.toSet());
    }

    public RoadInfo findRoadInfoForSegment(RoadSegment segment) {
        return this.getRoadInfos().stream()
                .filter(roadInfo -> roadInfo.id == segment.roadId)
                .findAny()
                .orElse(null);
    }

    public static class Factory {
        private final AsyncTaskQueues asyncTaskQueues;

        @Inject
        public Factory(AsyncTaskQueues asyncTaskQueues) {
            this.asyncTaskQueues = asyncTaskQueues;
        }

        /**
         * Must have the same parameter names as the MapData constructor (this
         * is used by Guice)
         */
        public MapData create(Collection<Node> nodes,
                       Collection<RoadSegment> roadSegments,
                       Supplier<Collection<RoadInfo>> roadInfosSupplier) {
            return new MapData(
                    asyncTaskQueues,
                    nodes,
                    roadSegments,
                    roadInfosSupplier
            );
        }
    }
}
