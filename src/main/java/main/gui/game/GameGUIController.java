package main.gui.game;

import main.gamemodel.Direction;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class GameGUIController {
	private final GameGUIModel gameGUIModel;

	public GameGUIController(GameGUIModel gameGUIModel) {
		this.gameGUIModel = gameGUIModel;
	}

	public void addActions(BiConsumer<KeyStroke, Action> actionAdder) {
		actionAdder.accept(KeyStroke.getKeyStroke("UP"), new MoveAction(Direction.NORTH));
		actionAdder.accept(KeyStroke.getKeyStroke("DOWN"), new MoveAction(Direction.SOUTH));
		actionAdder.accept(KeyStroke.getKeyStroke("LEFT"), new MoveAction(Direction.WEST));
		actionAdder.accept(KeyStroke.getKeyStroke("RIGHT"), new MoveAction(Direction.EAST));
	}

	public void onBoardCellClick(Cell cell, MouseEvent e) {
		gameGUIModel.performGameAction(() -> {
			if (gameGUIModel.getGuiState() != GUIState.MOVING_OR_ROTATING_PIECE_SELECTION) {
				return;
			}
			Player currentPlayer = gameGUIModel.getCurrentPlayer();
			if (!currentPlayer.ownsPiece(cell)) {
				return;
			}
			if (!(cell instanceof PieceCell)) {
				return;
			}

			gameGUIModel.setMovementSelectedCell((PieceCell) cell);
		});
	}

	public void onCreationCellClick(Cell cell, MouseEvent e) {
		gameGUIModel.performGameAction(() -> {
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
			MouseEvent mouseEvent
	) {
		if (gameGUIModel.getGuiState() != GUIState.CREATE_PIECE_ROTATION) {
			return;
		}

		gameGUIModel.performGameAction(() -> {
			gameGUIModel.createPiece(rotatedCellCopy);
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
