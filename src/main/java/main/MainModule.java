package main;

import com.google.inject.Binder;
import com.google.inject.Module;
import main.game.DefaultDiskStackFactory;
import main.gui.DefaultGameGui;
import main.game.DiskStackFactory;
import main.gui.GameGui;
import main.gui.GameOutProvider;
import main.printers.TextAreaPrinter;
import main.printers.TextPrinter;

import javax.swing.*;

/**
 * Created by Dylan on 6/03/17.
 */
public class MainModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(GameGui.class).to(DefaultGameGui.class);
        binder.bind(TextPrinter.class).to(TextAreaPrinter.class);
        binder.bind(JTextArea.class).toProvider(GameOutProvider.class);
        binder.bind(DiskStackFactory.class).to(DefaultDiskStackFactory.class);
    }
}
