package renderer;

import com.google.inject.Inject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class Renderer extends GUI {

    private final Parser parser;

    private Scene scene;

    @Inject
    public Renderer(Parser parser) {
        this.parser = parser;
    }

    @Override
    protected void onLoad(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            this.scene = parser.parse(reader);
        } catch (IOException e) {
           throw new AssertionError(e);
        }
    }

    @Override
    protected void onKeyPress(KeyEvent ev) {
        // TODO fill this in.

		/*
		 * This method should be used to rotate the user's viewpoint.
		 */
    }

    @Override
    protected BufferedImage render() {
        // TODO fill this in.

		/*
		 * This method should put together the pieces of your renderer, as
		 * described in the lecture. This will involve calling each of the
		 * static method stubs in the Pipeline class, which you also need to
		 * fill in.
		 */
        return null;
    }

    /**
     * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
     * indexed by column then row and has imageHeight rows and imageWidth
     * columns. Note that image.setRGB requires x (col) and y (row) are given in
     * that order.
     */
    private BufferedImage convertBitmapToImage(Color[][] bitmap) {
        BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {
                image.setRGB(x, y, bitmap[x][y].getRGB());
            }
        }
        return image;
    }
}

// code for comp261 assignments
