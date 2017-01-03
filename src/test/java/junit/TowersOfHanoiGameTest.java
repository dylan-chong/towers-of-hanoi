package junit;

import main.DiskMoveException;
import main.DiskStackList;
import main.GameInfoPrinter;
import main.TowersOfHanoiGame;
import main.textprinter.StringTextPrinter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dylan on 3/01/17.
 */
public class TowersOfHanoiGameTest {
    private TowersOfHanoiGame getNewTowersOfHanoiGame() {
        return new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(new StringBuilder())),
                new DiskStackList(3));
    }

    @Test
    public void getSuccessfulMoveCount_startingState_shouldBe0() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        Assert.assertEquals(0, game.getSuccessfulMoveCount());
    }

    @Test
    public void getSuccessfulMoveCount_1move_shouldBe1() throws DiskMoveException {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        game.moveDisk(0, 1);
        Assert.assertEquals(1, game.getSuccessfulMoveCount());
    }


    @Test
    public void getSuccessfulMoveCount_2moves_shouldBe2() throws DiskMoveException {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        game.moveDisk(0, 1);
        game.moveDisk(0, 2);
        Assert.assertEquals(2, game.getSuccessfulMoveCount());
    }

    @Test
    public void getSuccessfulMoveCount_moveDiskNowhere_shouldBe0() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        try {
            game.moveDisk(0, 0);
        } catch (DiskMoveException ignored) {}
        Assert.assertEquals(0, game.getSuccessfulMoveCount());
    }

    @Test
    public void getSuccessfulMoveCount_startingStateFailedMove_shouldBe0() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        try {
            game.moveDisk(1, 2); // no disk there
        } catch (DiskMoveException ignored) {}

        Assert.assertEquals(0, game.getSuccessfulMoveCount());
    }
}
