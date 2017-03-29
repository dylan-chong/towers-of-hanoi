package junit.gui.helpers;

import junit.mapdata.model.ParserUtils;
import main.gui.helpers.View;
import org.junit.Test;
import main.gui.GUI;

import java.awt.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dylan on 15/03/17.
 */
public class ViewTest {

    @Test
    public void applyMove_zoomIn_scaleShouldIncrease() throws Exception {
        View view = new View();
        double scaleBefore = ParserUtils.getScale(view);
        view.applyMove(GUI.Move.ZOOM_IN, new Dimension(100, 50));
        double scaleAfter = ParserUtils.getScale(view);
        assertTrue(scaleAfter > scaleBefore);
    }

    @Test
    public void applyMove_zoomOut_scaleShouldDecrease() throws Exception {
        View view = new View();
        double scaleBefore = ParserUtils.getScale(view);
        view.applyMove(GUI.Move.ZOOM_OUT, new Dimension(100, 50));
        double scaleAfter = ParserUtils.getScale(view);
        assertTrue(scaleAfter < scaleBefore);
    }
}
