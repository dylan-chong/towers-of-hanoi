package main;

import java.util.concurrent.atomic.AtomicInteger;

public class Board implements TextualRepresentable {

	private static final int DEFAULT_NUM_COLS = 10;
	private static final int DEFAULT_NUM_ROWS = 10;

	private final BoardCell[][] cells;

	public Board(int numRows, int numCols) {
		this.cells = new BoardCell[numRows][numCols];
	}

	public Board() {
		this(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS);
	}

	public void addCell(BoardCell boardCell, int row, int col) {
		cells[row][col] = boardCell;
	}

	/**
	 * To be used for testing only. Underscore is used to show privacy
	 */
	public BoardCell _getCellAt(int row, int col) {
		return cells[row][col];
	}

	public boolean isEmpty() {
		return numCells() == 0;
	}

	public int numCells() {
		AtomicInteger count = new AtomicInteger(0);
		forEachCell((cell, row, col) -> {
			if (cell != null) {
				count.getAndIncrement();
			}
		});
		return count.get();
	}

	/**
	 * Convert this board, and the cells inside the board to a textual
	 * representation (grid of characters), where every
	 * cell takes up {@link BoardCell#TEXTUAL_REP_WIDTH} chars in width
	 * and {@link BoardCell#TEXTUAL_REP_HEIGHT} cells in height.
	 */
	@Override
	public char[][] toTextualRep() {
		int height = cells.length * BoardCell.TEXTUAL_REP_HEIGHT;
		int width = cells[0].length * BoardCell.TEXTUAL_REP_WIDTH;

		char[][] representation =
				TextualRepresentable.blankTextualRep(height, width);

		forEachCell((cell, row, col) -> {
			if (cell == null) {
				return;
			}

			char[][] cellRep = cell.toTextualRep();
			TextualRepresentable.copyRepIntoRep(
					cellRep,
					representation,
					row * BoardCell.TEXTUAL_REP_HEIGHT,
					col * BoardCell.TEXTUAL_REP_WIDTH
			);

		});

		return representation;
	}

	/**
	 * This is here to reduce nested for loop
	 * @param consumer A function that does stuff to each cell
	 */
	private void forEachCell(CellConsumer consumer) {
		for (int r = 0; r < cells.length; r++) {
			BoardCell[] row = cells[r];
			for (int c = 0; c < row.length; c++) {
				BoardCell cell = cells[r][c];
				consumer.apply(cell, r, c);
			}
		}
	}

	interface CellConsumer {
		void apply(BoardCell cell, int row, int col);
	}
}
