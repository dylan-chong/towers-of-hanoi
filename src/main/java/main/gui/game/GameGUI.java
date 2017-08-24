package main.gui.game;

import aurelienribon.slidinglayout.*;
import main.GameUtils;
import main.gamemodel.*;
import main.gamemodel.cells.Cell;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.game.drawersandviews.CellDrawer;
import main.gui.game.drawersandviews.cellcanvas.BoardCanvas;
import main.gui.game.drawersandviews.cellcanvas.GridCanvas;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameGUI implements GUICard, Observer {
	public static final int PREFERRED_BOARD_CELL_SIZE = 50;
	private static final int TRANSITION_DURATION = 1;

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
						PlayerComponents::new
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

	private class PlayerComponents {

		public final SLPanel createPanel;
		public final GridCanvas createCreationCanvas;
		public final GridCanvas createRotationsCanvas;
		public final GridCanvas cemeteryCanvas;
		private final SLConfig createCreationConfig;
		private final SLConfig createRotationConfig;

		public PlayerComponents(Player player) {
			String name = player.getPlayerCell().getToken().name();

			createCreationCanvas = newGridCanvas(
					player.getUnusedPieces()::values,
					String.format("Creatable (%s)", name),
					gameGUIController::onCreationCellClick
			);
			createRotationsCanvas = newGridCanvas(
					player.getUnusedPieces()::values,// TODO
					String.format("Select Rotation (%s)", name),
					gameGUIController::onCreationRotationCellClick
			);
			createPanel = new SLPanel();
			createPanel.setPreferredSize(createCreationCanvas.getPreferredSize());
			createPanel.setTweenManager(SLAnimator.createTweenManager());
			createCreationConfig = new SLConfig(createPanel)
					.row(1f)
					.col(1f)
					.place(0, 0, createCreationCanvas);
			createRotationConfig = new SLConfig(createPanel)
					.row(1f)
					.col(1f)
					.place(0, 0, createRotationsCanvas);
			createPanel.initialize(createCreationConfig);

			AtomicBoolean aBoolean = new AtomicBoolean(true);
			new Timer(3000, e -> {
				SwingUtilities.invokeLater(() -> {
					System.out.println(aBoolean);
					if (aBoolean.get()) {
						SLKeyframe keyframe =
								new SLKeyframe(createRotationConfig, TRANSITION_DURATION)
										.setStartSide(SLSide.LEFT, createRotationsCanvas)
										.setEndSide(SLSide.RIGHT, createCreationCanvas);
						createPanel.createTransition()
								.push(keyframe)
								.play();
					}else {
						SLKeyframe keyframe =
								new SLKeyframe(createCreationConfig, TRANSITION_DURATION)
										.setStartSide(SLSide.LEFT, createCreationCanvas)
										.setEndSide(SLSide.RIGHT, createRotationsCanvas);
						createPanel.createTransition()
								.push(keyframe)
								.play();
					}
					aBoolean.set(!aBoolean.get());
				});
			}).start();

			cemeteryCanvas = newGridCanvas(
					player.getDeadPieces()::values,
					String.format("Dead (%s)", name),
					(cell, mouseEvent) -> {}
			);
		}

		public List<? extends JComponent> getAllComponents() {
			return Arrays.asList(createPanel, cemeteryCanvas);
		}

		private GridCanvas newGridCanvas(
				Supplier<Collection<? extends Cell>> cellGetter,
				String title,
				BiConsumer<Cell, MouseEvent> onClickHandler
		) {
			return new GridCanvas(
					(columns) -> GameUtils.packCells(
							cellGetter.get()
									.stream()
									.sorted()
									.collect(Collectors.toList()),
							columns
					),
					gameModel,
					cellDrawer,
					title
			) {
				@Override
				protected void onCellClick(Cell cell, MouseEvent e) {
					onClickHandler.accept(cell, e);
				}
			};
		}

	}

}
