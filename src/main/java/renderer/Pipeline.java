package renderer;

import java.awt.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * <p>
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

    /**
     * Returns true if the given polygon is facing away from the camera (and so
     * should be hidden), and false otherwise.
     *
     * Hidden when cross product is positive (across product is the direction
     * the polygon is facingAnd the positive direction is the direction that
     * the camera is looking through).
     */
    public static boolean isHidden(Polygon poly) {
        return poly.getNormal().z > 0;
    }

    /**
     * Computes the colour of a polygon on the screen, once the lights, their
     * angles relative to the polygon's face, and the reflectance of the polygon
     * have been accounted for.
     *
     * @param lightDirection The Vector3D pointing to the directional light read in from
     *                       the file.
     * @param lightColor     The color of that directional light (incident
     *                       light)
     * @param ambientLight   The ambient light in the scene, i.e. light that doesn't depend
     *                       on the direction.
     */
    public static Color getShading(Polygon poly,
                                   Vector3D lightDirection,
                                   Color lightColor,
                                   Color ambientLight) {
        // lightIntensity == cos(theta)
        // Theta is the angle of the incident light relative to the polygon.
        // Theta == 0 when the light is directly facing polygon
        // cosTheta == 1 when theta == 0, or less when Theta > 0
        float lightIntensity = poly.getNormal().cosTheta(lightDirection);
        if (lightIntensity < 0 || lightIntensity > 1) {
            // polygon is facing away from camera
            return ambientLight;
        }

        Stream<Function<Color, Float>> colorGetters = Stream.of(
                c -> c.getRed() / 255f,
                c -> c.getGreen() / 255f,
                c -> c.getBlue() / 255f);
        double[] colorComponents = colorGetters.mapToDouble(
                colorGet -> {
                    float ambientCol = colorGet.apply(ambientLight);
                    float lightCol = colorGet.apply(lightColor);
                    // polygon color
                    float reflectance = colorGet.apply(poly.getReflectance());
                    float result = ambientCol + (lightCol * lightIntensity) * reflectance;
                    return Math.min(result, 1f);
                })
                .toArray();

        return new Color(
                (float) colorComponents[0],
                (float) colorComponents[1],
                (float) colorComponents[2]
        );
    }

    /**
     * This method should rotate the polygons and light such that the viewer is
     * looking down the Z-axis. The idea is that it returns an entirely new
     * Scene object, filled with new Polygons, that have been rotated.
     *
     * @param scene The original Scene.
     * @param xRot  An angle describing the viewer's rotation in the YZ-plane (i.e
     *              around the X-axis).
     * @param yRot  An angle describing the viewer's rotation in the XZ-plane (i.e
     *              around the Y-axis).
     * @return A new Scene where all the polygons and the light source have been
     * rotated accordingly.
     */
    public static Scene rotateScene(Scene scene, float xRot, float yRot) {
        // TODO fill this in.
        return null;
    }

    /**
     * This should translate the scene by the appropriate amount.
     *
     * @param scene
     * @return
     */
    public static Scene translateScene(Scene scene) {
        // TODO fill this in.
        return null;
    }

    /**
     * This should scale the scene.
     *
     * @param scene
     * @return
     */
    public static Scene scaleScene(Scene scene) {
        // TODO fill this in.
        return null;
    }

    /**
     * Computes the edgelist of a single provided polygon, as per the lecture
     * slides.
     */
    public static EdgeList computeEdgeList(Polygon poly) {
        EdgeList edgeList = new EdgeList(poly.getMinX(), poly.getMaxX());

        for (Polygon.Edge polyEdge : poly.getEdges()) {
            Vector3D start = polyEdge.start;
            Vector3D end = polyEdge.end;

            float xSlope = (end.x - start.x) / (end.y - start.y);
            float zSlope = (end.z - start.z) / (end.y - start.y);

            float x = start.x; // modified below
            float z = start.z; // modified below

            // assume that the sides are in a anticlockwise direction
            if (start.y < end.y) {
                // going downwards (we must be on the left side)
                for (int y = Math.round(start.y); y <= Math.round(end.y); y++) {
                    edgeList.setLeftX(y, x);
                    x += xSlope;
                    edgeList.setLeftZ(y, z);
                    z += zSlope;
                }
            } else {
                // going upwards (we must be on the right side)
                for (int y = Math.round(start.y); y >= Math.round(end.y); y--) {
                    edgeList.setRightX(y, x);
                    x -= xSlope;
                    edgeList.setRightZ(y, z);
                    z -= zSlope;
                }
            }
        }
        return edgeList;
    }

    /**
     * Fills a zbuffer with the contents of a single edge list according to the
     * lecture slides.
     * <p>
     * The idea here is to make zbuffer and zdepth arrays in your main loop, and
     * pass them into the method to be modified.
     *
     * @param zbuffer      A double array of colours representing the Color at each pixel
     *                     so far.
     * @param zdepth       A double array of floats storing the z-value of each pixel
     *                     that has been coloured in so far.
     * @param polyEdgeList The edgelist of the polygon to add into the zbuffer.
     * @param polyColor    The colour of the polygon to add into the zbuffer.
     */
    public static void updateZBuffer(Color[][] zbuffer,
                                     float[][] zdepth,
                                     EdgeList polyEdgeList,
                                     Color polyColor) {
        for (int y = polyEdgeList.getStartY(); y < polyEdgeList.getEndY(); y++) {
            float leftX = polyEdgeList.getLeftX(y);
            float leftZ = polyEdgeList.getLeftZ(y);
            float rightX = polyEdgeList.getRightX(y);
            float rightZ = polyEdgeList.getRightZ(y);

            float slope = (rightZ - leftZ) / (rightX - leftX);

            float z = leftZ;
            for (int x = Math.round(leftX);
                 x < rightX;
                 x++, z += slope) {
                if (zbuffer[y][x] != null && zdepth[y][x] < z) {
                    continue; // current poly is further
                }

                zdepth[y][x] = z;
                zbuffer[y][x] = polyColor;
            }
        }
    }
}

// code for comp261 assignments
