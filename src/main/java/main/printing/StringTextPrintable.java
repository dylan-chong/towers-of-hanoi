package main.printing;

/**
 * Created by Dylan on 31/12/16.
 */
public class StringTextPrintable implements TextPrintable {

    private final StringBuilder stringBuilder;

    public StringTextPrintable(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public void print(String str) {
        stringBuilder.append(str);
    }

    @Override
    public void println() {
        stringBuilder.append("\n");
    }

    @Override
    public void println(String ln) {
        stringBuilder.append(ln)
                .append("\n");
    }

    @Override
    public StringTextPrintable printf(String str, Object... args) {
        stringBuilder.append(String.format(str, args));
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
