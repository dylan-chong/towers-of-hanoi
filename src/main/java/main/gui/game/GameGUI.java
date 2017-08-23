package main.gui.game;

import main.gamemodel.*;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.game.drawers.BoardCellDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

public class GameGUI implements GUICard, Observer {
	private static final int PREFERRED_BOARD_CELL_SIZE = 50;

	private final JPanel rootJPanel;
	private final GameModel gameModel;
	private final GameGUIController gameGUIController;
	private final BoardCellDrawer boardCellDrawer;
	private final JToolBar jToolBar;

	public GameGUI(GameModel gameModel,
				   GameGUIController gameGUIController,
				   BoardCellDrawer boardCellDrawer) {
		this.gameModel = gameModel;
		this.gameGUIController = gameGUIController;
		this.boardCellDrawer = boardCellDrawer;
		gameModel.addObserver(this);

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));

		// Toolbar

		jToolBar = new JToolBar();
		rootJPanel.add(jToolBar);

		addToolbarButton("Undo", gameModel::undo);
		addToolbarButton("Pass", gameModel::passTurnState);
		addToolbarButton("Surrender", gameModel::surrender);

		// Board

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

	private void addToolbarButton(String title, GameAction action) {
		JButton button = new JButton(title);
		button.addActionListener(performGameAction(action));
		jToolBar.add(button);
	}

	private ActionListener performGameAction(GameAction action) {
		return (event) ->  {
			try {
				action.perform();
			} catch (Exception e) {
				String message = "There was an error making your move";
				if (!(e instanceof ArrayIndexOutOfBoundsException) &&
						e.getMessage() != null) {
					message += ":\n" + e.getMessage();
				}
				JOptionPane.showMessageDialog(
						rootJPanel,
						message,
						"Error",
						JOptionPane.ERROR_MESSAGE
				);
			}
		};
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

		if (gameModel.getTurnState() == TurnState.GAME_FINISHED) {
			PlayerData winner = gameModel.getWinner();
			JOptionPane.showMessageDialog(
					rootJPanel,
					String.format("Player '%s' won!", winner.getName()),
					"Game Finished",
					JOptionPane.PLAIN_MESSAGE
			);
		}
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
			Board board = gameModel.getBoard();
			int cellSize = PREFERRED_BOARD_CELL_SIZE;

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

			board.forEachCell((cell, row, col) -> {
				if (cell == null) {
					return;
				}

				PlayerData player = gameModel.getPlayerOfCell(cell);
				boardCellDrawer.valueOf(cell).draw(
						player,
						graphics2D,
						col,
						row,
						cellSize
				);
			});
		}
	}

	private interface GameAction {
		void perform() throws Exception;
	}
}
