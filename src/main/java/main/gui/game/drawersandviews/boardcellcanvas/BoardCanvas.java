package main.gui.game.drawersandviews.boardcellcanvas;

import main.gamemodel.Board;
import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gui.game.GameGUI;
import main.gui.game.drawersandviews.BoardCellDrawer;

import java.awt.*;

public class BoardCanvas extends BoardCellCanvas {

    private final Board board;

    public BoardCanvas(
            Board board,
            GameModel gameModel,
            BoardCellDrawer boardCellDrawer,
			String titleOrNull
    ) {
        super(gameModel, boardCellDrawer, titleOrNull);
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        int cellSize = GameGUI.PREFERRED_BOARD_CELL_SIZE;

        // Draw chequered grid
        for (int r = 0; r < board.getNumRows(); r++) {
            boolean isDark = r % 2 == 0;
            for (int c = 0; c < board.getNumCols(); c++) {
                graphics2D.setColor(isDark ? Color.GRAY : Color.WHITE);
                graphics2D.fillRect(
                        r * cellSize,
                        c * cellSize,
                        cellSize,
                        cellSize
                );
                isDark = !isDark;
            }
        }

        super.paintComponent(g);
    }

    @Override
    protected void forEachCell(CellConsumer cellConsumer) {
        board.forEachCell(cellConsumer);
    }
}
