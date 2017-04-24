package renderer;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public static final Color LIGHT_COLOR = new Color(255, 255, 255);

    private final List<Polygon> polygons;
    private final Vector3D lightDirection;

    private transient List<MinMax> xyzBounds;

    public Scene(List<Polygon> polygons, Vector3D lightDirection) {
        this.polygons = Collections.unmodifiableList(polygons);
        this.lightDirection = lightDirection;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public Vector3D getLightDirection() {
        return lightDirection;
    }

    /**
     * @return The {@link MinMax} for x, y, and z ordinates.
     */
    public List<MinMax> getXYZBounds() {
        if (xyzBounds != null) return xyzBounds;

        List<Vector3D> allVertices = polygons.stream()
                .map(Polygon::getVertices)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        if (allVertices.isEmpty()) {
            return null;
        }

        // Find min/max x/y/z (all combinations)
        List<Function<Vector3D, Float>> ordinateGetters = Arrays.asList(
                vertex -> vertex.x,
                vertex -> vertex.y,
                vertex -> vertex.z
        );
        xyzBounds = ordinateGetters.stream()
                .map(ordinateGetter -> {
                    MinMax minMax = new MinMax();
                    allVertices.forEach(vertex -> {
                        float ordinate = ordinateGetter.apply(vertex);
                        minMax.update(ordinate);
                    });
                    return minMax;
                })
                .collect(Collectors.toList());
        return xyzBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scene scene = (Scene) o;

        if (polygons != null) {
            if (!polygons.equals(scene.polygons))
                return false;
        } else {
            if (scene.polygons != null)
                return false;
        }
        if (lightDirection != null)
            return lightDirection.equals(scene.lightDirection);
        else return scene.lightDirection == null;
    }

    @Override
    public int hashCode() {
        int result = polygons != null ? polygons.hashCode() : 0;
        result = 31 * result + (lightDirection != null ? lightDirection.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CustomGson.getInstance().toJson(this);
    }
}

// code for COMP261 assignments
