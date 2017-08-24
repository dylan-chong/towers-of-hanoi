package test;

import main.gamemodel.Board;
import main.gamemodel.InvalidMoveException;
import main.gamemodel.Textable;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PieceCell.SideCombination;
import org.junit.Test;

import java.util.*;

import static main.gamemodel.Board.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test.TestUtils.assertRepresentationEquals;

public class BoardTest {

	@Test
	public void new_initialState_shouldBeEmpty() {
		assertTrue(new Board().isEmpty());
	}

	@Test
	public void toTextualRep_emptyBoard_returnsEmptyRepresentation() {
		int rows = 5;
		int cols = 4;
		Board board = new Board(rows, cols);

		char[][] representation = board.toTextualRep();
		char[][] expected = Textable.blankTextualRep(
				rows * Cell.TEXTUAL_REP_HEIGHT,
				cols * Cell.TEXTUAL_REP_WIDTH
		);
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void toTextualRep_boardWith1Cell_returnsBoardWithCellRep()
			throws InvalidMoveException {
		Board board = new Board(2, 2);
		board.addCell(new PieceCell(
				'a',
				SideCombination.SWORD_SWORD_SWORD_SWORD
		), 0, 1);

		char[][] representation = board.toTextualRep();
		char[][] expected = new char[][] {
				"... | ".toCharArray(),
				"...-a-".toCharArray(),
				"... | ".toCharArray(),
				"......".toCharArray(),
				"......".toCharArray(),
				"......".toCharArray(),
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void toTextualRep_boardWith2Cells_returnsBoardWithCellRep()
			throws InvalidMoveException {
		Board board = new Board(2, 2);
		board.addCell(new PieceCell(
				'a',
				SideCombination.SWORD_SWORD_SWORD_SWORD
		), 0, 1);
		board.addCell(new PieceCell(
				'b',
				SideCombination.SHIELD_SHIELD_SHIELD_SHIELD
		), 1, 0);

		char[][] representation = board.toTextualRep();
		char[][] expected = new char[][] {
				"... | ".toCharArray(),
				"...-a-".toCharArray(),
				"... | ".toCharArray(),
				" # ...".toCharArray(),
				"#b#...".toCharArray(),
				" # ...".toCharArray(),
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void
	findTouchingCellPairs_someCellsAreTouchingAndSomeAreNot_findsTouchingCells()
			throws InvalidMoveException {
		Board board = new Board(6, 6);
		SideCombination anySide = SideCombination.values()[0];
		List<PieceCell> pieces = Arrays.asList(
				new PieceCell('a', anySide),
				new PieceCell('b', anySide),
				new PieceCell('c', anySide),
				new PieceCell('d', anySide),
				new PieceCell('e', anySide),
				new PieceCell('f', anySide),
				new PieceCell('g', anySide)
		);
		Set<CellPair> pairs = new HashSet<>();

		// Horizontal pair
		board.addCell(pieces.get(0), 0, 0);
		board.addCell(pieces.get(1), 1, 0);
		pairs.add(new CellPair(pieces.get(0), pieces.get(1)));

		// Vertical pair
		board.addCell(pieces.get(2), 2, 3);
		board.addCell(pieces.get(3), 2, 4);
		pairs.add(new CellPair(pieces.get(2), pieces.get(3)));

		// Diagonal (not touching)
		board.addCell(pieces.get(4), 4, 3);
		board.addCell(pieces.get(5), 5, 4);

		// Lonely cell
		board.addCell(pieces.get(6), 4, 1);

		assertEquals(pairs, board.findTouchingCellPairs());
	}
}
