package test;

import main.Board;
import main.GameModel;
import main.PlayerCell;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameModelTest {

	@Test
	public void new_withEmptyBoard_addsPlayers() {
		Board board = new Board(6, 6);
		assertTrue(board.isEmpty()); // sanity check

		GameModel gameModel = new GameModel(board);

		// Assume we only have 2 players
		assertEquals(2, board.numCells());
		// Assert these cells are PlayerCells
		PlayerCell player1 = (PlayerCell) board.getCellAt(1, 1);
		PlayerCell player2 = (PlayerCell) board.getCellAt(4, 4);
		assertNotEquals(player1, player2);
		assertNotNull(player1);
		assertNotNull(player2);
		// TODO add creation cells
	}

}
