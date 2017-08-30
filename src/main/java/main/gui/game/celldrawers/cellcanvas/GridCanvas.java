package main.gui.game.celldrawers.cellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.cells.Cell;
import main.gui.game.Events.EventGameGUIViewUpdated;
import main.gui.game.GameGUIModel;
import main.gui.game.celldrawers.CellDrawer;

import java.util.List;

public class GridCanvas extends CellCanvas {

	private static final int DEFAULT_NUM_COLS = 3;

	private final GridSupplier gridSupplier;

    public GridCanvas(
            GridSupplier gridSupplier,
            GameGUIModel gameGUIModel,
            CellDrawer cellDrawer,
			String titleOrNull,
			EventGameGUIViewUpdated eventGameGUIViewUpdated
    ) {
        super(gameGUIModel, cellDrawer, titleOrNull, eventGameGUIViewUpdated);
        this.gridSupplier = gridSupplier;
    }

	@Override
	protected int[] minRowsCols() {
		return new int[]{1, DEFAULT_NUM_COLS};
	}

	@Override
    protected void forEachCell(CellConsumer cellConsumer) {
        List<? extends List<? extends Cell>> cellRows =
                gridSupplier.apply(DEFAULT_NUM_COLS);

        for (int r = 0; r < cellRows.size(); r++) {
            List<? extends Cell> row = cellRows.get(r);
            for (int c = 0; c < row.size(); c++) {
                Cell cell = row.get(c);
                cellConsumer.apply(cell, r, c);
            }
        }
    }

}
