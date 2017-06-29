package main.printers;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.swing.*;

/**
 * Created by Dylan on 31/12/16.
 */
public class TextAreaPrinter extends AbstractTextPrinter {
    private final JTextArea textArea;

    @Inject
    public TextAreaPrinter(Provider<JTextArea> jTextAreaProvider) {
        this.textArea = jTextAreaProvider.get();
    }

    @Override
    protected void appendText(String text) {
        textArea.append(text);
    }
}
