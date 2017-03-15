package main;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import slightlymodifiedtemplate.GUI;

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

    private final DataParser dataParser;
    private final View view;
    private final MapData.Factory mapDataFactory;
    private final Drawer.Factory drawerFactory;

    private MapData mapData; // Null unless data is loaded from onLoad();
    private Drawer drawer;

    @Inject
    public MapGUI(DataParser dataParser,
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

        drawer.draw(graphics);
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
        view.applyMove(m);
    }

    /**
     * @param polygons a File for polygon-shapes.mp (map be null)
     */
    @Override
    protected void onLoad(File nodes, File roads, File segments, File polygons) {
        try {
            outputInfo("Loading data");
            long loadStartTime = System.currentTimeMillis();

            mapData = mapDataFactory.create(
                    dataParser.parseNodes(new Scanner(nodes)),
                    dataParser.parseRoadSegments(new Scanner(segments))
            );

            drawer = drawerFactory.create(mapData, view);

            long duration = System.currentTimeMillis() - loadStartTime;
            outputInfo("Loading finished (took " + duration + "ms)");
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @param info Text to put in the text area
     */
    private void outputInfo(String info) {
        JTextArea t = getTextOutputArea();
        t.append(info);
        t.append("\n");
    }
}
