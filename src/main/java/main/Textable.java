package main;

/**
 * Something that can be represented by text
 */
public interface Textable {

	char BLANK_TEXT_REP_CHAR = '.';

	/**
	 * A new textual representation object. Add your representation here
	 */
	static char[][] blankTextualRep(int height, int width) {
		char[][] representation = new char[height][width];
		for (int r = 0; r < representation.length; r++) {
			char[] row = representation[r];
			for (int c = 0; c < row.length; c++) {
				row[c] = BLANK_TEXT_REP_CHAR;
			}
		}
		return representation;
	}

	static String convertToString(char[][] representation, boolean withGap) {
		StringBuilder builder = new StringBuilder();
		for (char[] row : representation) {
			for (char c : row) {
				builder.append(c);
				if (withGap) {
					builder.append(' ');
				}
			}

			builder.append('\n');
		}
		return builder.toString();
	}

	static String convertToString(char[][] representation) {
		return convertToString(representation, false);
	}

	/**
	 * Copies the entire toCopy content onto the largerRep where the top left
	 * position is at startRow and startCol.
	 */
	static void copyRepIntoRep(char[][] toCopy,
							   char[][] largerRep,
							   int startRow,
							   int startCol) {
		for (int r = 0; r < toCopy.length; r++) {
			for (int c = 0; c < toCopy[0].length; c++) {
				int largerRow = startRow + r;
				int largerCol = startCol + c;

				if (largerRep[largerRow][largerCol] != BLANK_TEXT_REP_CHAR) {
					throw new RuntimeException(String.format(
							"Position r: %d, c: %d not blank",
							r,
							c
					));
				}

				largerRep[largerRow][largerCol] = toCopy[r][c];
			}
		}
	}

	/**
	 * @return The textual representation of this object
	 */
	char[][] toTextualRep();
}
