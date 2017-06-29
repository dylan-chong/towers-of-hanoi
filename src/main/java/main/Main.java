package main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import main.game.TowersOfHanoiGame;
import main.gui.GameGui;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());

        GameGui gui = mainInjector.getInstance(GameGui.class);
        TowersOfHanoiGame game = mainInjector.getInstance(TowersOfHanoiGame.class);

        gui.registerObserver(game);
    }

}
