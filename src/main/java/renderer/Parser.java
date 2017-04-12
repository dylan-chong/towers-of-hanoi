package renderer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 12/04/17.
 */
public class Parser {
    public RendererModel parse(BufferedReader reader) {
        try {
            String[] lightOrdinates = reader.readLine().split(" ");
            Vector3D lightDirection = new Vector3D(
                    Float.parseFloat(lightOrdinates[0]),
                    Float.parseFloat(lightOrdinates[1]),
                    Float.parseFloat(lightOrdinates[2])
            );
            Collection<Triangle> triangles = reader.lines()
                    .map(line -> {
                        List<String> tokens = Arrays.asList(line.split(" "));

                        List<Float> ordinates = tokens.subList(0, 9)
                                .stream()
                                .map(Float::parseFloat)
                                .collect(Collectors.toList());
                        List<Point> points = new ArrayList<>(3);
                        for (Iterator<Float> iterator = ordinates.iterator();;) {
                            if (!iterator.hasNext()) break;
                            points.add(new Point(
                                    iterator.next(),
                                    iterator.next(),
                                    iterator.next()
                            ));
                        }

                        int[] colorParts = tokens.subList(9, 12)
                                .stream()
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        return new Triangle(
                                points.toArray(new Point[points.size()]),
                                new Color(
                                        colorParts[0],
                                        colorParts[1],
                                        colorParts[2]
                                )
                        );
                    })
                    .collect(Collectors.toList());
            return new RendererModel(triangles, lightDirection);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
