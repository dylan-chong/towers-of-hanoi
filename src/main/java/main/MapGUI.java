package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import main.mapdata.MapDataParser;
import main.mapdata.MapData;
import main.mapdata.Node;
import slightlymodifiedtemplate.GUI;
import slightlymodifiedtemplate.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    private final MapDataParser dataParser;
    private final View view;
    private final MapData.Factory mapDataFactory;
    private final Drawer.Factory drawerFactory;

    private Drawer drawer;
    private MapData mapData;
    private Node highlightedNode;

    @Inject
    public MapGUI(MapDataParser dataParser,
                  View view,
                  MapData.Factory mapDataFactory,
                  Drawer.Factory drawerFactory) {
        this.dataParser = dataParser;
        this.view = view;
        this.mapDataFactory = mapDataFactory;
        this.drawerFactory = drawerFactory;
    }

    @Override
    protected void redraw(Graphics graphics) {
        if (drawer == null) return;
        drawer.draw(graphics, highlightedNode);
    }

    /**
     * Called after the mouse has been clicked on the graphics pane and then
     * released.
     */
    @Override
    protected void onClick(MouseEvent e) {
        if (mapData == null) return;

        Location clickLocation = view.getLocationFromPoint(
                new Point(e.getX(), e.getY())
        );

        double maxDistance = view.getClickRadius();
        highlightedNode = mapData.findNodeNearLocation(clickLocation, maxDistance);
        if (highlightedNode == null) {
            outputLine("There are no nodes close to where you clicked");
            return;
        }

        outputLine("Found a node: " + highlightedNode.id);
    }

    @Override
    protected void onSearch() {
        // TODO sometime
    }

    @Override
    protected void onMove(Move m) {
        view.applyMove(m);
    }

    /**
     * @param polygons a File for polygon-shapes.mp (map be null)
     */
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        try {
            outputLine("Loading data");
            long loadStartTime = System.currentTimeMillis();

            mapData = mapDataFactory.create(
                    dataParser.parseNodes(new Scanner(nodes)),
                    dataParser.parseRoadSegments(new Scanner(segments)),
                    dataParser.parseRoadInfo(new Scanner(roads))
            );
            drawer = drawerFactory.create(mapData, view);

            long duration = System.currentTimeMillis() - loadStartTime;
            outputLine("Loading finished (took " + duration + "ms)");
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @param info Text to put in the text area
     */
    private void outputLine(String info) {
        JTextArea t = getTextOutputArea();
        t.append(info);
        t.append("\n");
    }
}
