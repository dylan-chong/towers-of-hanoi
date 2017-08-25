package main.gui.game.drawersandviews.cellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.cells.Cell;
import main.gui.game.GameGUIModel;
import main.gui.game.drawersandviews.CellDrawer;

import java.util.List;

public abstract class GridCanvas extends CellCanvas {

	private static final int DEFAULT_NUM_COLS = 3;

	private final GridSupplier gridSupplier;

    public GridCanvas(
            GridSupplier gridSupplier,
            GameGUIModel gameGUIModel,
            CellDrawer cellDrawer,
			String titleOrNull
    ) {
        super(gameGUIModel, cellDrawer, titleOrNull);
        this.gridSupplier = gridSupplier;
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
