package main.gui.game.drawersandviews.cellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gamemodel.cells.Cell;
import main.gui.game.drawersandviews.CellDrawer;

import java.util.List;

public abstract class GridCanvas extends CellCanvas {

    private final GridSupplier gridSupplier;

    public GridCanvas(
            GridSupplier gridSupplier,
            GameModel gameModel,
            CellDrawer cellDrawer,
			String titleOrNull
    ) {
        super(gameModel, cellDrawer, titleOrNull);
        this.gridSupplier = gridSupplier;
    }

    @Override
    protected void forEachCell(CellConsumer cellConsumer) {
        List<? extends List<? extends Cell>> cellRows =
                gridSupplier.apply(2);

        // TODO don't use 2

        for (int r = 0; r < cellRows.size(); r++) {
            List<? extends Cell> row = cellRows.get(r);
            for (int c = 0; c < row.size(); c++) {
                Cell cell = row.get(c);
                cellConsumer.apply(cell, r, c);
            }
        }
    }
}
