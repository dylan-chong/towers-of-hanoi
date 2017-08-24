package main.gui.game;

import main.GameUtils;
import main.gamemodel.*;
import main.gamemodel.cells.BoardCell;
import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.game.drawersandviews.BoardCellDrawer;
import main.gui.game.drawersandviews.boardcellcanvas.BoardCanvas;
import main.gui.game.drawersandviews.boardcellcanvas.GridCanvas;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameGUI implements GUICard, Observer {
	public static final int PREFERRED_BOARD_CELL_SIZE = 50;

	private final GameModel gameModel;
	private final GameGUIController gameGUIController;
	private final BoardCellDrawer boardCellDrawer;

	private final JPanel rootJPanel;
	private JToolBar jToolBar;
	private JPanel boardCellCanvasesJPanel;

	public GameGUI(
			GameModel gameModel,
			GameGUIController gameGUIController,
			BoardCellDrawer boardCellDrawer
	) {
		this.gameModel = gameModel;
		this.gameGUIController = gameGUIController;
		this.boardCellDrawer = boardCellDrawer;

		gameModel.addObserver(this);

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));

		setupToolbar();
		setupBoardCellCanvases();

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

	private void setupBoardCellCanvases() {
		boardCellCanvasesJPanel = new JPanel();
		boardCellCanvasesJPanel.setLayout(
				new BoxLayout(boardCellCanvasesJPanel, BoxLayout.X_AXIS)
		);
		rootJPanel.add(boardCellCanvasesJPanel);

		Board board = gameModel.getBoard();
		BoardCanvas boardCanvas = new BoardCanvas(
				board, gameModel, boardCellDrawer, "Board"
		) {
			@Override
			protected void onCellClick(BoardCell cell, MouseEvent e) {
				gameGUIController.onBoardCellClick(cell, e);
			}
		};

		List<Player> players = gameModel.getPlayers();
		if (players.size() != 2) {
			throw new IllegalGameStateException("Must be 2 players");
		}
		List<List<? extends JComponent>> playerCanvases = players
				.stream()
				.map(this::getPlayerCanvases)
				.collect(Collectors.toList());

		for (int i = 0; i < playerCanvases.get(0).size(); i++) {
			boardCellCanvasesJPanel.add(playerCanvases.get(0).get(i));
		}
		boardCellCanvasesJPanel.add(boardCanvas);
		for (int i = playerCanvases.get(1).size() - 1; i >= 0; i--) {
			boardCellCanvasesJPanel.add(playerCanvases.get(1).get(i));
		}
	}

	private List<? extends JComponent> getPlayerCanvases(Player player) {
		String name = player.getPlayerCell().getToken().name();
		return Arrays.asList(
				newGridCanvas(
						player.getUnusedPieces()::values,
						String.format("Creatable (%s)", name)
				),
				newGridCanvas(
						player.getDeadPieces()::values,
						String.format("Dead (%s)", name)
				)
		);
	}

	private GridCanvas newGridCanvas(
			Supplier<Collection<? extends BoardCell>> cellGetter,
			String title
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
				 boardCellDrawer,
				 title
		 ) {
			 @Override
			 protected void onCellClick(BoardCell cell, MouseEvent e) {
			 	gameGUIController.onCreationCellClick(cell, e);
			 }
		 };
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
