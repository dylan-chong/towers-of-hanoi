package main.gui.game.drawersandviews.boardcellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gamemodel.cells.BoardCell;
import main.gui.game.drawersandviews.BoardCellDrawer;

import java.util.List;

public class GridCanvas extends BoardCellCanvas {

    private final GridSupplier gridSupplier;

    public GridCanvas(
            int preferredRows,
            int preferredColumns,
            GridSupplier gridSupplier,
            GameModel gameModel,
            BoardCellDrawer boardCellDrawer
    ) {
        super(preferredRows, preferredColumns, gameModel, boardCellDrawer);
        this.gridSupplier = gridSupplier;
    }

    @Override
    protected void forEachCell(CellConsumer cellConsumer) {
        List<? extends List<? extends BoardCell>> cellRows =
                gridSupplier.apply(2);

        // TODO don't use 2

        for (int r = 0; r < cellRows.size(); r++) {
            List<? extends BoardCell> row = cellRows.get(r);
            for (int c = 0; c < row.size(); c++) {
                BoardCell cell = row.get(c);
                cellConsumer.apply(cell, r, c);
            }
        }
    }
}
