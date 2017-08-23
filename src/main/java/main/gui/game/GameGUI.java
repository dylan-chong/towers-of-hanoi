package main.gui.game;

import main.gamemodel.Direction;
import main.gamemodel.GameModel;
import main.gamemodel.InvalidMoveException;
import main.gamemodel.PlayerData;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.game.drawers.BoardCellDrawer;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class GameGUI implements GUICard, Observer {
	private static final int PREFERRED_BOARD_CELL_SIZE = 50;

	private final JPanel rootJPanel;
	private final GameModel gameModel;
	private final GameGUIController gameGUIController;
	private final BoardCellDrawer boardCellDrawer;

	public GameGUI(GameModel gameModel,
				   GameGUIController gameGUIController,
				   BoardCellDrawer boardCellDrawer) {
		this.gameModel = gameModel;
		this.gameGUIController = gameGUIController;
		this.boardCellDrawer = boardCellDrawer;

		rootJPanel = new JPanel();

		JComponent boardCanvas = new BoardCanvas();
		rootJPanel.add(boardCanvas);

		try {
			// TODO remove
			gameModel.create('a', 0);
			gameModel.move('a', Direction.SOUTH);
			gameModel.passTurnState();
			gameModel.create('c', 0);
			gameModel.move('c', Direction.NORTH);
		} catch (InvalidMoveException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public JComponent getRootComponent() {
		return rootJPanel;
	}

	@Override
	public GUICardName getCardName() {
		return GUICardName.GAME;
	}

	@Override
	public void update(Observable o, Object arg) {
		rootJPanel.repaint();
	}

	private class BoardCanvas extends JComponent {
		public BoardCanvas() {
			setPreferredSize(new Dimension(
					gameModel.getBoard().getNumCols() * PREFERRED_BOARD_CELL_SIZE,
					gameModel.getBoard().getNumRows() * PREFERRED_BOARD_CELL_SIZE
			));
		}

		@Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g;
			gameModel.getBoard().forEachCell((cell, row, col) -> {
				if (cell == null) {
					return;
				}

				PlayerData player = gameModel.getPlayerOfCell(cell);
				boardCellDrawer.valueOf(cell).draw(
						player,
						graphics2D,
						col,
						row,
						PREFERRED_BOARD_CELL_SIZE
				);
			});
		}
	}
}
