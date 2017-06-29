package main.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.swing.*;

/**
 * Created by Dylan on 6/03/17.
 */

public class GameOutProvider implements Provider<JTextArea> {
    private GameGui gameGui;

    @Inject
    public GameOutProvider(GameGui gameGui) {
        this.gameGui = gameGui;
    }

    @Override
    public JTextArea get() {
        return gameGui.getGameOut();
    }
}

