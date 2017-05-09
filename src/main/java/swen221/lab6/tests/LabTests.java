package swen221.lab6.tests;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import swen221.lab6.connect.Game;
import swen221.lab6.connect.core.Board;
import swen221.lab6.connect.util.Position;

import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LabTests {

	@Test
	public void test_01() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n";

		Board board = new Board();
		try {
			if (!output.equals(board.toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// ==================================
	// Valid Moves
	// ==================================

	@Test
	public void test_02() {
		String output = "|W|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_03() {
		String output = "|_|_|_|W|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(3, 0), Board.Token.WHITE);
			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_04() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|W|_|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 3), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_05() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|W|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(3, 3), Board.Token.WHITE);
			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_06() {
		String output = "|W|_|_|_|\n" + "|_|B|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// ==================================
	// Invalid Moves
	// ==================================

	@Test
	public void test_07() {
		try {
			new Position(0, -1);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_08() {
		try {
			new Position(-1, 0);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_09() {
		try {
			new Position(0, 4);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_10() {
		try {
			new Position(4, 0);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_11() {
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(0, 0), Board.Token.BLACK);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_12() {
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_13() {
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 0), Board.Token.BLACK);
			game.placeToken(new Position(2, 0), Board.Token.BLACK);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_14() {
		String output = "|W|B|B|B|\n" + "|W|_|_|W|\n" + "|W|_|_|W|\n" + "|B|B|B|W|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 0), Board.Token.BLACK);
			game.placeToken(new Position(0, 1), Board.Token.WHITE);
			game.placeToken(new Position(2, 0), Board.Token.BLACK);
			game.placeToken(new Position(0, 2), Board.Token.WHITE);
			game.placeToken(new Position(3, 0), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);
			game.placeToken(new Position(0, 3), Board.Token.BLACK);
			game.placeToken(new Position(3, 2), Board.Token.WHITE);
			game.placeToken(new Position(1, 3), Board.Token.BLACK);
			game.placeToken(new Position(3, 1), Board.Token.WHITE);
			game.placeToken(new Position(2, 3), Board.Token.BLACK);
			game.placeToken(new Position(1, 1), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(1, 1), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);

			game.placeToken(new Position(1, 1), Board.Token.WHITE);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	@Test
	public void test_15() {
		String output = "|W|W|W|W|\n" + "|B|B|B|B|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(0, 1), Board.Token.BLACK);
			game.placeToken(new Position(1, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(2, 0), Board.Token.WHITE);
			game.placeToken(new Position(2, 1), Board.Token.BLACK);
			game.placeToken(new Position(3, 0), Board.Token.WHITE);
			game.placeToken(new Position(3, 1), Board.Token.BLACK);
			fail();
		} catch (IllegalArgumentException e) {

		}
	}

	// ==================================
	// Capture Moves
	// ==================================

	// Short horizontal (left)
	@Test
	public void test_16() {
		String output = "|W|_|W|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 0), Board.Token.BLACK);
			game.placeToken(new Position(2, 0), Board.Token.WHITE);
			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Short horizontal (right)
	@Test
	public void test_17() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|W|_|W|\n" + "|_|_|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(1, 2), Board.Token.WHITE);
			game.placeToken(new Position(2, 2), Board.Token.BLACK);
			game.placeToken(new Position(3, 2), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Short vertical (top)
	@Test
	public void test_18() {
		String output = "|_|W|_|_|\n" + "|_|_|_|_|\n" + "|_|W|_|_|\n" + "|_|_|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(1, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(1, 2), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Short vertical (bottom)
	@Test
	public void test_19() {
		String output = "|_|_|_|_|\n" + "|_|W|_|_|\n" + "|_|_|_|_|\n" + "|_|W|_|_|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(1, 1), Board.Token.WHITE);
			game.placeToken(new Position(1, 2), Board.Token.BLACK);
			game.placeToken(new Position(1, 3), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Long horizontal (left)
	@Test
	public void test_20() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|W|_|_|W|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 3), Board.Token.WHITE);
			game.placeToken(new Position(1, 3), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Long horizontal (right)
	@Test
	public void test_21() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|W|_|_|W|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 3), Board.Token.WHITE);
			game.placeToken(new Position(2, 3), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Long vertical (top)
	@Test
	public void test_22() {
		String output = "|_|_|_|W|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|W|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(3, 0), Board.Token.WHITE);
			game.placeToken(new Position(3, 1), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Long vertical (bottom)
	@Test
	public void test_23() {
		String output = "|_|_|_|W|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|W|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(3, 0), Board.Token.WHITE);
			game.placeToken(new Position(3, 2), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Mutual vertical
	@Test
	public void test_24() {
		String output = "|_|_|_|W|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|B|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(3, 0), Board.Token.WHITE);
			game.placeToken(new Position(3, 3), Board.Token.BLACK);
			game.placeToken(new Position(3, 2), Board.Token.WHITE);
			game.placeToken(new Position(3, 1), Board.Token.BLACK);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// Mutual horizontal
	@Test
	public void test_25() {
		String output = "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|_|_|_|_|\n" + "|W|_|_|B|\n";

		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 3), Board.Token.WHITE);
			game.placeToken(new Position(3, 3), Board.Token.BLACK);
			game.placeToken(new Position(2, 3), Board.Token.WHITE);
			game.placeToken(new Position(1, 3), Board.Token.BLACK);

			if (!output.equals(game.getBoard().toString())) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	// ==================================
	// Game Over Moves
	// ==================================

	@Test
	public void test_26() {
		String output = "|W|W|W|W|\n" + "|B|B|B|B|\n" + "|_|_|_|_|\n" + "|_|_|_|W|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(0, 1), Board.Token.BLACK);
			game.placeToken(new Position(1, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(2, 0), Board.Token.WHITE);
			game.placeToken(new Position(2, 1), Board.Token.BLACK);
			game.placeToken(new Position(3, 0), Board.Token.WHITE);
			if (Game.Status.WHITEWON != game.getStatus()) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_27() {
		String output = "|W|W|W|_|\n" + "|B|B|B|B|\n" + "|_|_|_|_|\n" + "|_|_|_|W|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(0, 1), Board.Token.BLACK);
			game.placeToken(new Position(1, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(2, 0), Board.Token.WHITE);
			game.placeToken(new Position(2, 1), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);
			game.placeToken(new Position(3, 1), Board.Token.BLACK);
			if (Game.Status.BLACKWON != game.getStatus()) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_28() {
		String output = "|W|B|_|_|\n" + "|W|B|_|_|\n" + "|W|B|_|_|\n" + "|W|_|_|_|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 0), Board.Token.BLACK);
			game.placeToken(new Position(0, 1), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(0, 2), Board.Token.WHITE);
			game.placeToken(new Position(1, 2), Board.Token.BLACK);
			game.placeToken(new Position(0, 3), Board.Token.WHITE);
			if (Game.Status.WHITEWON != game.getStatus()) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_29() {
		String output = "|W|B|_|_|\n" + "|W|B|_|_|\n" + "|W|B|_|_|\n" + "|_|B|_|W|\n";
		try {
			Game game = new Game(new Board());
			game.placeToken(new Position(0, 0), Board.Token.WHITE);
			game.placeToken(new Position(1, 0), Board.Token.BLACK);
			game.placeToken(new Position(0, 1), Board.Token.WHITE);
			game.placeToken(new Position(1, 1), Board.Token.BLACK);
			game.placeToken(new Position(0, 2), Board.Token.WHITE);
			game.placeToken(new Position(1, 2), Board.Token.BLACK);
			game.placeToken(new Position(3, 3), Board.Token.WHITE);
			game.placeToken(new Position(1, 3), Board.Token.BLACK);
			if (Game.Status.BLACKWON != game.getStatus()) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_30() {
		String output = "|W|B|B|B|\n" +
                        "|W|_|_|W|\n" +
                        "|W|_|_|W|\n" +
                        "|B|B|B|W|\n";
		Game game = new Game(new Board());
		game.placeToken(new Position(0, 0), Board.Token.WHITE);
		game.placeToken(new Position(1, 0), Board.Token.BLACK);
		game.placeToken(new Position(0, 1), Board.Token.WHITE);
		game.placeToken(new Position(2, 0), Board.Token.BLACK);
		game.placeToken(new Position(0, 2), Board.Token.WHITE);
		game.placeToken(new Position(3, 0), Board.Token.BLACK);
		game.placeToken(new Position(3, 3), Board.Token.WHITE);
		game.placeToken(new Position(0, 3), Board.Token.BLACK);
		game.placeToken(new Position(3, 2), Board.Token.WHITE);
		game.placeToken(new Position(1, 3), Board.Token.BLACK);
		game.placeToken(new Position(3, 1), Board.Token.WHITE);
		game.placeToken(new Position(2, 3), Board.Token.BLACK);
		game.placeToken(new Position(1, 1), Board.Token.WHITE);
		game.placeToken(new Position(1, 1), Board.Token.BLACK);
		game.placeToken(new Position(1, 1), Board.Token.WHITE);
		game.placeToken(new Position(1, 1), Board.Token.BLACK);
		if (Game.Status.STALEMATE != game.getStatus()) {
			fail();
		}
	}

	@Test
	public void test_31() {
		try {
			Position pos = new Position(1, 2);
			if (!pos.toString().equals("(1,2)")) {
				fail();
			}
		} catch (Exception e) {
			fail();
		}
	}
}
