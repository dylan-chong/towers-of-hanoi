package junit;

import org.junit.Test;
import renderer.Pipeline;
import renderer.Polygon;
import renderer.Scene;
import renderer.Vector3D;

import java.awt.*;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 24/04/17.
 */
public class TranslationTests {
    @Test
    public void translateScene_noAmountAndOnlyLight_sceneIsTheSame() {
        Scene oldScene = new Scene(Collections.emptyList(), new Vector3D(1, 2, 3));
        Scene newScene = Pipeline.translateScene(oldScene, new Vector3D(0, 0, 0));
        assertEquals(oldScene, newScene);
    }

    @Test
    public void translateScene_someAmountAndOnlyLight_lightDoesntShift() {
        Scene oldScene = new Scene(Collections.emptyList(), new Vector3D(1, 2, 3));
        Scene newScene = Pipeline.translateScene(oldScene, new Vector3D(4, 5, 6));
        assertEquals(new Scene(
                Collections.emptyList(), new Vector3D(1, 2, 3)
        ), newScene);
    }

    @Test
    public void translateScene_someAmountWithOnePolygon_sceneShifts() {
        Scene oldScene = new Scene(
                Collections.singletonList(
                        new Polygon(
                                new Vector3D(10, 11, 12),
                                new Vector3D(13, 14, 15),
                                new Vector3D(16, 17, 18),
                                Color.WHITE
                        )),
                new Vector3D(1, 2, 3)
        );
        Scene newScene = Pipeline.translateScene(oldScene, new Vector3D(4, 5, 6));
        assertEquals(
                Collections.singletonList(new Polygon(
                        new Vector3D(14, 16, 18),
                        new Vector3D(17, 19, 21),
                        new Vector3D(20, 22, 24),
                        Color.WHITE
                )
        ), newScene.getPolygons());
    }

    @Test
    public void translateToCenter_noPolygons_noShift() {
        Scene oldScene = new Scene(Collections.emptyList(), new Vector3D(1, 2, 3));
        Scene newScene = Pipeline.translateToCenter(oldScene, 0, 0);
        assertEquals(oldScene, newScene);
    }

    @Test
    public void translateToCenter_shiftOnePolygonToOrigin_xAndYShifts() {
        Scene oldScene = new Scene(
                Collections.singletonList(
                        new Polygon(
                                new Vector3D(1, 1, 1),
                                new Vector3D(1, 1, 1),
                                new Vector3D(1, 1, 1),
                                Color.WHITE
                        )),
                new Vector3D(1, 1, 1)
        );
        Scene newScene = Pipeline.translateToCenter(oldScene, 0, 0);
        assertEquals(
                Collections.singletonList(new Polygon(
                        new Vector3D(0, 0, 1), // z doesn't change
                        new Vector3D(0, 0, 1),
                        new Vector3D(0, 0, 1),
                        Color.WHITE
                )
        ), newScene.getPolygons());
    }

    @Test
    public void translateToCenter_shiftOnePolygonToPoint_xAndYShifts() {
        Scene oldScene = new Scene(
                Collections.singletonList(
                        new Polygon(
                                new Vector3D(1, 1, 1),
                                new Vector3D(1, 1, 1),
                                new Vector3D(1, 1, 1),
                                Color.WHITE
                        )),
                new Vector3D(1, 1, 1)
        );
        Scene newScene = Pipeline.translateToCenter(oldScene, 3, 5);
        assertEquals(
                Collections.singletonList(new Polygon(
                        new Vector3D(3, 5, 1), // z doesn't change
                        new Vector3D(3, 5, 1),
                        new Vector3D(3, 5, 1),
                        Color.WHITE
                )
        ), newScene.getPolygons());
    }
}
