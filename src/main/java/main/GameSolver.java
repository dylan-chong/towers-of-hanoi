package main;

import java.util.List;

/**
 * Created by Dylan on 3/01/17.
 */
public class GameSolver {
    private final TowersOfHanoiGame towersOfHanoiGame;

    public GameSolver(TowersOfHanoiGame towersOfHanoiGame) {
        this.towersOfHanoiGame = towersOfHanoiGame;
    }

    public List<Move> getSolutionMoves() {
        if (towersOfHanoiGame.getSuccessfulMoveCount() != 0)
            throw new GameSolverStateException();

        return null;
    }
}
