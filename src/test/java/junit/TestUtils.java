package junit;

import main.LatLong;
import main.View;
import main.mapdata.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 14/03/17.
 *
 * Helper methods for tests
 */
public class TestUtils {
    public static List<Node> getNodesFromString(String input) {
        Scanner scanner = new Scanner(input);
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseNodes(scanner));
    }

    public static List<RoadSegment> getRoadSegmentsFromString(String input) {
        Scanner scanner = new Scanner(input);
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseRoadSegments(scanner));
    }

    public static List<RoadInfo> getRoadInfoFromString(String input) {
        Scanner scanner = new Scanner(input);
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseRoadInfo(scanner));
    }

    public static double getScale(View view)
            throws IllegalAccessException, NoSuchFieldException {
        Field scaleField = view.getClass().getDeclaredField("scale");
        scaleField.setAccessible(true);
        return (double) scaleField.get(view);
    }

    /**
     * Roughly convert a distance in the {@link LatLong} coordinate system
     * into the {@link slightlymodifiedtemplate.Location} coordinate system.
     */
    public static double roughLocationDistance(double latLongDistance) {
        LatLong latLongA = new LatLong(0, 0);
        LatLong latLongB = new LatLong(0, latLongDistance);
        return latLongA.asLocation().distance(latLongB.asLocation());
    }
}
