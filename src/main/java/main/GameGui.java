package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Dylan on 31/12/16.
 */
public class GameGui extends JFrame {
    private JPanel mainPanel;
    private JTextArea gameOut;
    private JTextField userInput;
    private TowersOfHanoiGame towersOfHanoiGame;

    public GameGui() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(mainPanel); // todo need?

        Font font = new Font("monospaced", Font.PLAIN, 12);
        gameOut.setFont(font);
        userInput.setFont(font);

        gameOut.setColumns(GameInfoPrinter.WIDTH);

        pack();
        setVisible(true);
        setResizable(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println(e);
            }
        });
    }

    public JTextArea getGameOut() {
        return gameOut;
    }

    public void setTowersOfHanoiGame(TowersOfHanoiGame towersOfHanoiGame) {
        this.towersOfHanoiGame = towersOfHanoiGame;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(450, 380);
    }
}
