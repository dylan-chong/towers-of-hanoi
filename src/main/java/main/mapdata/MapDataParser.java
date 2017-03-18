package main.mapdata;

import main.LatLong;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Dylan on 14/03/17.
 */
public class MapDataParser {
    public Collection<Node> parseNodes(Scanner scanner) {
        return new ScannerParser<Node>() {
            @Override
            protected boolean hasHeaderLine() {
                return false;
            }

            @Override
            protected Node parseLine(Scanner lineScanner) {
                long id = lineScanner.nextLong();
                double lat = lineScanner.nextDouble();
                double lon = lineScanner.nextDouble();
                return new Node(id, new LatLong(lat, lon));
            }

        }.parse(scanner);
    }

    public Collection<RoadSegment> parseRoadSegments(Scanner scanner) {
        return new ScannerParser<RoadSegment>() {
            @Override
            protected RoadSegment parseLine(Scanner lineScanner) {
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
        }.parse(scanner);
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
     * @param <T> The type of object to return
     */
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
