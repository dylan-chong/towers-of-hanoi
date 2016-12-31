package main.printing;

/**
 * Created by Dylan on 31/12/16.
 */
public interface TextPrintable {
    void print(String str);

    void println();

    void println(String ln);

    TextPrintable printf(String str, Object... args);
}
