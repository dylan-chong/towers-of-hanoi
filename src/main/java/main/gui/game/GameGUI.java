package main.gui.game;

import main.gamemodel.GameModel;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class GameGUI implements GUICard, Observer {
	private static final int PREFERRED_BOARD_CELL_SIZE = 50;

	private final JPanel rootJPanel;
	private final GameModel gameModel;
	private final GameGUIController gameGUIController;

	public GameGUI(GameModel gameModel, GameGUIController gameGUIController) {
		this.gameModel = gameModel;
		this.gameGUIController = gameGUIController;

		rootJPanel = new JPanel();

		JComponent boardCanvas = new BoardCanvas();
		rootJPanel.add(boardCanvas);
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
            float size = PREFERRED_BOARD_CELL_SIZE;
            gameModel.getBoard().forEachCell((cell, row, col) -> {
				if (cell == null) {
					return;
				}

				graphics2D.fillOval(
						(int) (col * size),
						(int) (row * size),
						(int) (size),
						(int) (size)
				);
			});
        }
	}
}
