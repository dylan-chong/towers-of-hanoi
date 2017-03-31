package main.gui.helpers;

import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;
import main.mapdata.model.MapDataModel;
import main.mapdata.Route;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 29/03/17.
 */
public class RouteOutputter {

    public void outputRoute(Route route,
                            MapDataModel mapModel,
                            Output output) {
        output.outputLine("Instructions:");

        RoadInfo lastUniqueRoadInfo = null;
        double distanceSinceUnique = 0; // Distance since lastUniqueRoadInfo
        for (int i = 0; i < route.segments.size(); i++) {
            Node node = route.nodes.get(i);
            RoadSegment segment = route.segments.get(i);
            RoadInfo roadInfo = mapModel.findRoadInfoForSegment(segment);

            if (lastUniqueRoadInfo != null) {
                List<RoadInfo> roadInfos = Arrays.asList(
                        roadInfo, lastUniqueRoadInfo
                );
                if (RoadInfo.getDistinctByName(roadInfos).size() != 2) {
                    // The previous had the same name, so don't bother printing
                    // the name again
                    distanceSinceUnique += segment.length;
                    continue;
                }
            }

            distanceSinceUnique += segment.length;
            String distanceString = String.format("%.2f", distanceSinceUnique);
            output.outputLine("\t- Go from intersection " + node);
            output.outputLine("\t  on " + roadInfo + " for " +
                    distanceString + " km");
            lastUniqueRoadInfo = roadInfo;
            distanceSinceUnique = 0;
        }

        // There is one more node than segment
        Node routeEndNode = route.nodes.get(route.nodes.size() - 1);
        output.outputLine("\t- Then end your journey at " + routeEndNode);
    }

    public interface Output {
        void outputLine(String line);
    }
}
