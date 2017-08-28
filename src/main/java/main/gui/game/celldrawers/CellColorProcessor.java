package main.gui.game.celldrawers;

import main.gamemodel.cells.Cell;

import java.awt.*;

public interface CellColorProcessor {
	Color process(Color color, Cell cell);
}
