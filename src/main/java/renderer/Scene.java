package renderer;

import java.util.Collections;
import java.util.List;

/**
 * The Scene class is where we store data about a 3D model and light source
 * inside our renderer. It also contains a static inner class that represents one
 * single polygon.
 * <p>
 * Method stubs have been provided, but you'll need to fill them in.
 * <p>
 * If you were to implement more fancy rendering, e.g. Phong shading, you'd want
 * to store more information in this class.
 */
public class Scene {

    private final List<Polygon> polygons;
    private final Vector3D lightPos;

    public Scene(List<Polygon> polygons, Vector3D lightPos) {
        this.polygons = Collections.unmodifiableList(polygons);
        this.lightPos = lightPos;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public Vector3D getLightPos() {
        return lightPos;
    }
}

// code for COMP261 assignments
