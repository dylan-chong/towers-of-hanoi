package main;

/**
 * The player cell
 */
public class Player extends BoardCell {

	private final Token token;

	public Player(Token token, int row, int col) {
		super(row, col);
		this.token = token;
	}

	/**
	 * @return a 3x3 grid of characters representing this piece -
	 * a textual representation
	 */
	public char[][] toTextualRep() {
		char[][] representation = blankTextualRep();
		representation[1][1] = token.representation;
		return representation;
	}

	public enum Token {
		HAPPY('0'),
		ANGRY('1');

		Token(char representation) {
			this.representation = representation;
		}

		public final char representation;
	}
}
