package main;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// todo move the print welcome into a new class

/**
 * Created by Dylan on 30/12/16.
 */
public class TowersOfHanoiGame {
    private static final String TITLE_PREFIX = "***************";
    static final String TITLE =
            TITLE_PREFIX + " WELCOME TO TOWERS OF HANOI " + TITLE_PREFIX;

    private final PrintStream out;

    private DiskStackList diskStacks;

    public TowersOfHanoiGame(PrintStream messageOutputStream,
                             DiskStackList diskStacks) {
        this.out = messageOutputStream;
        this.diskStacks = diskStacks;

        printWelcome();
        out.println();
        printInstructions();
        out.println();
        printControls();
        printStackState();
    }

    public DiskStackList getDiskStackList() {
        return diskStacks;
    }

    public void onUserInputtedLine(String line) {
        out.println("Move from x to y: ");
        try {
            consumeUserInput(line);
        } catch (Exception e) {
            out.println(e.getMessage() + "\n");
        }
        printStackState();
    }

    private void printWelcome() {
        // Push everything to bottom of screen
        for (int l = 0; l < 300; l++) out.println();

        printSectionLine(0);
        out.println(TITLE);
        printSectionLine(0);
    }

    private void printInstructions() {
        out.println("Objective: Get everything to the right stack");
    }

    private void printControls() {
        out.println("Controls: Enter '1 3' to move from the left stack to the 3rd stack");
    }

    private void consumeUserInput(String line) throws Exception {
        List<Integer> stackNumbers = Arrays.stream(line.split(" "))
                .filter(token -> !token.equals(""))
                .map(token -> {
                    try {
                        return Integer.parseInt(token);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());

        // Validate input
        if (stackNumbers.contains(null)) throw new Exception(
                "Number format exception");
        if (stackNumbers.size() != 2) throw new Exception(
                "Wrong number of arguments");
        if (stackNumbers.stream()
                .filter(this::isInvalidStackForUserInput)
                .count() > 0) throw new Exception("Invalid stack number");
        // if (stackNumbers.)

        moveDisk(stackNumbers.get(0) - 1, stackNumbers.get(1) - 1);
    }

    private boolean isInvalidStackForUserInput(int stackNum) {
        return stackNum < 1 || stackNum > diskStacks.numberOfStacks();
    }

    private void moveDisk(int fromStackIndex, int toStackIndex) {
        try {
            int radius = diskStacks.moveDisk(fromStackIndex, toStackIndex);
            O.pf("MOVED DISK of size %d: from index %d to %d\n",
                    radius, fromStackIndex, toStackIndex);
        } catch (DiskMoveException e) {
            out.println("CAN'T MOVE: " + e.getMessage());
        }
    }

    /**
     * @param importance 0 for most important
     */
    private void printSectionLine(int importance) {
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
                new String(new char[O.CONSOLE_WIDTH]).replace("\0", c)
        );
    }

    private void printStackState() {
        out.println();
        printSectionLine(1);
        out.println();
        out.println(diskStacks.toString());
        out.println();
        printSectionLine(1);
        out.println();
    }
}
