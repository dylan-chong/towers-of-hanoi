package main.gui.game;

import main.gamemodel.Direction;
import main.gamemodel.Player;
import main.gamemodel.ReactionData;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gui.game.celldrawers.cellcanvas.CellClickEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

import static main.gui.game.Events.*;

public class GameGUIController {
	public static final double MOVE_CLICK_EDGE_DISTANCE = 0.3;

	private final GameGUIModel gameGUIModel;

	public GameGUIController(
			GameGUIModel gameGUIModel,
			EventReactionClicked eventReactionClicked
	) {
		this.gameGUIModel = gameGUIModel;

		eventReactionClicked.registerListener(this::onReactionClick);
	}

	public void addActions(BiConsumer<KeyStroke, Action> actionAdder) {
		actionAdder.accept(KeyStroke.getKeyStroke("UP"), new MoveAction(Direction.NORTH));
		actionAdder.accept(KeyStroke.getKeyStroke("DOWN"), new MoveAction(Direction.SOUTH));
		actionAdder.accept(KeyStroke.getKeyStroke("LEFT"), new MoveAction(Direction.WEST));
		actionAdder.accept(KeyStroke.getKeyStroke("RIGHT"), new MoveAction(Direction.EAST));
	}

	public void onBoardCellClick(Cell cell, CellClickEvent e) {
		gameGUIModel.performGameAction(() -> {
			if (cell == null || !(cell instanceof PieceCell)) {
				return;
			}

			if (gameGUIModel.getGuiState() == GUIState.MOVING_OR_ROTATING_PIECE_APPLYING) {
				gameGUIModel.performGameAction(() -> {
					Direction clickedEdge = e.getClickedEdge();
					if (clickedEdge == null) {
						gameGUIModel.enterRotationMode();
					} else  {
						gameGUIModel.move(clickedEdge);
					}
				});
				return;
			}

			if (gameGUIModel.getGuiState() != GUIState.MOVING_OR_ROTATING_PIECE_SELECTION) {
				return;
			}

			Player currentPlayer = gameGUIModel.getCurrentPlayer();
			if (!currentPlayer.ownsPiece(cell)) {
				return;
			}

			gameGUIModel.setMovementOrRotationSelectedCell((PieceCell) cell);
		});
	}

	public void onCreationCellClick(Cell cell, CellClickEvent e) {
		gameGUIModel.performGameAction(() -> {
			if (cell == null) {
				return;
			}

			if (gameGUIModel.getGuiState() != GUIState.CREATE_PIECE_CREATION) {
				return;
			}

			Player currentPlayer = gameGUIModel.getCurrentPlayer();
			if (!currentPlayer.ownsPiece(cell)) {
				return;
			}

			gameGUIModel
					.getGameModel()
					.requireCanCreatePiece();

			gameGUIModel.setCreationSelectedCell((PieceCell) cell);
		});
	}

	public void onCreationRotationCellClick(
			Cell rotatedCellCopy,
			CellClickEvent mouseEvent
	) {
		gameGUIModel.performGameAction(() -> {
			if (rotatedCellCopy == null) {
				return;
			}

			if (gameGUIModel.getGuiState() != GUIState.CREATE_PIECE_ROTATION) {
				return;
			}

			gameGUIModel.createPiece(rotatedCellCopy);
		});
	}

	private void onReactionClick(ReactionData.Pair pair) {
		if (gameGUIModel.getGuiState() != GUIState.RESOLVING_REACTIONS) {
			return;
		}

		gameGUIModel.performGameAction(() -> {
			gameGUIModel.getGameModel().react(pair);
		});
	}

	private class MoveAction extends AbstractAction {
		private final Direction moveDirection;

		private MoveAction(Direction moveDirection) {
			this.moveDirection = moveDirection;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (gameGUIModel.getGuiState() != GUIState.MOVING_OR_ROTATING_PIECE_APPLYING) {
				return;
			}

			gameGUIModel.performGameAction(() -> {
				gameGUIModel.move(moveDirection);
			});
		}
	}
}
