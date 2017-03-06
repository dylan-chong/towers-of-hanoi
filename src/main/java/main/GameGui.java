package main;

import javax.swing.*;

/**
 * Created by Dylan on 6/03/17.
 *
 * todo make this observable
 */
public interface GameGui {
    JTextArea getGameOut();

    void setTowersOfHanoiGame(TowersOfHanoiGame towersOfHanoiGame);

    void onUserEnteredLine();
}
