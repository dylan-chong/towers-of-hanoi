package main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 30/12/16.
 */
public class TowersOfHanoiGame {

    private final GameInfoPrinter gameInfoPrinter;

    private DiskStackList diskStacks;
    private int successfulMoveCount = 0;

    public TowersOfHanoiGame(GameInfoPrinter gameInfoPrinter,
                             DiskStackList diskStacks) {
        this.gameInfoPrinter = gameInfoPrinter;
        this.diskStacks = diskStacks;

        gameInfoPrinter.printWelcome()
                .printEmptyLine()
                .printInstructions()
                .printEmptyLine()
                .printControls()
                .printStackState(diskStacks);

        gameInfoPrinter.printShortControls();
    }

    public DiskStackList getDiskStackList() {
        return diskStacks;
    }

    public void moveDisk(int fromStackIndex, int toStackIndex)
            throws DiskMoveException {
        diskStacks.moveDisk(fromStackIndex, toStackIndex);
        successfulMoveCount++; // called iff above line doesn't throw exception
    }

    /**
     * @return True iff a change was made to the diskStacks
     */
    public boolean onUserInputtedLine(String line) {
        gameInfoPrinter.printUserEnteredLine(line);

        boolean didChange = false;
        try {
            List<Integer> stackNumbers = getStackNumbers(line);
            moveDisk(stackNumbers.get(0) - 1,
                    stackNumbers.get(1) - 1);
            didChange = true;
        } catch (UserInputFormatException | DiskMoveException e) {
            gameInfoPrinter.printUnableToMoveDisk(e.getMessage());
        }
        gameInfoPrinter.printStackState(diskStacks)
                .printShortControls();

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

    public int getSuccessfulMoveCount() {
        return successfulMoveCount;
    }
}
