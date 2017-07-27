package test;

import main.Board;
import main.BoardCell;
import main.Piece;
import org.junit.Test;

import static test.TestUtils.assertRepresentationEquals;

public class BoardTest {

	@Test
	public void toTextualRep_emptyBoard_returnsEmptyRepresentation() {
		int rows = 5;
		int cols = 4;
		Board board = new Board(rows, cols);

		char[][] representation = board.toTextualRep();
		char[][] expected = Board.blankTextualRep(
				rows * BoardCell.TEXTUAL_REP_HEIGHT,
				cols * BoardCell.TEXTUAL_REP_WIDTH
		);
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void toTextualRep_boardWith1Cell_returnsBoardWithCellRep() {
		Board board = new Board(2, 2);
		board.addCell(new Piece(
				'a',
				Piece.SideCombination.SWORD_SWORD_SWORD_SWORD,
				0,
				1
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
}
