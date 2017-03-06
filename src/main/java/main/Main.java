package main;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());

        GameGui gameGui = mainInjector.getInstance(GameGui.class);
        TowersOfHanoiGame game = mainInjector.getInstance(TowersOfHanoiGame.class);
        gameGui.setTowersOfHanoiGame(game);
    }

}
