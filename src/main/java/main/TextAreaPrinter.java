package main;

import javax.swing.*;

/**
 * Created by Dylan on 31/12/16.
 */
public class TextAreaPrinter extends AbstractTextPrinter {

    private final JTextArea textArea;

    public TextAreaPrinter(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    protected void appendText(String text) {
        textArea.append(text);
    }
}
