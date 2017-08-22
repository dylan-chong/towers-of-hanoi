package main.gui.game.drawers;

import main.gamemodel.PlayerData;
import main.gamemodel.cells.BoardCellMapper;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;

import java.awt.*;

public class BoardCellDrawer implements
		BoardCellMapper<BoardCellDrawer.DrawFunction> {

	@Override
	public DrawFunction valueOfPieceCell(PieceCell cell) {
		return (playerData, graphics2D, col, row, size) -> {
			Color color = playerData.getPlayerCell().getToken().color;
			graphics2D.setPaint(new RadialGradientPaint(
					(col + 0.5f) * size,
					(row + 0.5f) * size,
					size / 2,
					new float[]{0, 1},
					new Color[]{color.brighter(), color}
			));
			graphics2D.fillOval(
					(int) (col * size),
					(int) (row * size),
					(int) (size),
					(int) (size)
			);
		};
	}

	@Override
	public DrawFunction valueOfPlayerCell(PlayerCell cell) {
		return (playerData, graphics2D, col, row, size) -> {
			graphics2D.setColor(playerData.getPlayerCell().getToken().color);
			graphics2D.fillRect(
					(int) (col * size),
					(int) (row * size),
					(int) (size),
					(int) (size)
			);
		};
	}

	public interface DrawFunction {
		void draw(
				PlayerData playerData,
				Graphics2D graphics2D,
				int col,
				int row,
				float size
		);
	}
}
