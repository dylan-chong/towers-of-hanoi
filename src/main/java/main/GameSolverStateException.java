package main;

/**
 * To be used when the {@link GameSolver} class is given a
 * {@link TowersOfHanoiGame} that is not in its starting state.
 *
 * Created by Dylan on 3/01/17.
 */
public class GameSolverStateException extends Exception {
    public GameSolverStateException(String message) {
        super(message);
    }
}
