package main.gui.game.drawersandviews.boardcellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.GameModel;
import main.gamemodel.Player;
import main.gui.game.GameGUI;
import main.gui.game.drawersandviews.BoardCellDrawer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Draws a grid of board cells
 */
public abstract class BoardCellCanvas extends JPanel {
	protected final GameModel gameModel;
	protected final BoardCellDrawer boardCellDrawer;

	public BoardCellCanvas(
			GameModel gameModel,
			BoardCellDrawer boardCellDrawer,
			String titleOrNull
	) {
		this.gameModel = gameModel;
		this.boardCellDrawer = boardCellDrawer;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new LineBorder(Color.GRAY, 1));

		if (titleOrNull != null) {
			JLabel titleLabel = new JLabel(titleOrNull, SwingConstants.CENTER);
			titleLabel.setAlignmentX(CENTER_ALIGNMENT);
			add(titleLabel);
		}

		JComponent cellCanvas = new JComponent() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				paintCanvasComponent(((Graphics2D) g));
			}

			@Override
			public Dimension getPreferredSize() {
				int[] preferredRowsCols = calculatePreferredRowsCols();
				return new Dimension(
						preferredRowsCols[1] * GameGUI.PREFERRED_BOARD_CELL_SIZE,
						preferredRowsCols[0] * GameGUI.PREFERRED_BOARD_CELL_SIZE
				);
			}
		};
		cellCanvas.setAlignmentX(CENTER_ALIGNMENT);
		add(cellCanvas);
	}

	protected void paintCanvasComponent(Graphics2D graphics2D) {
		int cellSize = GameGUI.PREFERRED_BOARD_CELL_SIZE;
		int[] rowsCols = calculatePreferredRowsCols();
		graphics2D.setColor(Color.GRAY);
		graphics2D.setStroke(new BasicStroke(1));
		graphics2D.drawRect(
				0, 0,
				cellSize * rowsCols[1],
				cellSize * rowsCols[0]
		);

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
					cellSize
			);
		});
	}

	protected abstract void forEachCell(CellConsumer cellConsumer);

	protected int[] calculatePreferredRowsCols() {
		int[] rowsCols = {-1, -1};
		forEachCell((cell, row, col) -> {
			rowsCols[0] = Math.max(row, rowsCols[0]);
			rowsCols[1] = Math.max(col, rowsCols[1]);
		});
		rowsCols[0] += 1;
		rowsCols[1] += 1;
		return rowsCols;
	}
}
