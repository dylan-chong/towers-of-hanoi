package junit;

import main.*;
import main.textprinter.StringTextPrinter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 4/01/17.
 */
@RunWith(Parameterized.class)
public class GameSolverDifferentNumDisksTest {
    @Parameterized.Parameter
    public int numberOfDisksToTrySolveGame;

    @Parameterized.Parameters(name = "numberOfDisksToTrySolveGame == {0}")
    public static Iterable<Integer[]> data() {
        return Arrays.asList(new Integer[][]{{1}, {2}});
    }

    private TowersOfHanoiGame getNewTowersOfHanoiGame(int numDisks) {
        return new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
                new DiskStackList(numDisks));
    }

    @Test
    public void getSolutionMoves_startingStateWithNDisks_shouldBeSolved()
            throws GameSolverStateException, DiskMoveException {
        TowersOfHanoiGame towersOfHanoiGame =
                getNewTowersOfHanoiGame(numberOfDisksToTrySolveGame);

        GameSolver gameSolver = new GameSolver(towersOfHanoiGame);
        List<Move> moves = gameSolver.getSolutionMoves();
        towersOfHanoiGame.moveDisks(moves);

        Assert.assertTrue(towersOfHanoiGame.isSolved());
    }
}
