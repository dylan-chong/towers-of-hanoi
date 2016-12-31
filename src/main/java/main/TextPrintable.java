package main;

/**
 * Created by Dylan on 31/12/16.
 */
public interface TextPrintable {
    void print(String str);

    void println();

    void println(String ln);

    TextAreaPrinter printf(String str, Object... args);
}
