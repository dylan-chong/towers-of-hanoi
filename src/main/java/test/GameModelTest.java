package test;

import main.gamemodel.*;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static test.TestUtils.assertRepresentationEquals;
import static test.TestUtils.assertRepresentationNotEquals;

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
		gameModel.create(cellId, 0);

		PieceCell newCell = (PieceCell) board.getCellAt(2, 2);
		assertEquals(cellId, newCell.getId());
	}

	@Test
	public void undo_afterCreate_textualRepresentationChangesBack()
			throws Exception {
		new UndoTest() {
			@Override
			protected void performUndoableOperation(GameModel gameModel)
					throws Exception {
				gameModel.create('a', 0);
			}
		}.run();
	}

	@Test
	public void undo_afterMove_textualRepresentationChangesBack()
			throws Exception {
		new UndoTest() {
			@Override
			protected void setup(GameModel gameModel) throws Exception {
				gameModel.create('a', 0);
			}

			@Override
			protected void performUndoableOperation(GameModel gameModel)
					throws Exception {
				gameModel.move('a', Direction.NORTH);
			}
		}.run();
	}

	@Test
	public void undo_afterRotate_textualRepresentationChangesBack()
			throws Exception {
		new UndoTest() {
			@Override
			protected void setup(GameModel gameModel) throws Exception {
				gameModel.create('a', 0);
			}

			@Override
			protected void performUndoableOperation(GameModel gameModel)
					throws Exception {
				gameModel.rotate('a', 1);
			}
		}.run();
	}

	@Test
	public void undo_afterPassFromCreationToMoveState_textualRepresentationChangesBack()
			throws Exception {
		AtomicReference<TurnState> originalState = new AtomicReference<>();
		new UndoTest() {
			@Override
			protected void setup(GameModel gameModel) throws Exception {
				super.setup(gameModel);
				originalState.set(gameModel.getTurnState());
			}

			@Override
			protected boolean skipSanityDefaultCheck() {
				return true;
			}

			@Override
			protected void performUndoableOperation(GameModel gameModel)
					throws Exception {
				gameModel.passTurnState();
			}

			@Override
			protected void checkStateEqualsOriginal(
					GameModel gameModel,
					char[][] repBeforeCreate
			) throws Exception {
				assertEquals(originalState.get(), gameModel.getTurnState());
			}
		}.run();
	}

	@Test
	public void undo_afterPassFromMoveToCreateState_textualRepresentationChangesBack()
			throws Exception {
		AtomicReference<TurnState> originalState = new AtomicReference<>();
		new UndoTest() {
			@Override
			protected void setup(GameModel gameModel) throws Exception {
				super.setup(gameModel);
				gameModel.create('a', 0);
				originalState.set(gameModel.getTurnState());
			}

			@Override
			protected boolean skipSanityDefaultCheck() {
				return true;
			}

			@Override
			protected void performUndoableOperation(GameModel gameModel)
					throws Exception {
				gameModel.passTurnState();
			}

			@Override
			protected void checkStateEqualsOriginal(
					GameModel gameModel,
					char[][] repBeforeCreate
			) throws Exception {
				assertEquals(originalState.get(), gameModel.getTurnState());
			}
		}.run();
	}
	private abstract class UndoTest {
		public void run() throws Exception {
			Board board = new Board(6, 6);
			GameModel gameModel = new GameModel(board);
			setup(gameModel);

			char[][] repBeforeCreate = gameModel.toTextualRep();

			// Repeat do/undo stuff
			for (int i = 0; i < 10; i++) {
				performUndoableOperation(gameModel);

				char[][] repAfterCreate = gameModel.toTextualRep();
				if (!skipSanityDefaultCheck()) {
					// Sanity check: If this fails, the test is not very useful!
                	// (Unless you overrode the state checking)
					assertRepresentationNotEquals(repBeforeCreate, repAfterCreate);
				}

				gameModel.undo();
				checkStateEqualsOriginal(gameModel, repBeforeCreate);
			}
		}

		protected abstract void performUndoableOperation(GameModel gameModel)
				throws Exception;

		// Hooks

		protected void setup(GameModel gameModel) throws Exception {
			// Stub
		}

		protected boolean skipSanityDefaultCheck() {
			return false;
		}

		protected void checkStateEqualsOriginal(
				GameModel gameModel,
				char[][] repBeforeCreate
		) throws Exception {
			char[][] repAfterUndo = gameModel.toTextualRep();
			assertRepresentationEquals(repBeforeCreate, repAfterUndo);
		}
	}

}
