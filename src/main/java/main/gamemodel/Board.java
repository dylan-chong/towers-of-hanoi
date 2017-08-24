package main.gamemodel;

import main.gamemodel.cells.Cell;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Board implements Textable {

	public static final int DEFAULT_NUM_COLS = 10;
	public static final int DEFAULT_NUM_ROWS = 10;

	private final Cell[][] cells;

	public Board(int numRows, int numCols) {
		this.cells = new Cell[numRows][numCols];
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

	public void addCell(Cell cell, int row, int col)
			throws InvalidMoveException {
		if (getCellAt(row, col) != null) {
			throw new InvalidMoveException("There is already cell there");
		}

		cells[row][col] = cell;
	}

	public Cell removeCell(int row, int col) throws InvalidMoveException {
		Cell cell = getCellAt(row , col);
		if (cell == null) {
			throw new InvalidMoveException("There is no cell there");
		}

		cells[row][col] = null;
		return cell;
	}

	/**
	 * To be used for testing only. Underscore is used to show privacy
	 */
	public Cell getCellAt(int row, int col) {
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

	public int[] rowColOf(Cell cell) {
		for (int r = 0; r < cells.length; r++) {
			Cell[] row = cells[r];
			for (int c = 0; c < row.length; c++) {
				Cell currentCell = cells[r][c];
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
	 * cell takes up {@link Cell#TEXTUAL_REP_WIDTH} chars in width
	 * and {@link Cell#TEXTUAL_REP_HEIGHT} cells in height.
	 */
	@Override
	public char[][] toTextualRep() {
		int height = cells.length * Cell.TEXTUAL_REP_HEIGHT;
		int width = cells[0].length * Cell.TEXTUAL_REP_WIDTH;

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
					row * Cell.TEXTUAL_REP_HEIGHT,
					col * Cell.TEXTUAL_REP_WIDTH
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
				Cell cell = cells[r][c];
				if (cell == null) {
					continue;
				}

				if (r + 1 < cells.length) {
					Cell cellBelow = cells[r + 1][c];
					if (cellBelow != null) {
						pairs.add(new CellPair(cell, cellBelow));
					}
				}
				if (c + 1 < cells[r].length) {
					Cell cellToRight = cells[r][c + 1];
					if (cellToRight != null) {
						pairs.add(new CellPair(cell, cellToRight));
					}
				}
			}
		}

		return pairs;
	}

	public boolean isInside(int row, int col) {
		try {
			// noinspection unused
			Cell cell = cells[row][col];
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * This is here to reduce nested for loop
	 * @param consumer A function that does stuff to each cell
	 */
	public void forEachCell(CellConsumer consumer) {
		for (int r = 0; r < cells.length; r++) {
			Cell[] row = cells[r];
			for (int c = 0; c < row.length; c++) {
				Cell cell = cells[r][c];
				consumer.apply(cell, r, c);
			}
		}
	}

	/**
	 * Reaction between 2 {@link Cell} objects (they are next to each
	 * other).
	 *
	 * cellA should be above or to the left of cellB
	 */
	public static class CellPair {
		public final Cell cellA;
		public final Cell cellB;

		public CellPair(Cell cellA, Cell cellB) {
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

			List<Cell> otherCells = Arrays.asList(cellPair.cellA, cellPair.cellB);
			List<Cell> theseCells = Arrays.asList(cellA, cellB);
			return new HashSet<>(theseCells).equals(new HashSet<>(otherCells));
		}

		@Override
		public int hashCode() {
			return cellA.hashCode() + cellB.hashCode();
		}

		@Override
		public String toString() {
			return String.format(
					"CellPair{%c,%c}",
					cellA.getId(),
					cellB.getId()
			);
		}
	}
}
