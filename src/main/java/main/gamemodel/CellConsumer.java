package main.gamemodel;

import main.gamemodel.cells.Cell;

public interface CellConsumer {
	void apply(Cell cell, int row, int col);
}

