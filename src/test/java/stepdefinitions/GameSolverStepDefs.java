package stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import main.DiskStackList;
import main.GameInfoPrinter;
import main.TowersOfHanoiGame;
import main.textprinter.StringTextPrinter;
import org.junit.Assert;

/**
 * Created by Dylan on 4/01/17.
 */
public class GameSolverStepDefs {
    private TowersOfHanoiGame game;
    private StringBuilder gameOutSB;

    private boolean isThereSolveErrorDisplayed() {
        return gameOutSB
                .toString()
                .contains(GameInfoPrinter.CANT_SOLVE);
    }

    @Given("^a new game with (\\d+) disks$")
    public void a_new_game_with_disks(int numDisks) throws Throwable {
        gameOutSB = new StringBuilder();
        game = new TowersOfHanoiGame(
                new GameInfoPrinter(new StringTextPrinter(gameOutSB)),
                new DiskStackList(numDisks));
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

}
