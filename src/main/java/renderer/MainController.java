package renderer;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Dylan on 11/04/17.
 */
public class MainController extends GUI {
    @Override
    protected void onLoad(File file) {
        System.out.println(file.getAbsolutePath());
    }

    @Override
    protected void onKeyPress(KeyEvent ev) {

    }

    @Override
    protected BufferedImage render() {
        return null;
    }
}
