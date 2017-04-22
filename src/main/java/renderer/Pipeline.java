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
        Vector3D normal = poly.getNormal().unitVector();
        Vector3D lightDir = lightDirection.unitVector();

        // lightIntensity == cos(theta)
        // Theta is the angle of the incident light relative to the polygon.
        // Theta == 0 when the light is directly facing polygon
        // cosTheta == 1 when theta == 0, or less when Theta > 0
        float lightIntensity = normal.cosTheta(lightDir);
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
                    return ambientCol + (lightCol * lightIntensity) * reflectance;
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
        // TODO fill this in.
        return null;
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
    public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
        // TODO fill this in.
    }
}

// code for comp261 assignments
