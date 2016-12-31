package main.textprintable;

import javax.swing.*;

/**
 * Created by Dylan on 31/12/16.
 */
public class TextAreaPrinter implements TextPrintable {

    private final JTextArea textArea;

    public TextAreaPrinter(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void print(String str) {
        textArea.append(str);
    }

    @Override
    public void println() {
        textArea.append("\n");
    }

    @Override
    public void println(String ln) {
        textArea.append(ln + "\n");
    }

    @Override
    public TextAreaPrinter printf(String str, Object... args) {
        textArea.append(String.format(str, args));
        return this;
    }
}
