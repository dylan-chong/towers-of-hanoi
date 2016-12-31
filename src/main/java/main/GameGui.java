package main;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Dylan on 31/12/16.
 */
public class GameGui extends JFrame {
    private JPanel mainPanel;
    private JTextArea gameOut;
    private JTextField userInput;
    private JScrollPane gameOutScroll;
    private TowersOfHanoiGame towersOfHanoiGame;

    public GameGui() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(mainPanel);

        Font font = new Font("monospaced", Font.PLAIN, 12);
        gameOut.setFont(font);
        userInput.setFont(font);

        gameOut.setColumns(GameInfoPrinter.WIDTH);

        // Make gameOut scroll to the bottom automatically
        ((DefaultCaret) gameOut.getCaret())
                .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        pack();
        setVisible(true);
        setResizable(false);

        userInput.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUserEnteredLine();
            }
        });
    }

    public JTextArea getGameOut() {
        return gameOut;
    }

    public void setTowersOfHanoiGame(TowersOfHanoiGame towersOfHanoiGame) {
        this.towersOfHanoiGame = towersOfHanoiGame;
    }

    // todo don't auto size window
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(470, 380);
    }

    private void onUserEnteredLine() {
        towersOfHanoiGame.onUserInputtedLine(userInput.getText());
        userInput.setText("");
    }
}
