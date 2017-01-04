package main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 30/12/16.
 */
public class TowersOfHanoiGame {

    private static final String SOLVE_FUNCTION = "solve";
    private final GameInfoPrinter gameInfoPrinter;

    private DiskStackList diskStackList;
    private int successfulMoveCount = 0;

    public TowersOfHanoiGame(GameInfoPrinter gameInfoPrinter,
                             DiskStackList diskStackList) {
        this.gameInfoPrinter = gameInfoPrinter;
        this.diskStackList = diskStackList;

        gameInfoPrinter.printWelcome()
                .printEmptyLine()
                .printInstructions()
                .printEmptyLine()
                .printControls()
                .printStackState(diskStackList);

        gameInfoPrinter.printShortControls();
    }

    public void moveDisk(Move move) throws DiskMoveException {
        diskStackList.moveDisk(move);
        successfulMoveCount++; // called iff above line doesn't throw exception
        gameInfoPrinter.printReadyForAction(diskStackList);
    }

    /**
     * @throws DiskMoveException Will not complete all the moves if one throws
     *                           an exception
     */
    public void moveDisks(List<Move> moves) throws DiskMoveException {
        for (Move move : moves) {
            moveDisk(move);
        }
    }

    /**
     * @return True iff a change was made to the diskStackList
     */
    public boolean onUserInputtedLine(String line) {
        gameInfoPrinter.printUserEnteredLine(line);

        line = line.toLowerCase();

        if (line.equals(SOLVE_FUNCTION)) return solve();
        else return moveDisk(line);
    }

    private boolean moveDisk(String line) {
        boolean didChange = false;
        try {
            List<Integer> stackNumbers = getStackNumbers(line);
            moveDisk(new Move(
                    stackNumbers.get(0) - 1,
                    stackNumbers.get(1) - 1));
            didChange = true;
        } catch (UserInputFormatException | DiskMoveException e) {
            gameInfoPrinter.printUnableToMoveDisk(e.getMessage());
        }

        return didChange;
    }

    public boolean isSolved() {
        List<DiskStack> diskStacks = diskStackList.getDiskStacks();
        for (int i = 0; i < diskStacks.size(); i++) {
            DiskStack diskStack = diskStackList.getDiskStacks().get(i);
            if (i == getFinalStackIndex()) continue;
            if (diskStack.size() != 0) return false;
        }

        assert diskStacks.get(getFinalStackIndex()).size()
                == diskStackList.getNumberOfDisks();

        return true;
    }

    public int getStartingDiskStackIndex() {
        return 0;
    }

    public int getSpareStackIndex() {
        if (getNumberOfStacks() != 3) throw new RuntimeException();
        return 1;
    }

    public int getFinalStackIndex() {
        return diskStackList.getDiskStacks().size() - 1;
    }

    public int getNumberOfStacks() {
        return diskStackList.getNumberOfStacks();
    }

    public int getNumberOfDisks() {
        return diskStackList.getNumberOfDisks();
    }

    public GameSolver getNewSolver() {
        try {
            return GameSolver.createNewGameSolver(this);
        } catch (GameSolverStateException e) {
            gameInfoPrinter.printGameSolverStateException(e);
            return null;
        }
    }

    public boolean solve() {
        GameSolver solver = getNewSolver();
        if (solver == null) {
            gameInfoPrinter.printCannotSolve();
            gameInfoPrinter.printReadyForAction(diskStackList);
            return false;
        }

        try {
            moveDisks(solver.getSolutionMoves());
        } catch (DiskMoveException | GameSolverStateException e) {
            assert false : e;
        }
        assert isSolved();
        gameInfoPrinter.printSolveSuccess();
        return true;
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
        return stackNum < 1 || stackNum > diskStackList.getNumberOfStacks();
    }

    public int getSuccessfulMoveCount() {
        return successfulMoveCount;
    }


    public List<Disk> getAllDisks() {
        return diskStackList.getAllDisks();
    }
}
