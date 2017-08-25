package main.gui.game.drawersandviews.cellcanvas;

import main.gamemodel.Board;
import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gui.game.GameGUIView;
import main.gui.game.drawersandviews.CellDrawer;

import java.awt.*;

public abstract class BoardCanvas extends CellCanvas {

    private final Board board;

    public BoardCanvas(
            Board board,
            GameModel gameModel,
            CellDrawer cellDrawer,
			String titleOrNull
    ) {
        super(gameModel, cellDrawer, titleOrNull);
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        int cellSize = GameGUIView.PREFERRED_BOARD_CELL_SIZE;

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
