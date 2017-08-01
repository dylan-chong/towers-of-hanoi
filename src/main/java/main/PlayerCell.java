package main;

/**
 * The player cell
 */
public class PlayerCell extends BoardCell {

	/**
	 * Char to go around {@link Token#representation} icon
	 */
	private static final char FILLER_CHAR = '+';

	private final Token token;

	public PlayerCell(Token token) {
		this.token = token;
	}

	/**
	 * @return a 3x3 grid of characters representing this piece -
	 * a textual representation
	 */
	@Override
	public char[][] toTextualRep() {
		char[][] representation = blankTextualRep();
		for (int r = 0; r < representation.length; r++) {
			for (int c = 0; c < representation[r].length; c++) {
				representation[r][c] = FILLER_CHAR;
			}
		}

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
