package main.gamemodel;

import main.gamemodel.cells.BoardCell;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Board implements Textable {

	private static final int DEFAULT_NUM_COLS = 10;
	private static final int DEFAULT_NUM_ROWS = 10;

	private final BoardCell[][] cells;

	public Board(int numRows, int numCols) {
		this.cells = new BoardCell[numRows][numCols];
	}

	public Board() {
		this(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS);
	}

	public int getNumRows() {
		return cells.length;
	}

	public int getNumCols() {
		return cells[0].length;
	}

	public void addCell(BoardCell boardCell, int row, int col)
			throws InvalidMoveException {
		if (getCellAt(row, col) != null) {
			throw new InvalidMoveException("There is already cell there");
		}

		cells[row][col] = boardCell;
	}

	public BoardCell removeCell(int row, int col) throws InvalidMoveException {
		BoardCell cell = getCellAt(row , col);
		if (cell == null) {
			throw new InvalidMoveException("There is no cell there");
		}

		cells[row][col] = null;
		return cell;
	}

	/**
	 * To be used for testing only. Underscore is used to show privacy
	 */
	public BoardCell getCellAt(int row, int col) {
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

	public int[] rowColOf(BoardCell cell) {
		for (int r = 0; r < cells.length; r++) {
			BoardCell[] row = cells[r];
			for (int c = 0; c < row.length; c++) {
				BoardCell currentCell = cells[r][c];
				if (cell == currentCell) {
					return new int[]{r, c};
				}
			}
		}

		return null;
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
				Textable.blankTextualRep(height, width);

		forEachCell((cell, row, col) -> {
			if (cell == null) {
				return;
			}

			char[][] cellRep = cell.toTextualRep();
			Textable.copyRepIntoRep(
					cellRep,
					representation,
					row * BoardCell.TEXTUAL_REP_HEIGHT,
					col * BoardCell.TEXTUAL_REP_WIDTH
			);

		});

		return representation;
	}

	/**
	 * Two cells are touching if one is to the left of the other, or one is
	 * below the other. Diagonals do not count.
	 */
	public Set<CellPair> findTouchingCellPairs() {
		Set<CellPair> pairs = new HashSet<>();

		for (int r = 0; r < cells.length; r++) {
			for (int c = 0; c < cells[r].length; c++) {
				BoardCell cell = cells[r][c];
				if (cell == null) {
					continue;
				}

				if (r + 1 < cells.length) {
					BoardCell cellBelow = cells[r + 1][c];
					if (cellBelow != null) {
						pairs.add(new CellPair(cell, cellBelow));
					}
				}
				if (c + 1 < cells[r].length) {
					BoardCell cellToRight = cells[r][c + 1];
					if (cellToRight != null) {
						pairs.add(new CellPair(cell, cellToRight));
					}
				}
			}
		}

		return pairs;
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

	/**
	 * Reaction between 2 {@link BoardCell} objects (they are next to each
	 * other). cellA and cellB are in no particular order.
	 */
	public static class CellPair {
		private final BoardCell cellA;
		private final BoardCell cellB;

		public CellPair(BoardCell cellA, BoardCell cellB) {
			if (cellA.equals(cellB)) {
				throw new IllegalArgumentException("Cells can't be the same");
			}

			this.cellA = cellA;
			this.cellB = cellB;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CellPair cellPair = (CellPair) o;

			return new HashSet<>(Arrays.asList(cellPair.cellA, cellPair.cellB))
					.equals(new HashSet<>(Arrays.asList(cellA, cellB)));
		}

		@Override
		public int hashCode() {
			return cellA.hashCode() + cellB.hashCode();
		}
	}
}
