package swen221.lab6.tests;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import swen221.lab6.connect.Game;
import swen221.lab6.connect.core.Board;
import swen221.lab6.connect.util.Position;

import static org.junit.Assert.*;

/**
 * Use name format by Roy Osherove
 * public void unitOfWorkUnderTest_inputOrState_expectation() {}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectTests {

	@Test
	public void boardToString_emptyBoard_returnsEmptyBoardFormat() {
		String output = "|_|_|_|_|\n" +
						"|_|_|_|_|\n" +
						"|_|_|_|_|\n" +
						"|_|_|_|_|\n";

		Board board = new Board();

		assertEquals(output,board.toString());
	}

	// ************************* Position *************************

	@Test
	public void newPosition_atOrigin_noExceptionThrown() {
		new Position(0, 0);
	}

	@Test
	public void newPosition_bottomRight_noExceptionThrown() {
		new Position(3, 3);
	}

	@Test
	public void newPosition_farLeft_invalidXExceptionThrown() {
		testNewPositionAndExpectException(-1, 2, Position.INVALID_X_COMPONENT);
	}

	@Test
	public void newPosition_farRight_invalidXExceptionThrown() {
		testNewPositionAndExpectException(4, 2, Position.INVALID_X_COMPONENT);
	}

	@Test
	public void newPosition_farAbove_invalidYExceptionThrown() {
		testNewPositionAndExpectException(2, -1, Position.INVALID_Y_COMPONENT);
	}

	@Test
	public void newPosition_farBelow_invalidYExceptionThrown() {
		testNewPositionAndExpectException(2, 4, Position.INVALID_Y_COMPONENT);
	}

	private void testNewPositionAndExpectException(int x,
												   int y,
												   String partOfExceptionMessage) {
		try {
			new Position(x, y);
			fail();
		} catch (IllegalArgumentException e) {
			if (!e.getMessage().contains(partOfExceptionMessage))
				fail("Incorrect exception thrown: " + e);
		}
	}

	// ************************* Game *************************

	@Test
	public void gameCheckTurn_afterNoMoves_isWhitesTurn() {
		Game game = new Game(new Board());
		assertTrue(game.isWhitesTurn());
		assertFalse(game.isBlacksTurn());
	}

	@Test
	public void gameCheckTurn_after1Move_turnAlternates() {
		Game game = new Game(new Board());
		game.placeToken(new Position(0, 0), Board.Token.WHITE);
		assertFalse(game.isWhitesTurn());
		assertTrue(game.isBlacksTurn());
	}

	// ************************* Game Rules *************************

	@Test
	public void placeToken_oneToken_noChangeInBoard() {
		Board board = new Board();
		Board expectedBoard = board.clone();
		expectedBoard.setSquare(new Position(0, 0), Board.Token.WHITE);
		testPlaceToken(
				board,
				new Position(0, 0), Board.Token.WHITE,
				expectedBoard
		);
	}

	@Test
	public void placeToken_threeTokensHorizontal_middleIsCaptured() {
		Board board = new Board();
		board.setSquare(new Position(0, 0), Board.Token.WHITE);
		board.setSquare(new Position(1, 0), Board.Token.BLACK);
		Board expectedBoard = new Board();
		expectedBoard.setSquare(new Position(0, 0), Board.Token.WHITE);
		expectedBoard.setSquare(new Position(2, 0), Board.Token.WHITE);

		testPlaceToken(
				board,
				new Position(2, 0), Board.Token.WHITE,
				expectedBoard
		);
	}

	@Test
	public void placeToken_threeTokensVertical_middleIsCaptured() {
		Board board = new Board();
		board.setSquare(new Position(0, 0), Board.Token.WHITE);
		board.setSquare(new Position(0, 1), Board.Token.BLACK);
		Board expectedBoard = new Board();
		expectedBoard.setSquare(new Position(0, 0), Board.Token.WHITE);
		expectedBoard.setSquare(new Position(0, 2), Board.Token.WHITE);

		testPlaceToken(
				board,
				new Position(0, 2), Board.Token.WHITE,
				expectedBoard
		);
	}

	@Test
	public void placeToken_fourTokensHorizontal_middleTwoAreCaptured() {
		Board board = new Board();
		board.setSquare(new Position(0, 0), Board.Token.WHITE);
		board.setSquare(new Position(1, 0), Board.Token.BLACK);
		board.setSquare(new Position(3, 0), Board.Token.BLACK);
		Board expectedBoard = new Board();
		expectedBoard.setSquare(new Position(0, 0), Board.Token.WHITE);
		expectedBoard.setSquare(new Position(3, 0), Board.Token.BLACK);

		testPlaceToken(
				board,
				new Position(2, 0), Board.Token.WHITE,
				expectedBoard
		);
	}

	@Test
	public void placeToken_fourTokensVertical_middleTwoAreCaptured() {
		Board board = new Board();
		board.setSquare(new Position(0, 0), Board.Token.WHITE);
		board.setSquare(new Position(0, 1), Board.Token.BLACK);
		board.setSquare(new Position(0, 3), Board.Token.BLACK);
		Board expectedBoard = new Board();
		expectedBoard.setSquare(new Position(0, 0), Board.Token.WHITE);
		expectedBoard.setSquare(new Position(0, 3), Board.Token.BLACK);

		testPlaceToken(
				board,
				new Position(0, 2), Board.Token.WHITE,
				expectedBoard
		);
	}

	@Test
	public void placeToken_stalemateScenario_gameStatusIsStalemate() {
		Board board = new Board();

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (x == 3 && y == 3) break; // don't put last square yet
				Board.Token token;

				if (x <= 1 && y <= 1 || x >= 2 && y >= 2)
					token = Board.Token.WHITE;
				else
					token = Board.Token.BLACK;

				board.setSquare(new Position(x, y), token);
			}
		}

		Game game = new Game(board);
		// Ensure the game is generated correctly, to make sure the test below
		// is reasonable
		String boardString = "|W|W|B|B|\n" +
                             "|W|W|B|B|\n" +
                             "|B|B|W|W|\n" +
                             "|B|B|W|_|\n";
		assertEquals(boardString, game.getBoard().toString());

		assertNotEquals(Game.Status.STALEMATE, game.getStatus());
		game.placeToken(new Position(3, 3), Board.Token.WHITE);
		assertEquals(Game.Status.STALEMATE, game.getStatus());
	}

	@Test
	public void placeToken_fourTokensVertical_win() {
		Board board = new Board();
		for (int y = 0; y < 3; y++)
			board.setSquare(new Position(0, y), Board.Token.WHITE);
		Game game = new Game(board);
		assertNotEquals(Game.Status.WHITEWON, game.getStatus());
		game.placeToken(new Position(0, 3), Board.Token.WHITE);
		assertEquals(Game.Status.WHITEWON, game.getStatus());
	}

	@Test
	public void placeToken_fourTokensHorizontal_win() {
		Board board = new Board();
		for (int x = 0; x < 3; x++)
			board.setSquare(new Position(x, 0), Board.Token.WHITE);
		Game game = new Game(board);
		assertNotEquals(Game.Status.WHITEWON, game.getStatus());
		game.placeToken(new Position(3, 0), Board.Token.WHITE);
		assertEquals(Game.Status.WHITEWON, game.getStatus());
	}

	private void testPlaceToken(Board boardToApplyOn,
								Position lastPositionToPlace,
								Board.Token lastTokenToPlace,
								Board expectedBoard) {
		new Game(boardToApplyOn).placeToken(lastPositionToPlace, lastTokenToPlace);
		assertEquals(boardToApplyOn.toString(), expectedBoard.toString());
	}

}
