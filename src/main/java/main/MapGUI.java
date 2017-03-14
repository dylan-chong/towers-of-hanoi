package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Scanner;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    private final DataParser dataParser;

    private Collection<Node> nodes;

    private Location viewOrigin = Location.CITY_CENTER.asLocation();
    private double viewScale = 60;

    @Inject
    public MapGUI(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    protected void redraw(Graphics graphics) {
        if (nodes != null) nodes.forEach(node -> {
            Point p = node.latLong.asPoint(
                    viewOrigin,
                    viewScale
            );
            graphics.drawOval(p.x, p.y, 1, 1);
            // TODO LATER change to something else
        });
    }

    @Override
    protected void onClick(MouseEvent e) {
        // TODO sometime
        System.out.println("clic");
    }

    @Override
    protected void onSearch() {
        // TODO sometime
    }

    @Override
    protected void onMove(Move m) {
        if (m == Move.ZOOM_IN || m == Move.ZOOM_OUT) {
            double zoomChangeFactor = 1.3; // Greater than 1 for zooming in
            if (m == Move.ZOOM_OUT) zoomChangeFactor = 1 / zoomChangeFactor;
            viewScale *= zoomChangeFactor;
            return;

            // TODO SOMETIME zoom in center
        }

        double dx = 0;
        if (m == Move.WEST) {
            dx = -1;
        } else if (m == Move.EAST) {
            dx = 1;
        }

        double dy = 0;
        if (m == Move.NORTH) {
            dy = 1;
        } else if (m == Move.SOUTH) {
            dy = -1;
        }

        viewOrigin = viewOrigin.moveBy(dx, dy);
    }

    /**
     * @param polygons a File for polygon-shapes.mp (map be null)
     */
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        try {
            this.nodes = dataParser.parseNodes(new Scanner(nodes));
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

}
