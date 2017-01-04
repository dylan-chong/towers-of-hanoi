package main;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Dylan on 3/01/17.
 */
public class GameSolver {
    private final TowersOfHanoiGame game;
    private final List<Disk> allDisks;

    private GameSolver(TowersOfHanoiGame game) {
        this.game = game;
        this.allDisks = game.getAllDisks();
    }

    private static void checkGameIsValid(TowersOfHanoiGame game)
            throws GameSolverStateException {
        if (game.getSuccessfulMoveCount() != 0)
            throw new GameSolverStateException(
                    "Only works from the starting state");
        if (game.getNumberOfStacks() < 3)
            throw new GameSolverStateException(
                    "Only works when there are 3 or more stacks");
    }

    static GameSolver createNewGameSolver(TowersOfHanoiGame game)
            throws GameSolverStateException {
        checkGameIsValid(game);
        return new GameSolver(game);
    }

    public List<Move> getSolutionMoves() throws GameSolverStateException {
        checkGameIsValid(game);

        List<Move> moves = new ArrayList<>();
        if (game.getNumberOfDisks() == 0) return moves;

        moveDiskAndAllDisksAboveIt(allDisks.size() - 1,
                game.getStartingDiskStackIndex(),
                game.getFinalStackIndex(),
                game.getSpareStackIndex(),
                moves::add);

        return moves;
    }

    /**
     * @param diskI      Index of the disk in allDisks
     * @param srcStackI  Index of the stack containing the disk diskI
     * @param destStackI Index of stack to move
     * @param tempStackI Index of stack to temporarily move disks
     * @param addMove    Function to add moves to the solution list
     */
    private void moveDiskAndAllDisksAboveIt(int diskI,
                                            int srcStackI,
                                            int destStackI,
                                            int tempStackI,
                                            Consumer<Move> addMove) {
        if (diskI == 0) {
            // Base case
            addMove.accept(new Move(srcStackI, destStackI));
            return;
        }

        // Move everything larger than diskI to tempStackI
        moveDiskAndAllDisksAboveIt(diskI - 1,
                srcStackI, // from
                tempStackI, // to
                destStackI, // temp
                addMove);

        // Move diskI to destination
        addMove.accept(new Move(srcStackI, destStackI));

        // Move everything larger than diskI on top of diskI
        moveDiskAndAllDisksAboveIt(diskI - 1,
                tempStackI, // from
                destStackI, // to
                srcStackI, // temp
                addMove);
    }
}
