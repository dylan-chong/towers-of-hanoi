package main.gui.game;

import main.gamemodel.Board;
import main.gamemodel.IllegalGameStateException;
import main.gamemodel.Player;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.cardview.GUICardView;
import main.gui.game.celldrawers.CellDrawer;
import main.gui.game.celldrawers.cellcanvas.BoardCanvas;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameGUIView implements GUICard, Observer {
	public static final int PREFERRED_BOARD_CELL_SIZE = 50;
	public static final int TRANSITION_DURATION = 1;

	private final GUICardView guiCardView;
	private final GameGUIModel gameGUIModel;
	private final GameGUIController gameGUIController;
	private final CellDrawer cellDrawer;
	private final CellRotationDialogShower cellRotationDialogShower;

	private final JPanel rootJPanel;
	private JToolBar jToolBar;
	private JPanel cellCanvasesJPanel;
	private Map<Player, PlayerComponents> playerComponents;
	private GUIState currentGuiState;
	private JLabel stateReporterLabel;

	public GameGUIView(
			GUICardView guiCardView,
			GameGUIModel gameGUIModel,
			GameGUIController gameGUIController,
			CellDrawer cellDrawer,
			CellRotationDialogShower cellRotationDialogShower
	) {
		this.guiCardView = guiCardView;
		this.gameGUIModel = gameGUIModel;
		this.gameGUIController = gameGUIController;
		this.cellDrawer = cellDrawer;
		this.cellRotationDialogShower = cellRotationDialogShower;

		gameGUIModel.addObserver(this);

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));
		rootJPanel.setFocusable(true);

		setUpToolbar();
		setUpStateReporter();
		setUpCellCanvases();
		setUpKeyBindings();
	}

	private void setUpStateReporter() {
		stateReporterLabel = new JLabel();
		stateReporterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		stateReporterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rootJPanel.add(stateReporterLabel);
	}

	private void setUpCellCanvases() {
		cellCanvasesJPanel = new JPanel();
		cellCanvasesJPanel.setLayout(
				new BoxLayout(cellCanvasesJPanel, BoxLayout.X_AXIS)
		);
		rootJPanel.add(cellCanvasesJPanel);

		Board board = gameGUIModel.getGameModel().getBoard();
		BoardCanvas boardCanvas = new BoardCanvas(
				board, gameGUIModel, cellDrawer, "Board"
		);
		boardCanvas.addCellClickListener(gameGUIController::onBoardCellClick);

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

	private void setUpToolbar() {
		jToolBar = new JToolBar();
		rootJPanel.add(jToolBar);

		addToolbarButton("Undo", gameGUIModel.getGameModel()::undo);
		addToolbarButton("Pass", gameGUIModel.getGameModel()::passTurnState);
		addToolbarButton("Surrender", gameGUIModel.getGameModel()::surrender);
	}

	private void setUpKeyBindings() {
		gameGUIController.addActions((keyStroke, action) -> {
			Object actionKey = keyStroke;
			rootJPanel
					.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(keyStroke, actionKey);
			rootJPanel
					.getActionMap()
					.put(actionKey, action);
		});
	}

	private void addToolbarButton(String title, GameAction action) {
		JButton button = new JButton(title);
		button.addActionListener((event) ->
				gameGUIModel.performGameAction(action)
		);
		button.setFocusable(false);
		jToolBar.add(button);
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
			if (arg instanceof Exception) {
				showExceptionErrorMsg((Exception) arg);
				return;
			}

			updateState();

			rootJPanel.revalidate();
			rootJPanel.repaint();
		});
	}

	private void showExceptionErrorMsg(Exception e) {
		String message = "There was an error making your move";
		if (!(e instanceof ArrayIndexOutOfBoundsException) &&
				e.getMessage() != null) {
			message += ":\n" + e.getMessage();
		}
		String finalMessage = message;
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
				rootJPanel,
				finalMessage,
				"Error",
				JOptionPane.ERROR_MESSAGE
		));
		rootJPanel.revalidate();
		rootJPanel.repaint();
	}

	private void updateState() {
		GUIState nextGuiState = gameGUIModel.getGuiState();
		if (nextGuiState == currentGuiState) {
			return;
		}

		StateHooksMapper hooksMapper = new StateHooksMapper();

		if (currentGuiState != null) {
			StateHooks currentStateHooks = currentGuiState.getFromMap(hooksMapper);
			currentStateHooks.onExit();
		}
		StateHooks nextStateHooks = nextGuiState.getFromMap(hooksMapper);
		nextStateHooks.onEnter();

		currentGuiState = nextGuiState;
		updateStateReporterLabel();
	}

	private void updateStateReporterLabel() {
		String text = String.format(
				"<html>\n" +
						"<p style='text-align: center;'>\n" +
						"\tCurrent Player: <b>%s</b>\n" +
						"\t<br>\n" +
						"\tGame State: <b>%s</b>\n" +
						"</p></html>",
				gameGUIModel.getCurrentPlayer()
						.getNameWithoutNumber(),
				gameGUIModel.getGuiState()
						.name()
						.replaceAll("_", " ")
		);
		stateReporterLabel.setText(text);
	}

	private void showBothCreationCanvases() {
		for (PlayerComponents pc : playerComponents.values()) {
			pc.showCreateCreationCanvas();
		}
	}

	private void showRotationDialogue() {
		CellRotationDialogShower.ShowResult showResult = cellRotationDialogShower
				.showDialog(
						gameGUIModel.getCopyOfMovementOrRotationSelectedCell(),
						gameGUIModel.getCurrentPlayer()
				);
		if (showResult.jOptionPaneValue == JOptionPane.CANCEL_OPTION) {
			gameGUIModel.performGameAction(gameGUIModel::cancelRotation);
			return;
		}

		gameGUIModel.performGameAction(() -> {
			gameGUIModel.rotate(showResult.rotatedPieceCell);
		});
	}

	private class StateHooksMapper implements GUIState.Mapper<StateHooks> {
		@Override
		public StateHooks getCreatePieceCreationValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					// This should already be showing, but code is here to
					// ensure it is showing
					showBothCreationCanvases();
				}

				@Override
				public void onExit() {
					// Do nothing
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
					showBothCreationCanvases();
				}
			};
		}

		@Override
		public StateHooks getMovingOrRotatingPieceSelectionValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					// Do nothing
				}

				@Override
				public void onExit() {
					// Do nothing
				}
			};
		}

		@Override
		public StateHooks getMovingOrRotatingPieceApplyingValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					// Do nothing
				}

				@Override
				public void onExit() {
					// Do nothing
				}
			};
		}

		@Override
		public StateHooks getMovingOrRotatingPieceRotatingValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					SwingUtilities.invokeLater(
							GameGUIView.this::showRotationDialogue
					);
				}

				@Override
				public void onExit() {
					// TODO
				}
			};
		}

		@Override
		public StateHooks getResolvingReactionsValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					// TODO
				}

				@Override
				public void onExit() {
					// TODO
				}
			};
		}

		@Override
		public StateHooks getGameFinishedValue() {
			return new StateHooks() {
				@Override
				public void onEnter() {
					Player winner = gameGUIModel.getGameModel().getWinner();
					String name = winner.getNameWithoutNumber();
					SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
							rootJPanel,
							String.format("Player '%s' won!", name),
							"Game Finished",
							JOptionPane.PLAIN_MESSAGE
					));
				}

				@Override
				public void onExit() {
					// TODO
				}
			};
		}
	}
}
