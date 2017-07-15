package main;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import main.event.Events;
import main.game.DefaultDiskStackFactory;
import main.game.DiskStackFactory;
import main.gui.GameGui;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    /*
     * TODO: Move DiskStackFactory
     */

    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());

        GameGui gui = mainInjector.getInstance(GameGui.class);
        Events.AppReady appReadyEvent = mainInjector.getInstance(
                Events.AppReady.class
        );

        appReadyEvent.broadcast(null);
    }

    public static class MainModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(DiskStackFactory.class).to(DefaultDiskStackFactory.class);
        }
    }
}

