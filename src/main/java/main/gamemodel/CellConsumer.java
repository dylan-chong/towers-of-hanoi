package main.gamemodel;

import main.gamemodel.cells.BoardCell;

public interface CellConsumer {
	void apply(BoardCell cell, int row, int col);
}

