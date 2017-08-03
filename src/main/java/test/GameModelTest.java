package test;

import main.gamemodel.*;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;
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
		PlayerCell player1Cell = (PlayerCell) board.getCellAt(1, 1);
		PlayerCell player2Cell = (PlayerCell) board.getCellAt(4, 4);
		assertNotEquals(player1Cell, player2Cell);
		assertNotNull(player1Cell);
		assertNotNull(player2Cell);
	}

	@Test
	public void create_player1sTurn_newCellInPlayer1CreationSquare()
			throws InvalidMoveException {
		Board board = new Board(6, 6);
		GameModel gameModel = new GameModel(board);

		char cellId = 'B';
		gameModel.create(cellId, AbsDirection.NORTH);

		PieceCell newCell = (PieceCell) board.getCellAt(2, 2);
		assertEquals(cellId, newCell.getId());
	}
}
