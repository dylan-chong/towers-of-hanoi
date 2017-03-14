package main;

import com.google.inject.Singleton;
import modifiedtemplate.GUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Created by Dylan on 14/03/17.
 */
@Singleton
public class MapGUI extends GUI {

    public MapGUI() {

    }

    @Override
    protected void redraw(Graphics g) {
        // TODO
        System.out.println("redraw");
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
    }
}
