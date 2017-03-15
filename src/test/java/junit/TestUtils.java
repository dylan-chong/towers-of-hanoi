package junit;

import main.DataParser;
import main.Node;
import main.RoadSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 14/03/17.
 */
public class TestUtils {
    public static List<Node> getNodesFromString(String input) {
        Scanner scanner = new Scanner(input);
        DataParser parser = new DataParser();
        return new ArrayList<>(parser.parseNodes(scanner));
    }

    public static List<RoadSegment> getRoadSegmentsFromString(String input) {
        Scanner scanner = new Scanner(input);
        DataParser parser = new DataParser();
        return new ArrayList<>(parser.parseRoadSegments(scanner));
    }

    // public static List<Road>
}
