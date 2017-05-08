package swen221.lab6.connect.util;

/**
 * Represents an (x,y) position on the game board.
 *
 * @author David J. Pearce
 *
 */
public final class Position {
	public static final String INVALID_X_COMPONENT = "Invalid X component: ";
	public static final String INVALID_Y_COMPONENT = "Invalid Y component: ";

	private final int x;
	private final int y;

	/**
	 * Construct a position on the board
	 *
	 * @param x
	 * @param y
	 */
	public Position(int x, int y) {
		if(x < 0 || x >= 4) {
			throw new IllegalArgumentException(INVALID_X_COMPONENT + x);
		}
		if(y < 0 || y >= 4) {
			throw new IllegalArgumentException(INVALID_Y_COMPONENT + y);
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the X component associated with this position.
	 *
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the Y component associated with this position.
	 *
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Provide a human readable string representing this position.
	 */
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
