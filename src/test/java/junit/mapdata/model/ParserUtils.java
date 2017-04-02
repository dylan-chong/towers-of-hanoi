package junit.mapdata.model;

import main.gui.helpers.View;
import main.mapdata.Polygon;
import main.mapdata.location.LatLong;
import main.mapdata.location.Location;
import main.mapdata.model.MapDataParser;
import main.mapdata.roads.Node;
import main.mapdata.roads.Restriction;
import main.mapdata.roads.RoadInfo;
import main.mapdata.roads.RoadSegment;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Created by Dylan on 14/03/17.
 *
 * Helper methods for tests
 */
public class ParserUtils {
    public static List<Node> getNodesFromString(String input) {
        return getItemsFromString(input, MapDataParser::parseNodes);
    }

    public static List<RoadSegment> getRoadSegmentsFromString(String input) {
        return getItemsFromString(input, MapDataParser::parseRoadSegments);
    }

    public static List<RoadInfo> getRoadInfoFromString(String input) {
        Scanner scanner = new Scanner(input);
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parser.parseRoadInfo(scanner));
    }

    public static List<Polygon> getPolygonsFromString(String input) {
        return getItemsFromString(input, MapDataParser::parsePolygons);
    }

    public static List<Restriction> getRestrictionsFromString(String input) {
        return getItemsFromString(input, MapDataParser::parseRestrictions);
    }

    private static <T> List<T> getItemsFromString(
            String input,
            BiFunction<MapDataParser, BufferedReader, Collection<T>> parseFunction) {

        BufferedReader reader = new BufferedReader(new StringReader(input));
        MapDataParser parser = new MapDataParser();
        return new ArrayList<>(parseFunction.apply(parser, reader));
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
