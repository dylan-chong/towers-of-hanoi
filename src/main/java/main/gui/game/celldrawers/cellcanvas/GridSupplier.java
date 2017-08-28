package main.gui.game.celldrawers.cellcanvas;

import main.gamemodel.cells.Cell;

import java.util.List;
import java.util.function.Function;

public interface GridSupplier extends
		Function<Integer, List<? extends List<? extends Cell>>> {
}
