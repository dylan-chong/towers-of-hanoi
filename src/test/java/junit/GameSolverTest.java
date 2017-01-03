package junit;

import main.*;
import main.textprinter.StringTextPrinter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dylan on 1/01/17.
 */
public class GameSolverTest {
    // @Test
    // public void solveGame_startingStateWith1Disk_shouldBeSolved() {
    //     TowersOfHanoiGame towersOfHanoiGame = new TowersOfHanoiGame(
    //             new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
    //             new DiskStackList(1));
    //
    //     GameSolver gameSolver = new GameSolver(towersOfHanoiGame);
    // }

    @Test
    public void solveGame_partiallyPlayedGame_shouldRefuseToSolve() {
        TowersOfHanoiGame towersOfHanoiGame = new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
                new DiskStackList(1));

        GameSolver gameSolver = new GameSolver(towersOfHanoiGame);
        try {
            gameSolver.solve();
            Assert.fail("Expected exception");
        } catch (GameSolverStateException ignored) {}
    }
}
