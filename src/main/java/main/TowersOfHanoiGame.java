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

    private final PrintStream out;
    private final InfoPrinter infoPrinter;

    private DiskStackList diskStacks;

    public TowersOfHanoiGame(PrintStream messageOutputStream,
                             DiskStackList diskStacks) {
        this.out = messageOutputStream;
        this.diskStacks = diskStacks;
        this.infoPrinter = new InfoPrinter(messageOutputStream);

        infoPrinter.printWelcome();
        out.println();
        infoPrinter.printInstructions();
        out.println();
        infoPrinter.printControls();
        infoPrinter.printStackState(diskStacks);
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
        infoPrinter.printStackState(diskStacks);
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

        moveDisk(stackNumbers.get(0) - 1, stackNumbers.get(1) - 1);
    }

    private boolean isInvalidStackForUserInput(int stackNum) {
        return stackNum < 1 || stackNum > diskStacks.numberOfStacks();
    }

    private void moveDisk(int fromStackIndex, int toStackIndex) {
        try {
            int radius = diskStacks.moveDisk(fromStackIndex, toStackIndex);
            out.printf("MOVED DISK of size %d: from index %d to %d\n",
                    radius, fromStackIndex, toStackIndex);
        } catch (DiskMoveException e) {
            out.println("CAN'T MOVE: " + e.getMessage());
        }
    }

}
