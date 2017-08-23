package main.gamemodel.cells;

import main.gamemodel.Direction;
import main.gamemodel.Textable;

public abstract class BoardCell implements Textable, Comparable<BoardCell> {
	public static final int TEXTUAL_REP_WIDTH = 3;
	public static final int TEXTUAL_REP_HEIGHT = 3;

	/**
	 * A new textual representation object.
	 */
	public static char[][] blankCellTextualRep() {
		return Textable.blankTextualRep(
				TEXTUAL_REP_HEIGHT, TEXTUAL_REP_WIDTH,
				' '
		);
	}

	/**
	 * @return a 3x3 grid of characters representing this piece -
	 * a textual representation. This representation is independent
	 * of it's position.
	 *
	 * The array is in char[row][col] format
	 */
	public abstract char[][] toTextualRep();

	public abstract char getId();

	/**
	 * @param fromThisToCell The direction from this cell to the given cell
	 */
	public Reaction getReactionTo(BoardCell cell, Direction fromThisToCell) {
		return cell.getReactionToByVisiting(this, fromThisToCell);
	}

	protected abstract Reaction getReactionToPieceCell(PieceCell cell,
													   Direction fromThisToCell);
	protected abstract Reaction getReactionToPlayerCell(PlayerCell cell,
														Direction fromThisToCell);

	/**
	 * Call the corresponding method for this cell on the provided cell
	 * @param fromCellToThis Pass this into the method you call on the given cell
	 */
	protected abstract Reaction getReactionToByVisiting(BoardCell cell,
														Direction fromCellToThis);

	public abstract <ReturnT> ReturnT getValue(BoardCellMapper<ReturnT> getter);

	@Override
	public int compareTo(BoardCell o) {
		int result = getId() - o.getId();
		if (result == 0 && o != this) {
			throw new IllegalStateException();
		}
		return result;
	}
}
