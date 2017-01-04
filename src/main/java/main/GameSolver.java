package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 3/01/17.
 */
public class GameSolver {
    private final TowersOfHanoiGame game;

    private GameSolver(TowersOfHanoiGame game) {
        this.game = game;
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

        moves.add(new Move(
                game.getStartingDiskStackIndex(),
                game.getFinalStackIndex()));

        return moves;
    }
}
