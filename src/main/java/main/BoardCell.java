package main;

public abstract class BoardCell {
	public static final int TEXTUAL_REP_WIDTH = 3;
	public static final int TEXTUAL_REP_HEIGHT = 3;

	/**
	 * A new textual representation object. Add your representation here
	 */
	public static char[][] blankTextualRep() {
		return Board.blankTextualRep(TEXTUAL_REP_HEIGHT, TEXTUAL_REP_WIDTH);
	}

	/**
	 * Don't modify or access outside the class/subclasses. Underscore
	 * is used to make privacy obvious
	 */
	protected int _row;
	protected int _col;

	public BoardCell(int row, int col) {
		this._row = row;
		this._col = col;
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
