package main;

/**
 * Created by Dylan on 3/01/17.
 */
public class GameSolver {
    private final TowersOfHanoiGame towersOfHanoiGame;

    public GameSolver(TowersOfHanoiGame towersOfHanoiGame) {
        this.towersOfHanoiGame = towersOfHanoiGame;
    }

    public void solve() throws GameSolverStateException {
        towersOfHanoiGame.getSuccessfulMoveCount();
    }
}
