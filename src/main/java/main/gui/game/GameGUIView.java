package main.gui.game;

import main.Utils;
import main.gamemodel.Board;
import main.gamemodel.IllegalGameStateException;
import main.gamemodel.Player;
import main.gui.cardframe.GUICard;
import main.gui.cardframe.GUICardFrame;
import main.gui.cardframe.GUICardName;
import main.gui.game.celldrawers.CellDrawer;
import main.gui.game.celldrawers.cellcanvas.BoardCanvas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static main.gui.game.Events.*;

public class GameGUIView implements GUICard, Observer {
	public static final int PREFERRED_CELL_SIZE = 50;
	public static final int TRANSITION_DURATION = 1;

	private final GameGUIModel gameGUIModel;
	private final GameGUIController gameGUIController;
	private final CellDrawer cellDrawer;
	private final CellRotationDialogShower cellRotationDialogShower;
	private final EventGameGUIViewUpdated eventGameGUIViewUpdated;
	private final EventReactionClicked eventReactionClicked;
	private final EventGameReset eventGameReset;
	private final GUICardFrame guiCardFrame;

	private final JPanel rootJPanel;
	private JToolBar jToolBar;
	private JPanel cellCanvasesJPanel;
	private Map<Player, PlayerComponents> playerComponents;
	private GUIState currentGuiState;
	private JLabel stateReporterLabel;

	public GameGUIView(
			GameGUIModel gameGUIModel,
			GameGUIController gameGUIController,
			CellDrawer cellDrawer,
			CellRotationDialogShower cellRotationDialogShower,
			EventGameGUIViewUpdated eventGameGUIViewUpdated,
			EventReactionClicked eventReactionClicked,
			EventGameReset eventGameReset,
			GUICardFrame guiCardFrame
	) {
		this.gameGUIModel = gameGUIModel;
		this.gameGUIController = gameGUIController;
		this.cellDrawer = cellDrawer;
		this.cellRotationDialogShower = cellRotationDialogShower;
		this.eventGameGUIViewUpdated = eventGameGUIViewUpdated;
		this.eventReactionClicked = eventReactionClicked;
		this.eventGameReset = eventGameReset;
		this.guiCardFrame = guiCardFrame;

		gameGUIModel.addObserver(this);

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));
		rootJPanel.setFocusable(true);
		rootJPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

		setUpToolbar();
		setUpStateReporter();
		setUpCellCanvases();
		setUpKeyBindings();
	}

	private void setUpStateReporter() {
		stateReporterLabel = new JLabel("\n\n");
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
				board,
				gameGUIModel,
				cellDrawer,
				"Board",
				eventGameGUIViewUpdated,
				eventReactionClicked
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
								cellDrawer,
								eventGameGUIViewUpdated
						)
				));

		addPlayerComponents(boardCanvas, playerComponents);
	}

	private void addPlayerComponents(
			JComponent boardCanvas,
			Map<Player, PlayerComponents> playerComponents
	) {
		List<Player> players = gameGUIModel.getGameModel().getPlayers();

		List<List<? extends JComponent>> componentGroups = players.stream()
				.map(playerComponents::get)
				.map(PlayerComponents::getAllComponents)
				.collect(Collectors.toList());
		componentGroups.add(1, Arrays.asList(boardCanvas));
		Collections.reverse(componentGroups.get(2));

		List<? extends JComponent> flatComponents = componentGroups
				.stream()
				.flatMap(List::stream)
				.collect(Collectors.toList());
		cellCanvasesJPanel.add(Utils.compositeSplit(flatComponents));
	}

	private void setUpToolbar() {
		jToolBar = new JToolBar();
		rootJPanel.add(jToolBar);

		addToolbarButton("Undo", gameGUIModel.getGameModel()::undo);
		addToolbarButton(
				"Pass",
				gameGUIModel.getGameModel()::passTurnState,
				jButton -> jButton.setFont(new Font("Default", Font.BOLD, 20))
		);
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
		addToolbarButton(title, action, (button) -> {});
	}

	private void addToolbarButton(
			String title,
			GameAction action,
			Consumer<JButton> buttonConfigurer
	) {
		JButton button = new JButton(title);
		button.addActionListener((event) ->
				gameGUIModel.performGameAction(action)
		);
		button.setFocusable(false);
		buttonConfigurer.accept(button);
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
				updateComponents();
				return;
			}

			updateState();
			updateComponents();
		});
	}

	private void updateComponents() {
		rootJPanel.revalidate();
		rootJPanel.repaint();
		eventGameGUIViewUpdated.broadcast(null);
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
				gameGUIModel
						.getCurrentPlayer()
						.getNameWithoutNumber(),
				gameGUIModel
						.getGuiState()
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
		CellRotationDialogShower.ShowResult showResult =
				cellRotationDialogShower.showDialog(
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
			return new StateHooks.Adapter() {
				@Override
				public void onEnter() {
					// This should already be showing, but code is here to
					// ensure it is showing
					showBothCreationCanvases();
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
			return new StateHooks.Adapter();
		}

		@Override
		public StateHooks getMovingOrRotatingPieceApplyingValue() {
			return new StateHooks.Adapter();
		}

		@Override
		public StateHooks getMovingOrRotatingPieceRotatingValue() {
			return new StateHooks.Adapter() {
				@Override
				public void onEnter() {
					SwingUtilities.invokeLater(
							GameGUIView.this::showRotationDialogue
					);
				}
			};
		}

		@Override
		public StateHooks getResolvingReactionsValue() {
			return new StateHooks.Adapter();
		}

		@Override
		public StateHooks getGameFinishedValue() {
			return new StateHooks.Adapter() {
				@Override
				public void onEnter() {
					Player winner = gameGUIModel.getGameModel().getWinner();
					String name = winner.getNameWithoutNumber();
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(
								rootJPanel,
								String.format("Player '%s' won!", name),
								"Game Finished",
								JOptionPane.PLAIN_MESSAGE
						);
						guiCardFrame.setCurrentView(GUICardName.MENU);
						eventGameReset.broadcast(null);
					});
				}
			};
		}
	}
}
