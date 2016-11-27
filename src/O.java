import java.util.Arrays;

/**
 * Created by Dylan on 27/11/16.
 *
 * Output, i.e. printing to console
 */
class O {
    static final int CONSOLE_WIDTH = THMain.TITLE.length();

    /**
     * Print. Wraps tokens to next line if necessary
     */
    static void p(String str) {
        if (str.equals("\n")) {
            System.out.println();
            return;
        }
        String[] lines = str.split("\n");
        if (lines.length > 1 || str.contains("\n")) {
            if (str.startsWith("\n")) System.out.println();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                p(line);
                if (i != lines.length - 1 || line.equals(""))
                    System.out.println();
            }

            if (str.endsWith("\n")) System.out.println();
            return;
        }

        String[] tokens = str.split(" ");

        assert !Arrays.stream(tokens).anyMatch(t -> t.contains("\t"))
                : "Haven't taken into account tabs yet";

        int textX = 0;
        int line = 0;
        for (int t = 0; t < tokens.length; t++) {
            String token = tokens[t];
            if (token.equals("")) continue;

            boolean didJustIndent = false;
            if (line > 0 && textX == 0) {
                textX += 4;
                System.out.print("\t"); // tab all lines except first
                didJustIndent = true;
            }

            if (textX + token.length() > CONSOLE_WIDTH &&
                    !(textX == 0 || didJustIndent)) {
                textX = 0;
                System.out.println();
                line++;
                t--;
                continue;
            }


            // Even if token is too long when the number of tokens printed
            // on this line is 0, print anyway
            textX += token.length() + 1;
            System.out.print(token + ' ');
        }
    }

    /**
     * Println
     */
    static void pln() {
        pln(null);
    }

    /**
     * Println
     * @param ln
     */
    static void pln(String ln) {
        if (ln == null) p("\n");
        else p(ln + '\n');
    }

    /**
     * Printf
     * @param str
     */
    static void pf(String str, Object ... args) {
        p(String.format(str, args));
    }
}
