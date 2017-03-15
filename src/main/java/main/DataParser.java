package main;

import java.util.*;

/**
 * Created by Dylan on 14/03/17.
 */
public class DataParser {
    public Collection<Node> parseNodes(Scanner scanner) {
        List<Node> nodes = new ArrayList<>();
        while (scanner.hasNext()) {
            long id = scanner.nextLong();
            double lat = scanner.nextDouble();
            double lon = scanner.nextDouble();
            nodes.add(new Node(id, new LatLong(lat, lon)));
        }
        return nodes;
    }

    public Collection<RoadSegment> parseRoadSegments(Scanner scanner) {
        List<RoadSegment> roadSegments = new ArrayList<>();

        // Get rid of header row line (it only has the column titles)
        scanner.nextLine();

        while (scanner.hasNext()) {
            Scanner currentLineScanner = new Scanner(scanner.nextLine());
            roadSegments.add(parseSingleRoadSegment(currentLineScanner));
        }

        return roadSegments;
    }

    /**
     * @param lineScanner A scanner for a single line of text which represents
     *                    a single {@link RoadSegment}
     */
    private RoadSegment parseSingleRoadSegment(Scanner lineScanner) {
        long roadId = lineScanner.nextLong();
        double length = lineScanner.nextDouble();
        long node1ID = lineScanner.nextLong();
        long node2ID = lineScanner.nextLong();

        List<LatLong> points = new ArrayList<>();
        while (lineScanner.hasNext()) {
            points.add(new LatLong(
                    lineScanner.nextDouble(),
                    lineScanner.nextDouble()
            ));
        }

        return new RoadSegment(
                roadId,
                length,
                node1ID,
                node2ID,
                points
        );
    }
}
