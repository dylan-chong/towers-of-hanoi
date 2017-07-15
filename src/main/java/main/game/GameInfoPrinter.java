package main.game;

import com.google.inject.Inject;
import main.event.Events;

/**
 * Created by Dylan on 30/12/16.
 */
public class GameInfoPrinter {
    private static final String TITLE_PREFIX = "***************";
    private static final String GAME_TITLE =
            TITLE_PREFIX + " WELCOME TO TOWERS OF HANOI " + TITLE_PREFIX;
    public static final int WIDTH = GAME_TITLE.length();

    public static final String CANT_SOLVE = "CAN'T SOLVE: ";
    public static final String GAME_HAS_BEEN_SOLVED = "GAME HAS BEEN SOLVED";

    private final Events.OutputText outputTextEvent;

    @Inject
    public GameInfoPrinter(Events.OutputText outputTextEvent) {
        this.outputTextEvent = outputTextEvent;
    }

    public GameInfoPrinter printWelcome() {
        printSectionLine(0);
        println(GAME_TITLE);
        printSectionLine(0);
        return this;
    }

    public GameInfoPrinter printInstructions() {
        println("Objective: Get everything to the right stack");
        return this;
    }

    public GameInfoPrinter printControls() {
        println("Controls: Enter '1 3' to move from the left stack to " +
                "the 3rd stack. Enter 'solve' when you start the game to " +
                "automatically solve the game");
        return this;
    }

    public GameInfoPrinter printShortControls() {
        print("Action: ");
        return this;
    }

    public GameInfoPrinter printStackState(DiskStackList diskStacks) {
        println();
        printSectionLine(1);
        println();
        println(diskStacks.toString());
        println();
        printSectionLine(1);
        println();
        return this;
    }

    public GameInfoPrinter printUnableToMoveDisk(String reason) {
        println("CAN'T MOVE: " + reason);
        return this;
    }

    public GameInfoPrinter printEmptyLine() {
        println();
        return this;
    }

    public GameInfoPrinter printUserEnteredLine(String line) {
        println(line);
        return this;
    }

    /**
     * @param importance 0 for most important
     */
    public GameInfoPrinter printSectionLine(int importance) {
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
                        "Nothing set for " + importance + " importance level");
        }
        println(new String(new char[WIDTH]).replace("\0", c));
        return this;
    }

    public GameInfoPrinter printGameSolverStateException(GameSolverStateException e) {
        println(CANT_SOLVE + e.getMessage());
        return this;
    }

    public GameInfoPrinter printCannotSolve() {
        println();
        return this;
    }

    public GameInfoPrinter printReadyForAction(DiskStackList diskStackList) {
        printStackState(diskStackList);
        printShortControls();
        return this;
    }

    public GameInfoPrinter printSolveSuccess() {
        println(GAME_HAS_BEEN_SOLVED);
        return this;
    }

    private void print(String s) {
        outputTextEvent.broadcast(s);
    }

    private void println(String s) {
        print(s + "\n");
    }

    private void println() {
        println("");
    }

}
