package junit.mapdata.model;

import main.mapdata.location.LatLong;
import main.gui.helpers.View;
import main.mapdata.*;
import main.mapdata.location.Location;
import main.mapdata.model.MapDataParser;
import main.mapdata.roads.Node;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 14/03/17.
 *
 * Helper methods for tests
 */
public class ParserUtils {
    public static List<Node> getNodesFromString(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseNodes(reader));
    }

    public static List<RoadSegment> getRoadSegmentsFromString(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseRoadSegments(reader));
    }

    public static List<RoadInfo> getRoadInfoFromString(String input) {
        Scanner scanner = new Scanner(input);
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseRoadInfo(scanner));
    }

    public static List<Polygon> getPolygonsFromString(String input) {
        BufferedReader reader = new BufferedReader(new StringReader(input));
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parsePolygons(reader));
    }

    public static double getScale(View view)
            throws IllegalAccessException, NoSuchFieldException {
        Field scaleField = view.getClass().getDeclaredField("scale");
        scaleField.setAccessible(true);
        return (double) scaleField.get(view);
    }

    /**
     * Roughly convert a distance in the {@link LatLong} coordinate system
     * into the {@link Location} coordinate system.
     */
    public static double roughLocationDistance(double latLongDistance) {
        LatLong latLongA = new LatLong(0, 0);
        LatLong latLongB = new LatLong(0, latLongDistance);
        return latLongA.asLocation().distance(latLongB.asLocation());
    }
}
