package main.gamemodel;

import main.gamemodel.cells.Cell;

import java.util.List;

import static main.gamemodel.cells.Cell.TEXTUAL_REP_HEIGHT;
import static main.gamemodel.cells.Cell.TEXTUAL_REP_WIDTH;

/**
 * Something that can be represented by text
 */
public interface Textable {

	char BLANK_TEXT_REP_CHAR = '.';
	char BLANK_CELL_TEXT_REP_CHAR = ' ';

	/**
	 * A new textual representation object. Add your representation here
	 */
	static char[][] blankTextualRep(int height, int width) {
		return blankTextualRep(height, width, BLANK_TEXT_REP_CHAR);
	}

	static char[][] blankTextualRep(int height, int width, char fillChar) {
		char[][] representation = new char[height][width];
		for (int r = 0; r < representation.length; r++) {
			char[] row = representation[r];
			for (int c = 0; c < row.length; c++) {
				row[c] = fillChar;
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
					builder.append("  ");
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

	static char[][] copyRowIntoRep(List<? extends Cell> cells) {
		char[][] rep = Textable.blankTextualRep(
				TEXTUAL_REP_HEIGHT,
				TEXTUAL_REP_WIDTH * cells.size()
		);
		for (int i = 0; i < cells.size(); i++) {
			char[][] chars = cells.get(i).toTextualRep();
			Textable.copyRepIntoRep(chars, rep, 0, i * TEXTUAL_REP_WIDTH);
		}

		return rep;
	}

	/**
	 * @return The textual representation of this object
	 */
	char[][] toTextualRep();
}
