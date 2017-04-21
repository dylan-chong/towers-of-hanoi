package renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 12/04/17.
 */
public class Parser {
    public Scene parse(BufferedReader reader) {
        try {
            String[] lightOrdinates = reader.readLine().split(" ");
            Vector3D lightDirection = new Vector3D(
                    Float.parseFloat(lightOrdinates[0]),
                    Float.parseFloat(lightOrdinates[1]),
                    Float.parseFloat(lightOrdinates[2])
            );
            List<Polygon> triangles = reader.lines()
                    .map(line -> {
                        List<String> tokens = Arrays.asList(line.split(" "));

                        double[] positionDoubles = tokens.subList(0, 9)
                                .stream()
                                .mapToDouble(Float::parseFloat)
                                .toArray();
                        float[] positionFloats = new float[positionDoubles.length];
                        for (int i = 0; i < positionDoubles.length; i++) {
                            positionFloats[i] = (float) positionDoubles[i];
                        }

                        int[] colorParts = tokens.subList(9, 12)
                                .stream()
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        return new Polygon(positionFloats, colorParts);
                    })
                    .collect(Collectors.toList());
            return new Scene(triangles, lightDirection);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
