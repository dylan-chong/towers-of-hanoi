package main.printers;

/**
 * Created by Dylan on 31/12/16.
 */
public abstract class AbstractTextPrinter implements TextPrinter {
    protected abstract void appendText(String text);
    
    @Override
    public void print(String str) {
        appendText(str);
    }

    @Override
    public void println() {
        appendText("\n");
    }

    @Override
    public void println(String ln) {
        appendText(ln + "\n");
    }

    @Override
    public void printf(String str, Object... args) {
        appendText(String.format(str, args));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
