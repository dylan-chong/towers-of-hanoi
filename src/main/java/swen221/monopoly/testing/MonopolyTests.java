package swen221.monopoly.testing;
import org.junit.Test;
import swen221.monopoly.Board;
import swen221.monopoly.GameOfMonopoly;
import swen221.monopoly.Player;
import swen221.monopoly.locations.Location;
import swen221.monopoly.locations.Property;
import swen221.monopoly.locations.Street;

import static org.junit.Assert.*;

/**
 * Use non-crappy naming format methodName_state_expectedResult for tests.
 * See (http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html)
 */
public class MonopolyTests {
	// this is where you must write your tests; do not alter the package, or the
    // name of this file.  An example test is provided for you.
	
	@Test
	public void testValidBuyProperty_1() throws Exception {
		// Construct a "mini-game" of Monopoly and with a single player. The
		// player attempts to buy a property. We check that the right amount has
		// been deducted from his/her balance, and that he/she now owns the
		// property and vice-versa.
		GameOfMonopoly game = new GameOfMonopoly();
		Player player = setupMockPlayer(game,"Park Lane",1500);
		game.buyProperty(player);
		assertEquals(1150,player.getBalance());
		assertEquals("Park Lane",player.iterator().next().getName());
		Street street = (Street) game.getBoard().findLocation("Park Lane");
		assertEquals(player,street.getOwner());
	}

	@Test
	public void movePlayer_oneSquareFromGo_playerIsOnOldKent() {
		testMovePlayer("Go", "Old Kent Road", 1);
	}

	@Test
	public void movePlayer_oneSquareFromMayfairBeforeGo_playerIsOnGo() {
		testMovePlayer("Mayfair", "Go", 1);
	}

	@Test
	public void movePlayer_sixSquaresFromGo_playerIsOnAngelIslington() {
		testMovePlayer("Go", "The Angel Islington", 6);
	}

	@Test(expected = IllegalArgumentException.class)
	public void movePlayer_0DiceRoll_fail() {
		testMovePlayer("Go", "", 0);
	}

	private void testMovePlayer(String startLocationName,
								String endLocationName,
								int diceRoll) {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Player player = setupMockPlayer(gameOfMonopoly, startLocationName, 100);

		gameOfMonopoly.movePlayer(player, diceRoll);

		Location location = player.getLocation();
		assertEquals(endLocationName, location.getName());
	}

	@Test
	public void movePlayer_onToNonOwnedSquare_noCharge() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Property euston = (Property) gameOfMonopoly.getBoard().findLocation("Pentonville");
		assertFalse(euston.hasOwner()); // make sure test is sane
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Euston Road", startMoney);

		gameOfMonopoly.movePlayer(player, 1);

		assertEquals(startMoney, player.getBalance());
	}

	// ehh screw it ill just duplicate the code.

	@Test
	public void movePlayer_fromGo_moneyDoesntIncrease() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Go", startMoney);

		gameOfMonopoly.movePlayer(player, 1);

		assertEquals(startMoney, player.getBalance());
	}

	@Test
	public void movePlayer_ontoGo_moneyIncreases() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);

		gameOfMonopoly.movePlayer(player, 1);

		assertEquals(startMoney + 200, player.getBalance());
	}

	// NaCl

	@Test
	public void movePlayer_throughGo_moneyIncreases() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);

		gameOfMonopoly.movePlayer(player, 2);

		assertEquals(startMoney + 200, player.getBalance());
	}

	@Test
	public void movePlayer_onToSelfOwnedSquare_noCharge() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Property euston = (Property) gameOfMonopoly.getBoard().findLocation("Pentonville");
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Euston Road", startMoney);
		euston.setOwner(player);

		gameOfMonopoly.movePlayer(player, 1);

		assertEquals(startMoney, player.getBalance());
	}

	// the 221 assignment writers duplicate code stupidly, so it must be a good
	// practice! I shall adjust my code style accordingly!

	@Test
	public void movePlayer_onToSquareOwnedByAnotherPlayer_moneyTransferred() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Property pent = (Property) gameOfMonopoly.getBoard().findLocation("Pentonville");
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Euston Road", startMoney);
		Player enemy = setupMockPlayer(gameOfMonopoly, "Euston Road", startMoney);
		pent.setOwner(enemy);

		int diceRoll = 1;
		gameOfMonopoly.movePlayer(player, diceRoll);

		int rent = pent.getRent(diceRoll);
		assertEquals(startMoney - rent, player.getBalance());
		assertEquals(startMoney + rent, enemy.getBalance());
	}

	// because we all know the assignment writers have the best code style!

	@Test
	public void playerBuy_unownedProperty_propertyIsOwnedAndMoneyDecreased() throws Exception {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");
		assertFalse(mayfair.hasOwner()); // ensure test is sane

		gameOfMonopoly.buyProperty(player);

		assertTrue(mayfair.hasOwner());
		assertEquals(player, mayfair.getOwner());
		assertEquals(startMoney - mayfair.getPrice(), player.getBalance());
	}

	@Test
	public void buyProperty_ownedProperty_error() throws GameOfMonopoly.InvalidMove {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);
		Player enemy = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);
		gameOfMonopoly.buyProperty(enemy);

		try {
			gameOfMonopoly.buyProperty(player);
			fail();
		} catch (GameOfMonopoly.InvalidMove ignored) {}
	}

    // I'm getting the hand of this duplicating code thing! my code is the best now!

	@Test
	public void buyProperty_notEnoughMoney_error() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);

		try {
			gameOfMonopoly.buyProperty(player);
			fail();
		} catch (GameOfMonopoly.InvalidMove ignored) {}
	}

	@Test
	public void sellProperty_owned_moneyIncreases() throws GameOfMonopoly.InvalidMove {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");
		gameOfMonopoly.buyProperty(player);

		gameOfMonopoly.sellProperty(player, mayfair);
		assertEquals(startMoney, player.getBalance());
	}

	@Test
	public void sellProperty_unowned_error() throws GameOfMonopoly.InvalidMove {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		final int startMoney = 1000;
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", startMoney);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");

		try {
			gameOfMonopoly.sellProperty(player, mayfair);
			fail();
		} catch (GameOfMonopoly.InvalidMove ignored) {}
	}

	@Test
	public void morgageProperty_ownedProperty_moneyIncreasesAndStillOwn() throws GameOfMonopoly.InvalidMove {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", 1000);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");
		gameOfMonopoly.buyProperty(player);
		int balance = player.getBalance();

		gameOfMonopoly.mortgageProperty(player, mayfair);

		assertEquals(balance + mayfair.getPrice() / 2, player.getBalance());
		assertEquals(player, mayfair.getOwner());
		assertTrue(mayfair.isMortgaged());
	}

	@Test
	public void mortgageProperty_nonOwnedProperty_error() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", 1000);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");

		try {
			gameOfMonopoly.mortgageProperty(player, mayfair);
		} catch (GameOfMonopoly.InvalidMove ignored) {}
	}

	@Test
	public void mortgageProperty_mortgagedProperty_error() {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", 1000);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");
		mayfair.mortgage();

		try {
			gameOfMonopoly.mortgageProperty(player, mayfair);
		} catch (GameOfMonopoly.InvalidMove ignored) {}
	}

	@Test
	public void unmortgageProperty_ownedProperty_moneyGoesDown() throws GameOfMonopoly.InvalidMove {
		GameOfMonopoly gameOfMonopoly = new GameOfMonopoly();
		Player player = setupMockPlayer(gameOfMonopoly, "Mayfair", 1000);
		Property mayfair = (Property) gameOfMonopoly.getBoard().findLocation("Mayfair");
		gameOfMonopoly.buyProperty(player);
		mayfair.mortgage();
		int balance = player.getBalance();

		gameOfMonopoly.unmortgageProperty(player, mayfair);

		assertEquals(balance - 220, player.getBalance());
		assertEquals(player, mayfair.getOwner());
		assertFalse(mayfair.isMortgaged());
	}

	// todo sell
	// todo thi color
	// todo build house/hotel
	// todo coverage

	/**
	 * Setup a mock game of monopoly with a player located at a given location.
	 */
	private Player setupMockPlayer(GameOfMonopoly game, String locationName, int balance) {
		Board board = game.getBoard();
		Location location = board.findLocation(locationName);
		return new Player("Dave", Player.Token.ScottishTerrier, balance, location);
	}

}
