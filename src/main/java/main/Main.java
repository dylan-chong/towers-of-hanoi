package main;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 27/11/16.
 */
public class Main {

    public static void main(String[] args) {
        Main game = new Main();
    }

    // *********************************************************


    private static final String TITLE_PREFIX = "***************";
    static final String TITLE = TITLE_PREFIX + " WELCOME TO TOWERS OF HANOI "
            + TITLE_PREFIX;
    private static final int DEFAULT_NUM_STACKS = 3;

    private DiskStackList diskStacks;

    private Main() {
        diskStacks = new DiskStackList(DEFAULT_NUM_STACKS, 3);

        printWelcome();
        O.pln();
        printInstructions();
        O.pln();
        printControls();
        printStackState();

        Scanner systemInput = new Scanner(System.in);

        while (true) {
            O.pln("Move from x to y: ");

            String ln = systemInput.nextLine();
            try {
                consumeUserInput(ln);
            } catch (Exception e) {
                O.pln(e.getMessage() + "\n");
            }
            printStackState();
        }
    }

    private void printWelcome() {
        // Push everything to bottom of screen
        for (int l = 0; l < 300; l++) O.pln();

        printSectionLine(0);
        O.pln(TITLE);
        printSectionLine(0);
    }

    private void printInstructions() {
        O.pln("Objective: Get everything to the right stack");
    }

    private void printControls() {
        O.pln("Controls: Enter '1 3' to move from the left stack to the 3rd stack");
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
            O.pln("CAN'T MOVE: " + e.getMessage());
        }
    }

    /**
     *
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
        O.pln(
                new String(new char[O.CONSOLE_WIDTH]).replace("\0", c)
        );
    }

    private void printStackState() {
        O.pln();
        printSectionLine(1);
        O.pln();
        O.pln(diskStacks.toString());
        O.pln();
        printSectionLine(1);
        O.pln();
    }
}
