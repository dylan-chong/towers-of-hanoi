package main.gui.game.celldrawers.cellcanvas;

import main.gamemodel.Board;
import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gui.game.GameGUIModel;
import main.gui.game.GameGUIView;
import main.gui.game.celldrawers.CellDrawer;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BoardCanvas extends CellCanvas {

	private final Board board;

	public BoardCanvas(
			Board board,
			GameGUIModel gameGUIModel,
			CellDrawer cellDrawer,
			String titleOrNull
	) {
		super(gameGUIModel, cellDrawer, titleOrNull);
		this.board = board;
	}

	@Override
	protected void paintCanvasComponent(Graphics2D graphics2D) {
		// Draw chequered grid
		for (int r = 0; r < board.getNumRows(); r++) {
			boolean isDark = r % 2 == 0;
			for (int c = 0; c < board.getNumCols(); c++) {
				fillSquare(r, c, graphics2D, isDark ? Color.GRAY : Color.WHITE);
				isDark = !isDark;
			}
		}

		drawCreationSquares(graphics2D);

		super.paintCanvasComponent(graphics2D);
	}

	@Override
	protected void forEachCell(CellConsumer cellConsumer) {
		board.forEachCell(cellConsumer);
	}

	private void drawCreationSquares(Graphics2D graphics2D) {
		List<Color> playerColours = gameGUIModel.getGameModel()
				.getPlayers()
				.stream()
				.map(p -> p.getPlayerCell()
						.getToken()
						.color
						.darker()
				)
				.collect(Collectors.toList());
		int rows = board.getNumRows();
		int cols = board.getNumCols();
		int off = GameModel.CREATION_CELL_OFFSET;

		fillSquare(off, off, graphics2D, playerColours.get(0));
		fillSquare(rows - off - 1, cols - off - 1, graphics2D, playerColours.get(1));
	}

	private void fillSquare(int r, int c, Graphics2D graphics2D, Color color) {
		int cellSize = GameGUIView.PREFERRED_BOARD_CELL_SIZE;

		graphics2D.setColor(color);
		graphics2D.fillRect(
				r * cellSize,
				c * cellSize,
				cellSize,
				cellSize
		);
	}
}
