package stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import main.event.Events;
import main.game.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dylan on 29/12/16.
 */
public class MoveDisksStepDefs {
    TowersOfHanoiGame game;
    DiskStackList diskStackList;

    @Given("^a starting-game stack with (\\d+) disks and (\\d+) stacks$")
    public void aStartingGameStackWithDisksAndStacks(int numDisks,
                                                     int numStacks) throws Throwable {
        diskStackList = new DiskStackList(
                numStacks,
                numDisks,
                new DefaultDiskStackFactory()
        );
        Events.AppReady appReadyEvent = new Events.AppReady();
        game = new TowersOfHanoiGame(
                new GameInfoPrinter(new Events.OutputText()),
                appReadyEvent,
                diskStackList
        );
        appReadyEvent.broadcast(null);
    }

    @When("^the user moves a disk from stack (\\d+) to stack (\\d+)$")
    public void theUserMovesADiskFromStackToStack(int fromStackNum,
                                                  int toStackNum) throws Throwable {
        game.onUserInputtedLine(fromStackNum + " " + toStackNum);
    }

    @Then("^stack (\\d+) should have (\\d+) disks?$")
    public void stackShouldHaveDisks(int stackNum,
                                     int idealNumberOfDisks) throws Throwable {
        int numDisks = diskStackList
                .getDiskStacks()
                .get(stackNum - 1)
                .getHeight();
        assertEquals(idealNumberOfDisks, numDisks);
    }

    @And("^all stacks except stack (\\d+) should have (\\d+) disks?$")
    public void allStacksExceptStackShouldHaveDisks(int excludedStackNum,
                                                    int numDisks) throws Throwable {
        List<DiskStack> stacks = diskStackList.getDiskStacks();
        for (int i = 0; i < stacks.size(); i++) {
            int diskHeight = stacks.get(i).getHeight();
            int excludedStackIndex = excludedStackNum - 1;
            assertTrue(i == excludedStackIndex || diskHeight == numDisks);
        }
    }

    @When("^the user tries to move a disk from stack (\\d+) to stack (\\d+) \\(expect fail\\)$")
    public void theUserTriesToMoveADiskFromStackToStackExpectFail(
            int fromStackNum, int toStackNum) throws Throwable {

        int numberOfMoves = game.getSuccessfulMoveCount();
        game.onUserInputtedLine(fromStackNum + " " + toStackNum);
        int newNumberOfMoves = game.getSuccessfulMoveCount();
        assertEquals(numberOfMoves, newNumberOfMoves);
    }
}
