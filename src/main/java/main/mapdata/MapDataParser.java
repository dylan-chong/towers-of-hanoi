package main.mapdata;

import main.LatLong;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 14/03/17.
 */
public class MapDataParser {
    public Collection<Node> parseNodes(BufferedReader reader) {
        return reader.lines()
                .parallel()
                .map(line -> {
                    String[] tokens = line.split("\t");
                    long id = Long.parseLong(tokens[0]);
                    double lat = Double.parseDouble(tokens[1]);
                    double lon = Double.parseDouble(tokens[2]);
                    return new Node(id, new LatLong(lat, lon));
                })
                .collect(Collectors.toList());
    }

    public Collection<RoadSegment> parseRoadSegments(BufferedReader reader) {
        try {
            // Get rid of header line
            reader.readLine();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return reader.lines()
                .parallel()
                .map(line -> {
                    String[] tokens = line.split("\t");

                    int next = 0; // Next token index

                    long roadId = Long.parseLong(tokens[next++]);
                    double length = Double.parseDouble(tokens[next++]);
                    long node1ID = Long.parseLong(tokens[next++]);
                    long node2ID = Long.parseLong(tokens[next++]);

                    List<LatLong> points = new ArrayList<>();
                    while (next < tokens.length) {
                        points.add(new LatLong(
                                Double.parseDouble(tokens[next++]),
                                Double.parseDouble(tokens[next++])
                        ));
                    }

                    return new RoadSegment(
                            roadId,
                            length,
                            node1ID,
                            node2ID,
                            points
                    );
                })
                .collect(Collectors.toList());
    }

    public Collection<RoadInfo> parseRoadInfo(Scanner scanner) {
        return new ScannerParser<RoadInfo>() {
            @Override
            protected RoadInfo parseLine(Scanner lineScanner) {
                int roadId = lineScanner.nextInt();
                int roadType = lineScanner.nextInt();
                String label = lineScanner.next();
                String city = lineScanner.next();
                boolean oneWay = toBool(lineScanner.nextInt());
                RoadInfo.SpeedLimit speedLimit = RoadInfo.SpeedLimit
                        .values()[lineScanner.nextInt()];
                RoadInfo.RoadClass roadClass = RoadInfo.RoadClass
                        .values()[lineScanner.nextInt()];
                boolean notForCar = toBool(lineScanner.nextInt());
                boolean notForPedestrians = toBool(lineScanner.nextInt());
                boolean notForBicycles = toBool(lineScanner.nextInt());

                return new RoadInfo(
                        roadId,
                        roadType,
                        label,
                        city,
                        oneWay,
                        speedLimit,
                        roadClass,
                        notForCar,
                        notForPedestrians,
                        notForBicycles
                );
            }
        }.parse(scanner);
    }

    // public Collection<Polygon>

    private static boolean toBool(int integer) {
        return integer != 0;
    }

    /**
     * Don't use this anymore, use a {@link BufferedReader} instead for speed,
     *
     * @param <T> The type of object to return
     */
    @Deprecated
    private abstract class ScannerParser<T> {
        /**
         * @param scanner A scanner that has one item per line
         */
        public Collection<T> parse(Scanner scanner) {
            List<T> objects = new ArrayList<>();

            if (!scanner.hasNext()) return objects;
            if (hasHeaderLine()) scanner.nextLine();

            while (scanner.hasNext()) {
                Scanner currentLineScanner = new Scanner(scanner.nextLine());
                currentLineScanner.useDelimiter("\t" );
                objects.add(parseLine(currentLineScanner));
            }

            return objects;
        }

        /**
         * Get rid of header row line (it only has the column titles)
         */
        protected boolean hasHeaderLine() {
            return true;
        }

        /**
         * @param lineScanner A scanner for a single line of text which represents
         *                    a single {@link T}. The delimiter is set to '\t'.
         */
        protected abstract T parseLine(Scanner lineScanner);
    }
}
