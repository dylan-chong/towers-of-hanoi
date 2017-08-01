package test;

import main.Board;
import main.BoardCell;
import main.PieceCell;
import main.Textable;
import org.junit.Test;

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
				rows * BoardCell.TEXTUAL_REP_HEIGHT,
				cols * BoardCell.TEXTUAL_REP_WIDTH
		);
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void toTextualRep_boardWith1Cell_returnsBoardWithCellRep() {
		Board board = new Board(2, 2);
		board.addCell(new PieceCell(
				'a',
				PieceCell.SideCombination.SWORD_SWORD_SWORD_SWORD
		), 0, 1);

		char[][] representation = board.toTextualRep();
		char[][] expected = new char[][] {
				"....|.".toCharArray(),
				"...-a-".toCharArray(),
				"....|.".toCharArray(),
				"......".toCharArray(),
				"......".toCharArray(),
				"......".toCharArray(),
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void toTextualRep_boardWith2Cells_returnsBoardWithCellRep() {
		Board board = new Board(2, 2);
		board.addCell(new PieceCell(
				'a',
				PieceCell.SideCombination.SWORD_SWORD_SWORD_SWORD
		), 0, 1);
		board.addCell(new PieceCell(
				'b',
				PieceCell.SideCombination.SHIELD_SHIELD_SHIELD_SHIELD
		), 1, 0);

		char[][] representation = board.toTextualRep();
		char[][] expected = new char[][] {
				"....|.".toCharArray(),
				"...-a-".toCharArray(),
				"....|.".toCharArray(),
				".#....".toCharArray(),
				"#b#...".toCharArray(),
				".#....".toCharArray(),
		};
		assertRepresentationEquals(expected, representation);
	}
}
