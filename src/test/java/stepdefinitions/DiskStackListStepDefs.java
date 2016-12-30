package stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import main.DiskStack;
import main.DiskStackList;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dylan on 29/12/16.
 */
public class DiskStackListStepDefs {
    private DiskStackList diskStackList;

    @Given("^a starting-game stack with (\\d+) disks and (\\d+) stacks$")
    public void aStartingGameStackWithDisksAndStacks(int numDisks,
                                                     int numStacks) throws Throwable {
        diskStackList = new DiskStackList(numStacks, numDisks);
    }

    @When("^the user moves a disk from stack (\\d+) to stack (\\d+)$")
    public void theUserMovesADiskFromStackToStack(int fromStackNum,
                                                  int toStackNum) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        diskStackList.moveDisk(fromStackNum - 1, toStackNum - 1); // todo should really mock out entire game
    }

    @Then("^stack (\\d+) should have (\\d+) disks?$")
    public void stackShouldHaveDisks(int stackNum,
                                     int idealNumberOfDisks) throws Throwable {
        int numDisks = diskStackList.getDiscStacks()
                .get(stackNum - 1)
                .getHeight();
        assertEquals(idealNumberOfDisks, numDisks);
    }

    @And("^all stacks except stack (\\d+) should have (\\d+) disks?$")
    public void allStacksExceptStackShouldHaveDisks(int excludedStackNum,
                                                    int numDisks) throws Throwable {
        List<DiskStack> stacks = diskStackList.getDiscStacks();
        for (int i = 0; i < stacks.size(); i++) {
            int diskHeight = stacks.get(i).getHeight();
            int excludedStackIndex = excludedStackNum - 1;
            assertTrue(i == excludedStackIndex || diskHeight == numDisks);
        }
    }
}
