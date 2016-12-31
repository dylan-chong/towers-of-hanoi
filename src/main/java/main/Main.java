package main;

import main.printing.TextAreaPrinter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    public static void main(String[] args) {
        JFrame gameFrame = new JFrame("Towers of Hanoi");
        gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);

        TextArea gameTextArea = new TextArea(null,
                20, GameInfoPrinter.WIDTH, TextArea.SCROLLBARS_NONE);
        gameTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        gameFrame.getContentPane().add(gameTextArea);

        gameFrame.pack();
        gameFrame.setVisible(true);

        TowersOfHanoiGame game = new TowersOfHanoiGame(
                new TextAreaPrinter(gameTextArea), new DiskStackList()
        );
    }
}
