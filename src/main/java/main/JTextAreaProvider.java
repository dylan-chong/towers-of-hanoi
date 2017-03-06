package main;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.swing.*;

/**
 * Created by Dylan on 6/03/17.
 */

public class JTextAreaProvider implements Provider<JTextArea> {
    private GameGui gameGui;

    @Inject
    public JTextAreaProvider(GameGui gameGui) {
        this.gameGui = gameGui;
    }

    @Override
    public JTextArea get() {
        return gameGui.getGameOut();
    }
}

