package main.textprinter;

/**
 * Created by Dylan on 31/12/16.
 */
public interface TextPrinter {
    void print(String str);

    void println();

    void println(String ln);

    void printf(String str, Object... args);
}
