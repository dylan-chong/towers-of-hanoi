package junit;

import main.View;
import org.junit.Test;
import slightlymodifiedtemplate.GUI;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dylan on 15/03/17.
 */
public class ViewTest {

    @Test
    public void applyMove_zoomIn_scaleShouldIncrease() throws Exception {
        View view = new View();
        double scaleBefore = TestUtils.getScale(view);
        view.applyMove(GUI.Move.ZOOM_IN);
        double scaleAfter = TestUtils.getScale(view);
        assertTrue(scaleAfter > scaleBefore);
    }

    @Test
    public void applyMove_zoomOut_scaleShouldDecrease() throws Exception {
        View view = new View();
        double scaleBefore = TestUtils.getScale(view);
        view.applyMove(GUI.Move.ZOOM_OUT);
        double scaleAfter = TestUtils.getScale(view);
        assertTrue(scaleAfter < scaleBefore);
    }

    /**
     * It radius should get smaller because the clickRadius is in the
     * {@link slightlymodifiedtemplate.Location} coordinate system and the
     * constant {@link View#CLICK_RADIUS_PX} is in screen pixels - zooming
     * in causes less units of the Location coordinate system to be viewed.
     */
    @Test
    public void getClickRadius_withZoomIn_radiusShouldDecrease() {
        View view = new View();
        double clickRadiusBefore = view.getClickRadius();
        view.applyMove(GUI.Move.ZOOM_IN);
        double clickRadiusAfter = view.getClickRadius();
        assertTrue(clickRadiusAfter < clickRadiusBefore);
    }

    @Test
    public void getClickRadius_withZoomOut_radiusShouldIncrease() {
        View view = new View();
        double clickRadiusBefore = view.getClickRadius();
        view.applyMove(GUI.Move.ZOOM_OUT);
        double clickRadiusAfter = view.getClickRadius();
        assertTrue(clickRadiusAfter > clickRadiusBefore);
    }
}
