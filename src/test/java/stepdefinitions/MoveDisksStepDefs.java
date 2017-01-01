package stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import main.DiskStack;
import main.DiskStackList;
import main.GameInfoPrinter;
import main.TowersOfHanoiGame;
import main.textprinter.StringTextPrinter;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dylan on 29/12/16.
 */
public class MoveDisksStepDefs {
    private TowersOfHanoiGame game;
    private StringTextPrinter gameOut;

    @Given("^a starting-game stack with (\\d+) disks and (\\d+) stacks$")
    public void aStartingGameStackWithDisksAndStacks(int numDisks,
                                                     int numStacks) throws Throwable {
        gameOut = new StringTextPrinter(new StringBuilder());
        game = new TowersOfHanoiGame(
                new GameInfoPrinter(gameOut),
                new DiskStackList(numStacks, numDisks));
    }

    @When("^the user moves a disk from stack (\\d+) to stack (\\d+)$")
    public void theUserMovesADiskFromStackToStack(int fromStackNum,
                                                  int toStackNum) throws Throwable {
        game.onUserInputtedLine(fromStackNum + " " + toStackNum);
    }

    @Then("^stack (\\d+) should have (\\d+) disks?$")
    public void stackShouldHaveDisks(int stackNum,
                                     int idealNumberOfDisks) throws Throwable {
        int numDisks = game.getDiskStackList()
                .getDiscStacks()
                .get(stackNum - 1)
                .getHeight();
        assertEquals(idealNumberOfDisks, numDisks);
    }

    @And("^all stacks except stack (\\d+) should have (\\d+) disks?$")
    public void allStacksExceptStackShouldHaveDisks(int excludedStackNum,
                                                    int numDisks) throws Throwable {
        List<DiskStack> stacks = game.getDiskStackList().getDiscStacks();
        for (int i = 0; i < stacks.size(); i++) {
            int diskHeight = stacks.get(i).getHeight();
            int excludedStackIndex = excludedStackNum - 1;
            assertTrue(i == excludedStackIndex || diskHeight == numDisks);
        }
    }

    @When("^the user tries to move a disk from stack (\\d+) to stack (\\d+) \\(expect fail\\)$")
    public void theUserTriesToMoveADiskFromStackToStackExpectFail(
            int fromStackNum, int toStackNum) throws Throwable {
        boolean didChangeSomething =
                game.onUserInputtedLine(fromStackNum + " " + toStackNum);
        assertFalse(didChangeSomething);
    }
}
