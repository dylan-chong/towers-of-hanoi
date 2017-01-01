package main;

import main.textprinter.TextPrinter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// todo move the print welcome into a new class

/**
 * Created by Dylan on 30/12/16.
 */
public class TowersOfHanoiGame {

    private final TextPrinter out;
    private final GameInfoPrinter gameInfoPrinter;

    private DiskStackList diskStacks;

    public TowersOfHanoiGame(TextPrinter messageOutput,
                             DiskStackList diskStacks) {
        this.out = messageOutput;
        this.diskStacks = diskStacks;
        this.gameInfoPrinter = new GameInfoPrinter(messageOutput);

        gameInfoPrinter.printWelcome();
        out.println();
        gameInfoPrinter.printInstructions();
        out.println();
        gameInfoPrinter.printControls();
        gameInfoPrinter.printStackState(diskStacks);

        gameInfoPrinter.printShortControls();
    }

    public DiskStackList getDiskStackList() {
        return diskStacks;
    }

    /**
     * @return True iff a change was made to the diskStacks
     */
    public boolean onUserInputtedLine(String line) {
        out.println(line);

        boolean didChange = false;
        try {
            List<Integer> stackNumbers = getStackNumbers(line);
            moveDisk(stackNumbers.get(0) - 1,
                    stackNumbers.get(1) - 1);
            didChange = true;
        } catch (UserInputFormatException e) {
            gameInfoPrinter.printUnableToMoveDisk(e.getMessage());
        } catch (DiskMoveException e) {
            gameInfoPrinter.printUnableToMoveDisk(e.getMessage());
        }
        gameInfoPrinter.printStackState(diskStacks);
        gameInfoPrinter.printShortControls();

        return didChange;
    }

    private List<Integer> getStackNumbers(String line) throws UserInputFormatException {
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
        if (stackNumbers.contains(null)) throw new UserInputFormatException(
                "Number format exception");
        if (stackNumbers.size() != 2) throw new UserInputFormatException(
                "Wrong number of arguments");
        if (stackNumbers.stream()
                .filter(this::isInvalidStackForUserInput)
                .count() > 0) throw new UserInputFormatException(
                        "Invalid stack number");

        return stackNumbers;
    }

    private boolean isInvalidStackForUserInput(int stackNum) {
        return stackNum < 1 || stackNum > diskStacks.numberOfStacks();
    }

    private void moveDisk(int fromStackIndex, int toStackIndex)
            throws DiskMoveException {
        diskStacks.moveDisk(fromStackIndex, toStackIndex);
    }

}
