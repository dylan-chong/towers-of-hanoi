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

    @Inject
    public MapGUI(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    protected void redraw(Graphics graphics) {
        // TODO

        if (nodes != null) nodes.forEach(node -> {
            Point p = node.latLong.asPoint(
                    new Location(0, 0),
                    50
            );
            graphics.drawOval(p.x, p.y, 1, 1);
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
        // TODO sometime
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
