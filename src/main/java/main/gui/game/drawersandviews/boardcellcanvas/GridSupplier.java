package main.gui.game.drawersandviews.boardcellcanvas;

import main.gamemodel.cells.BoardCell;

import java.util.List;
import java.util.function.Function;

public interface GridSupplier extends
		Function<Integer, List<? extends List<? extends BoardCell>>> {
}
