package main.gamemodel.cells;

import main.gamemodel.Textable;

public abstract class BoardCell {
	public static final int TEXTUAL_REP_WIDTH = 3;
	public static final int TEXTUAL_REP_HEIGHT = 3;

	/**
	 * A new textual representation object.
	 */
	public static char[][] blankTextualRep() {
		return Textable.blankTextualRep(
				TEXTUAL_REP_HEIGHT, TEXTUAL_REP_WIDTH
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
}
