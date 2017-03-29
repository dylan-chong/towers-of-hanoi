package main.mapdata;

import main.mapdata.roads.Node;
import main.mapdata.roads.RoadSegment;
import main.datastructures.Graph;

/**
 * Created by Dylan on 17/03/17.
 *
 * Essentially a typedef to avoid typing out the generics every single time
 */
public class MapGraph extends Graph<Node, RoadSegment> {
}
