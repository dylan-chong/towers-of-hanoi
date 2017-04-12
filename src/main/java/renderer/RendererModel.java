package renderer;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Dylan on 12/04/17.
 */
public class RendererModel {
    public final Collection<Triangle> triangles;
    public final Vector3D lightDirection;

    public RendererModel(Collection<Triangle> triangles,
                         Vector3D lightDirection) {
        this.triangles = Collections.unmodifiableCollection(triangles);
        this.lightDirection = lightDirection;
    }
}
