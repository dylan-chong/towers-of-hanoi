package stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import main.event.Events;
import main.game.DefaultDiskStackFactory;
import main.game.DiskStackList;
import main.game.GameInfoPrinter;
import main.game.TowersOfHanoiGame;
import org.junit.Assert;

/**
 * Created by Dylan on 4/01/17.
 */
public class GameSolverStepDefs {
    TowersOfHanoiGame game;
    StringBuilder gameOutSB;
    Events.OutputText outputTextEvent;

    private boolean isThereSolveErrorDisplayed() {
        return gameOutSB.toString()
                .contains(GameInfoPrinter.CANT_SOLVE);
    }

    @Given("^a new game with (\\d+) disks$")
    public void a_new_game_with_disks(int numDisks) throws Throwable {
        gameOutSB = new StringBuilder();
        outputTextEvent = new Events.OutputText();
        outputTextEvent.registerListener(gameOutSB::append);

        Events.AppReady appReadyEvent = new Events.AppReady();
        game = new TowersOfHanoiGame(
                new GameInfoPrinter(outputTextEvent),
                appReadyEvent,
                new DiskStackList(numDisks, new DefaultDiskStackFactory())
        );
        appReadyEvent.broadcast(null);
    }

    @And("^no solve error should be displayed to the user$")
    public void noSolveErrorShouldBeDisplayedToTheUser() throws Throwable {
        Assert.assertFalse(isThereSolveErrorDisplayed());
    }

    @When("^the user enters the command \"([^\"]*)\"$")
    public void theUserEntersTheCommand(String command) throws Throwable {
        game.onUserInputtedLine(command);
    }

    @Then("^the game should not be in the solved state$")
    public void theGameShouldNotBeInTheSolvedState() throws Throwable {
        Assert.assertFalse(game.isSolved());
    }

    @And("^a solve error should be displayed to the user$")
    public void anErrorShouldBeDisplayedToTheUser() throws Throwable {
        Assert.assertTrue(isThereSolveErrorDisplayed());
    }

    @Then("^the game should be solved$")
    public void theGameShouldBeSolved() throws Throwable {
        Assert.assertTrue(game.isSolved());
    }

    @And("^a solve success message should be displayed to the user$")
    public void aSolveSuccessMessageShouldBeDisplayedToTheUser() throws Throwable {
        Assert.assertTrue(gameOutSB.toString()
                .contains(GameInfoPrinter.GAME_HAS_BEEN_SOLVED));
    }
}
