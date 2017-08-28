package main.gui.game;

import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gui.game.GameGUIModel.GUIState;

import java.awt.event.MouseEvent;

public class GameGUIController {
	private final GameGUIModel gameGUIModel;

	public GameGUIController(GameGUIModel gameGUIModel) {
		this.gameGUIModel = gameGUIModel;
	}

	public void onBoardCellClick(Cell cell, MouseEvent e) {
		gameGUIModel.performGameAction(() -> {
			if (gameGUIModel.getGuiState() != GUIState.MOVING_OR_ROTATING_PIECE) {
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
		gameGUIModel.performGameAction(() -> {
			gameGUIModel.createPiece(rotatedCellCopy);
		});
	}
}
