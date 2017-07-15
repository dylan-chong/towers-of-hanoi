package junit;

import main.event.Events;
import main.game.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dylan on 3/01/17.
 */
public class TowersOfHanoiGameTest {
    private TowersOfHanoiGame getNewTowersOfHanoiGame() {
        return new TowersOfHanoiGame(
                new GameInfoPrinter(new Events.OutputText()),
                new Events.AppReady(),
                new DiskStackList(3, new DefaultDiskStackFactory())
        );
    }

    @Test
    public void getSuccessfulMoveCount_startingState_shouldReturn0() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        Assert.assertEquals(0, game.getSuccessfulMoveCount());
    }

    @Test
    public void getSuccessfulMoveCount_1move_shouldReturn1() throws DiskMoveException {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        game.moveDisk(new Move(0, 1));
        Assert.assertEquals(1, game.getSuccessfulMoveCount());
    }


    @Test
    public void getSuccessfulMoveCount_2moves_shouldReturn2() throws DiskMoveException {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        game.moveDisk(new Move(0, 1));
        game.moveDisk(new Move(0, 2));
        Assert.assertEquals(2, game.getSuccessfulMoveCount());
    }

    @Test
    public void getSuccessfulMoveCount_moveDiskNowhere_shouldReturn0() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        try {
            game.moveDisk(new Move(0, 0));
        } catch (DiskMoveException ignored) {}
        Assert.assertEquals(0, game.getSuccessfulMoveCount());
    }

    @Test
    public void getSuccessfulMoveCount_startingStateFailedMove_shouldReturn0() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        try {
            game.moveDisk(new Move(1, 2)); // no disk there in starting state
        } catch (DiskMoveException ignored) {}

        Assert.assertEquals(0, game.getSuccessfulMoveCount());
    }

    @Test
    public void isSolved_startingState_shouldReturnFalse() {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        Assert.assertFalse(game.isSolved());
    }

    @Test
    public void isSolved_partiallySolved_shouldReturnFalse()
            throws DiskMoveException {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        // Solution for 3 disks with last few steps removed
        game.moveDisk(new Move(0, 2));
        game.moveDisk(new Move(0, 1));
        game.moveDisk(new Move(2, 1));
        game.moveDisk(new Move(0, 2));
        Assert.assertFalse(game.isSolved());
    }

    @Test
    public void isSolved_solvedState_shouldReturnTrue()
            throws DiskMoveException {
        TowersOfHanoiGame game = getNewTowersOfHanoiGame();
        // Solution for 3 disks
        game.moveDisk(new Move(0, 2));
        game.moveDisk(new Move(0, 1));
        game.moveDisk(new Move(2, 1));
        game.moveDisk(new Move(0, 2));
        game.moveDisk(new Move(1, 0));
        game.moveDisk(new Move(1, 2));
        game.moveDisk(new Move(0, 2));
        // ---
        Assert.assertTrue(game.isSolved());
    }
}
