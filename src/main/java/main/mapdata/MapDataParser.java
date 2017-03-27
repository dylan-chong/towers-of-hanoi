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

    public Collection<Polygon> parsePolygons(BufferedReader reader) {
        try {
            List<Polygon> polygons = new ArrayList<>();

            // For every polygon in the file
            while (true) {
                String line = reader.readLine();
                if (line == null) return polygons;

                if (line.trim().isEmpty()) continue;
                assert line.equals("[POLYGON]"); // start of new polygon

                polygons.add(parseOnePolygon(reader));
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * Oh god who wrote this mess?
     */
    private Polygon parseOnePolygon(BufferedReader reader) throws IOException {
        String type = null;
        String label = null;
        List<LatLong> points = new ArrayList<>();
        Integer endLevel = null;
        Integer cityIdx = null;

        // Get data from fields
        while (true) {
            String infoLine = reader.readLine();
            if (infoLine.equals("[END]")) {
                return new Polygon(type, label, points, endLevel, cityIdx);
            }
            String key = infoLine.split("=")[0].toLowerCase();
            String value = infoLine.split("=")[1];

            // Put data in fields
            switch (key) {
                case "type":
                    type = value;
                    break;
                case "label":
                    label = value;
                    break;
                case "endlevel":
                    endLevel = Integer.parseInt(value);
                    break;
                case "cityidx":
                    cityIdx = Integer.parseInt(value);
                    break;
                case "data0":
                case "data1":
                    List<Double> ordinates = Arrays
                            .stream(value.split("[(),]"))
                            .filter(ordinate -> !ordinate.isEmpty())
                            .map(Double::parseDouble)
                            .collect(Collectors.toList());
                    // If a piece of data has multiple Data0 tags, it is a
                    // multipolygon
                    List<LatLong> currentPolygonPoints = new ArrayList<>();
                    for (Iterator<Double> iterator = ordinates.iterator();
                         iterator.hasNext(); ) {
                        double lat = iterator.next();
                        double lon = iterator.next();
                        currentPolygonPoints.add(new LatLong(lat, lon));
                    }
                    if (currentPolygonPoints.size() > 1) {
                        // Close off this polygon, so that other polygons can
                        // be appended
                        currentPolygonPoints.add(currentPolygonPoints.get(0));
                    }
                    // Make this a multipolygon
                    points.addAll(currentPolygonPoints);
                    if (points.size() > currentPolygonPoints.size()) {
                        points.add(points.get(0));
                    }
                    break;
                default:
                    throw new IOException("Bad file format");
            }
        }
    }

    private static boolean toBool(int integer) {
        return integer != 0;
    }

    /**
     * Don't use this anymore, use a {@link BufferedReader} instead for speed,
     *
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
