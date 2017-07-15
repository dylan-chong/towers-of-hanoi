package main;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import main.game.DefaultDiskStackFactory;
import main.game.DiskStackFactory;
import main.gui.GameGui;
import main.printers.TextAreaPrinter;
import main.printers.TextPrinter;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    /*
     * TO DO NEXT Unbind Text printer
     * TODO AFTER: Remove AbstractTextPrinter
     * TODO: Move DiskStackFactory
     *
     */

    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());

        GameGui gui = mainInjector.getInstance(GameGui.class);
    }

    public static class MainModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(TextPrinter.class).to(TextAreaPrinter.class);
            binder.bind(DiskStackFactory.class).to(DefaultDiskStackFactory.class);
        }
    }
}

