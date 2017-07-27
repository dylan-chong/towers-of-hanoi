package
		main;

public class Board {

	public static final char BLANK_TEXT_REP_CHAR = '.';

	/**
	 * A new textual representation object. Add your representation here
	 */
	public static char[][] blankTextualRep(int height, int width) {
		char[][] representation = new char[height][width];
		for (int r = 0; r < representation.length; r++) {
			char[] row = representation[r];
			for (int c = 0; c < row.length; c++) {
				row[c] = BLANK_TEXT_REP_CHAR;
			}
		}
		return representation;
	}

	public static String representationToString(char[][] representation) {
		StringBuilder builder = new StringBuilder();
		for (char[] row : representation) {
			for (char c : row) {
				builder.append(c);
			}

			builder.append('\n');
		}
		return builder.toString();
	}

	/**
	 * Copies the entire toCopy content onto the largerRep where the top left
	 * position is at startRow and startCol.
	 */
	public void copyRepIntoRep(char[][] toCopy,
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

	private final BoardCell[][] cells;

	public Board(int numRows, int numCols) {
		this.cells = new BoardCell[numRows][numCols];
	}

	public void addCell(BoardCell boardCell, int row, int col) {
		cells[row][col] = boardCell;
	}

	/**
	 * Convert this board, and the cells inside the board to a textual
	 * representation (grid of characters), where every
	 * cell takes up {@link BoardCell#TEXTUAL_REP_WIDTH} chars in width
	 * and {@link BoardCell#TEXTUAL_REP_HEIGHT} cells in height.
	 */
	public char[][] toTextualRep() {
		int height = cells.length * BoardCell.TEXTUAL_REP_HEIGHT;
		int width = cells[0].length * BoardCell.TEXTUAL_REP_WIDTH;

		char[][] representation = blankTextualRep(height, width);

		for (int r = 0; r < cells.length; r++) {
			BoardCell[] row = cells[r];
			for (int c = 0; c < row.length; c++) {
				BoardCell cell = row[c];
				if (cell == null) {
					continue;
				}

				char[][] cellRep = cell.toTextualRep();
				copyRepIntoRep(
						cellRep,
						representation,
						r * BoardCell.TEXTUAL_REP_HEIGHT,
						c * BoardCell.TEXTUAL_REP_WIDTH
				);
			}
		}

		return representation;
	}
}
