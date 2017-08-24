package main.gui.game;

import aurelienribon.slidinglayout.*;
import main.gamemodel.*;
import main.gamemodel.cells.Cell;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.game.drawersandviews.CellDrawer;
import main.gui.game.drawersandviews.cellcanvas.BoardCanvas;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

public class GameGUI implements GUICard, Observer {
	public static final int PREFERRED_BOARD_CELL_SIZE = 50;
	public static final int TRANSITION_DURATION = 1;

	private final GameModel gameModel;
	private final GameGUIController gameGUIController;
	private final CellDrawer cellDrawer;

	private final JPanel rootJPanel;
	private JToolBar jToolBar;
	private JPanel cellCanvasesJPanel;
	private Map<Player, PlayerComponents> playerComponents;

	public GameGUI(
			GameModel gameModel,
			GameGUIController gameGUIController,
			CellDrawer cellDrawer
	) {
		this.gameModel = gameModel;
		this.gameGUIController = gameGUIController;
		this.cellDrawer = cellDrawer;

		gameModel.addObserver(this);

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));

		setupToolbar();
		setupCellCanvases();

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

	private void setupCellCanvases() {
		cellCanvasesJPanel = new JPanel();
		cellCanvasesJPanel.setLayout(
				new BoxLayout(cellCanvasesJPanel, BoxLayout.X_AXIS)
		);
		rootJPanel.add(cellCanvasesJPanel);

		Board board = gameModel.getBoard();
		BoardCanvas boardCanvas = new BoardCanvas(
				board, gameModel, cellDrawer, "Board"
		) {
			@Override
			protected void onCellClick(Cell cell, MouseEvent e) {
				gameGUIController.onBoardCellClick(cell, e);
			}
		};

		List<Player> players = gameModel.getPlayers();
		if (players.size() != 2) {
			throw new IllegalGameStateException("Must be 2 players");
		}
		playerComponents = players
				.stream()
				.collect(Collectors.toMap(
						player -> player,
						player -> new PlayerComponents(
								player,
								gameGUIController,
								gameModel,
								cellDrawer
						)
				));
		SLAnimator.start();

		// Add player zero components, the board, then player one

		playerComponents
				.get(players.get(0))
				.getAllComponents()
				.forEach(cellCanvasesJPanel::add);

		cellCanvasesJPanel.add(boardCanvas);

		List<? extends JComponent> player1Components = new ArrayList<>(
				playerComponents
						.get(players.get(1))
						.getAllComponents()
		);
		Collections.reverse(player1Components);
		player1Components.forEach(cellCanvasesJPanel::add);
	}

	private void setupToolbar() {
		jToolBar = new JToolBar();
		rootJPanel.add(jToolBar);

		addToolbarButton("Undo", gameModel::undo);
		addToolbarButton("Pass", gameModel::passTurnState);
		addToolbarButton("Surrender", gameModel::surrender);
	}

	private void addToolbarButton(String title, GameAction action) {
		JButton button = new JButton(title);
		button.addActionListener(performGameAction(action));
		jToolBar.add(button);
	}

	private ActionListener performGameAction(GameAction action) {
		return (event) -> {
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
			Player winner = gameModel.getWinner();
			JOptionPane.showMessageDialog(
					rootJPanel,
					String.format("Player '%s' won!", winner.getName()),
					"Game Finished",
					JOptionPane.PLAIN_MESSAGE
			);
		}
	}

	private interface GameAction {
		void perform() throws Exception;
	}

}
