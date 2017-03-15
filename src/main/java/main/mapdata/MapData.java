package main.mapdata;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Dylan on 15/03/17.
 *
 * Struct for storing data about the map.
 */
public class MapData {
    public final Collection<Node> nodes;
    public final Collection<RoadSegment> roadSegments;
    public final Collection<RoadInfo> roadInfos;

    private MapData(Collection<Node> nodes,
                    Collection<RoadSegment> roadSegments,
                    Collection<RoadInfo> roadInfos) {
        this.nodes = Collections.unmodifiableCollection(nodes);
        this.roadSegments = Collections.unmodifiableCollection(roadSegments);
        this.roadInfos = Collections.unmodifiableCollection(roadInfos);
    }

    public static class Factory {
        public MapData create(Collection<Node> nodes,
                              Collection<RoadSegment> roadSegments,
                              Collection<RoadInfo> roadInfos) {
            return new MapData(nodes, roadSegments, roadInfos);
        }
    }
}
