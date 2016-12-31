package main;

import main.textprintable.TextPrintable;

/**
 * Created by Dylan on 30/12/16.
 */
public class GameInfoPrinter {
    public static final int WIDTH = GameInfoPrinter.GAME_TITLE.length();

    private static final String TITLE_PREFIX = "***************";
    static final String GAME_TITLE =
            TITLE_PREFIX + " WELCOME TO TOWERS OF HANOI " + TITLE_PREFIX;

    private final TextPrintable out;

    public GameInfoPrinter(TextPrintable out) {
        this.out = out;
    }

    public void printWelcome() {
        printSectionLine(0);
        out.println(GAME_TITLE);
        printSectionLine(0);
    }

    public void printInstructions() {
        out.println("Objective: Get everything to the right stack");
    }

    public void printControls() {
        out.println("Controls: Enter '1 3' to move from the left stack to the 3rd stack");
    }

    public void printStackState(DiskStackList diskStacks) {
        out.println();
        printSectionLine(1);
        out.println();
        out.println(diskStacks.toString());
        out.println();
        printSectionLine(1);
        out.println();
    }

    /**
     * @param importance 0 for most important
     */
    public void printSectionLine(int importance) {
        String c;
        switch (importance) {
            case 0:
                c = "*";
                break;
            case 1:
                c = "-";
                break;
            default:
                throw new IllegalArgumentException(
                        "Nothing set for " + importance + " importance level"
                );
        }
        out.println(
                new String(new char[WIDTH]).replace("\0", c)
        );
    }
}
