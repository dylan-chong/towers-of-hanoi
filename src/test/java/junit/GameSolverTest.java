package junit;

import main.*;
import main.textprinter.StringTextPrinter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dylan on 1/01/17.
 */
public class GameSolverTest {
    private TowersOfHanoiGame getNewTowersOfHanoiGame() {
        return getNewTowersOfHanoiGame(3);
    }

    private TowersOfHanoiGame getNewTowersOfHanoiGame(int numDisks) {
        return new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
                new DiskStackList(numDisks));
    }

    @Test
    public void getSolutionMoves_partiallyPlayedGame_shouldRefuseToSolve()
            throws DiskMoveException {

        TowersOfHanoiGame towersOfHanoiGame = getNewTowersOfHanoiGame();
        GameSolver gameSolver = new GameSolver(towersOfHanoiGame);

        towersOfHanoiGame.moveDisk(new Move(0, 1));

        try {
            gameSolver.getSolutionMoves();
            Assert.fail("Expected exception");
        } catch (GameSolverStateException ignored) {
        }
    }
}
