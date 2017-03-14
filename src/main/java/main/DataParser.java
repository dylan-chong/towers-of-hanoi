package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

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
}
