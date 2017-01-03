package main;

/**
 * To be used when the {@link GameSolver} class is given a
 * {@link TowersOfHanoiGame} that is not in its starting state.
 *
 * Created by Dylan on 3/01/17.
 */
public class GameSolverStateException extends RuntimeException {
    public GameSolverStateException(String message) {
        super(message);
    }
}
