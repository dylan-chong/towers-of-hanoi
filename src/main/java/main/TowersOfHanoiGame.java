package main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// todo move the print welcome into a new class

/**
 * Created by Dylan on 30/12/16.
 */
public class TowersOfHanoiGame {

    private final TextPrintable out;
    private final InfoPrinter infoPrinter;

    private DiskStackList diskStacks;

    public TowersOfHanoiGame(TextPrintable messageOutput,
                             DiskStackList diskStacks) {
        this.out = messageOutput;
        this.diskStacks = diskStacks;
        this.infoPrinter = new InfoPrinter(messageOutput);

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

    /**
     * @return True iff a change was made to the diskStacks
     */
    public boolean onUserInputtedLine(String line) {
        out.println("Move from x to y: ");

        boolean didChange = false;
        try {
            List<Integer> stackNumbers = getStackNumbers(line);
            didChange = moveDisk(
                    stackNumbers.get(0) - 1,
                    stackNumbers.get(1) - 1);
        } catch (Exception e) {
            out.println(e.getMessage() + "\n");
        }
        infoPrinter.printStackState(diskStacks);
        return didChange;
    }

    private List<Integer> getStackNumbers(String line) throws Exception {
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

        return stackNumbers;
    }

    private boolean isInvalidStackForUserInput(int stackNum) {
        return stackNum < 1 || stackNum > diskStacks.numberOfStacks();
    }

    private boolean moveDisk(int fromStackIndex, int toStackIndex) {
        boolean didMove = false;
        try {
            int radius = diskStacks.moveDisk(fromStackIndex, toStackIndex);
            out.printf("MOVED DISK of size %d: from index %d to %d\n",
                    radius, fromStackIndex, toStackIndex);
            didMove = true;
        } catch (DiskMoveException e) {
            out.println("CAN'T MOVE: " + e.getMessage());
        }
        return didMove;
    }

}
