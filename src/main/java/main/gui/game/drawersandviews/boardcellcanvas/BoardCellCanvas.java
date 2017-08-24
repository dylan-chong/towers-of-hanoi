package main.gui.game.drawersandviews.boardcellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gamemodel.Player;
import main.gui.game.GameGUI;
import main.gui.game.drawersandviews.BoardCellDrawer;

import javax.swing.*;
import java.awt.*;

/**
 * Draws a grid of board cells
 */
public abstract class BoardCellCanvas extends JComponent {
    protected final GameModel gameModel;
    protected final BoardCellDrawer boardCellDrawer;

    public BoardCellCanvas(
            int preferredRows,
            int preferredColumns,
            GameModel gameModel,
            BoardCellDrawer boardCellDrawer
    ) {
        this.gameModel = gameModel;
        this.boardCellDrawer = boardCellDrawer;

        setPreferredSize(new Dimension(
                preferredColumns * GameGUI.PREFERRED_BOARD_CELL_SIZE,
                preferredRows * GameGUI.PREFERRED_BOARD_CELL_SIZE
        ));
    }

    @Override
protected void paintComponent(Graphics g) {
Graphics2D graphics2D = (Graphics2D) g;

        forEachCell((cell, row, col) -> {
            if (cell == null) {
                return;
            }

            Player player = gameModel.getPlayerOfCell(cell);
            boardCellDrawer.valueOf(cell).draw(
                    player,
                    graphics2D,
                    col,
                    row,
                    GameGUI.PREFERRED_BOARD_CELL_SIZE
            );
        });
    }

    protected abstract void forEachCell(CellConsumer cellConsumer);
}
