package junit;

import main.*;
import main.textprinter.StringTextPrinter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Dylan on 1/01/17.
 */
public class GameSolverTest {
    private TowersOfHanoiGame getNewTowersOfHanoiGame() {
        return new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
                new DiskStackList(3));
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
        } catch (GameSolverStateException ignored) {}
    }

    @Test
    public void getSolutionMoves_startingStateWith1Disk_shouldBeSolved()
            throws GameSolverStateException, DiskMoveException {

        TowersOfHanoiGame towersOfHanoiGame = new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
                new DiskStackList(1));

        GameSolver gameSolver = new GameSolver(towersOfHanoiGame);
        List<Move> moves = gameSolver.getSolutionMoves();

        towersOfHanoiGame.moveDisks(moves);

        Assert.assertTrue(towersOfHanoiGame.isSolved());
    }
}
