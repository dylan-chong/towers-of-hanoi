package main.gui.game;

import aurelienribon.slidinglayout.SLAnimator;
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

public class GameGUIView implements GUICard, Observer {
	public static final int PREFERRED_BOARD_CELL_SIZE = 50;
	public static final int TRANSITION_DURATION = 1;

	private final GameGUIModel gameGUIModel;
	private final GameGUIController gameGUIController;
	private final CellDrawer cellDrawer;

	private final JPanel rootJPanel;
	private JToolBar jToolBar;
	private JPanel cellCanvasesJPanel;
	private Map<Player, PlayerComponents> playerComponents;
	private GameGUIModel.GUIState currentGuiState;

	public GameGUIView(
			GameGUIModel gameGUIModel,
			GameGUIController gameGUIController,
			CellDrawer cellDrawer
	) {
		this.gameGUIModel = gameGUIModel;
		this.gameGUIController = gameGUIController;
		this.cellDrawer = cellDrawer;

		gameGUIModel.addObserver(this);

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));

		setupToolbar();
		setupCellCanvases();

		SwingUtilities.invokeLater(() -> {
			try {
				// TODO remove
				gameGUIModel.getGameModel().create('a', 0);
				gameGUIModel.getGameModel().move('a', Direction.SOUTH);
				gameGUIModel.getGameModel().passTurnState();
				gameGUIModel.getGameModel().create('c', 0);
				gameGUIModel.getGameModel().move('c', Direction.NORTH);
				gameGUIModel.getGameModel().passTurnState();
			} catch (InvalidMoveException e) {
				throw new RuntimeException(e);
			}}
		);
	}

	private void setupCellCanvases() {
		cellCanvasesJPanel = new JPanel();
		cellCanvasesJPanel.setLayout(
				new BoxLayout(cellCanvasesJPanel, BoxLayout.X_AXIS)
		);
		rootJPanel.add(cellCanvasesJPanel);

		Board board = gameGUIModel.getGameModel().getBoard();
		BoardCanvas boardCanvas = new BoardCanvas(
				board, gameGUIModel.getGameModel(), cellDrawer, "Board"
		) {
			@Override
			protected void onCellClick(Cell cell, MouseEvent e) {
				gameGUIController.onBoardCellClick(cell, e);
			}
		};

		List<Player> players = gameGUIModel.getGameModel().getPlayers();
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
								gameGUIModel,
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

		addToolbarButton("Undo", gameGUIModel.getGameModel()::undo);
		addToolbarButton("Pass", gameGUIModel.getGameModel()::passTurnState);
		addToolbarButton("Surrender", gameGUIModel.getGameModel()::surrender);
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
		SwingUtilities.invokeLater(() -> {
			updateState();

			rootJPanel.repaint();

			if (gameGUIModel.getGameModel().getTurnState() == TurnState.GAME_FINISHED) {
				Player winner = gameGUIModel.getGameModel().getWinner();
				JOptionPane.showMessageDialog(
						rootJPanel,
						String.format("Player '%s' won!", winner.getName()),
						"Game Finished",
						JOptionPane.PLAIN_MESSAGE
				);
			}
		});
	}

	private void updateState() {
		GameGUIModel.GUIState nextGuiState = gameGUIModel.getGuiState();
		StateHooksMapper hooksMapper = new StateHooksMapper();

		if (currentGuiState != null) {
			StateHooks currentStateHooks = currentGuiState.getFromMap(hooksMapper);
			currentStateHooks.onExit();
		}
		StateHooks nextStateHooks = nextGuiState.getFromMap(hooksMapper);
		nextStateHooks.onEnter();

		currentGuiState = nextGuiState;
	}

	private interface GameAction {
		void perform() throws Exception;
	}

	private interface StateHooks {
		void onEnter();
		void onExit();
	}

	/**
	 * TODO move to controller?
	 */
	private class StateHooksMapper implements GameGUIModel.GUIState.Mapper<StateHooks> {
		@Override
		public StateHooks getCreatePieceCreationValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					// This should already be showing, but code is here
					playerComponents
							.get(gameGUIModel.getCurrentPlayer())
							.showCreateCreationCanvas();
				}

				@Override
				public void onExit() {
				}
			};
		}

		@Override
		public StateHooks getCreatePieceRotationValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					playerComponents
							.get(gameGUIModel.getCurrentPlayer())
							.showCreateRotationCanvas();
				}

				@Override
				public void onExit() {
					playerComponents
							.get(gameGUIModel.getCurrentPlayer())
							.showCreateCreationCanvas();
				}
			};
		}

		@Override
		public StateHooks getMovingOrRotatingPieceValue() {
			return null;
		}

		@Override
		public StateHooks getResolvingReactionsValue() {
			return null;
		}

		@Override
		public StateHooks getGameFinishedValue() {
			return null;
		}
	}
}
