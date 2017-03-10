package swen221.assignment1;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Dylan on 6/03/17.
 */
public class LeftWalker {
    public static void main(String[] args) {
        System.out.println("DONE");

        new JFrame() {{
            getContentPane().add(new TextArea("It works!"));
            pack();
            setVisible(true);
            toFront();
        }};
    }
}
