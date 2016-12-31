package main.textprinter;

/**
 * Created by Dylan on 31/12/16.
 */
public class StringTextPrinter extends AbstractTextPrinter {

    private final StringBuilder stringBuilder;

    public StringTextPrinter(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    protected void appendText(String text) {
        stringBuilder.append(text);
    }
}
