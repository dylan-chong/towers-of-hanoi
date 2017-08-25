package main.gui.game;

import main.gamemodel.Direction;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;

import java.awt.event.MouseEvent;

import static main.gui.game.GameGUIModel.GUIState;

public class GameGUIController {
	private final GameGUIModel gameGUIModel;

	public GameGUIController(GameGUIModel gameGUIModel) {
		this.gameGUIModel = gameGUIModel;
	}

	public void onBoardCellClick(Cell cell, MouseEvent e) {
	}

	public void onCreationCellClick(Cell cell, MouseEvent e) {
		if (gameGUIModel.getGuiState() != GUIState.CREATE_PIECE_CREATION) {
			return;
		}

		Player currentPlayer = getCurrentPlayer();
		if (!currentPlayer.ownsPiece(cell)) {
			return;
		}

		gameGUIModel.setCreationSelectedCell((PieceCell) cell);
		gameGUIModel.setGuiState(GUIState.CREATE_PIECE_ROTATION);
	}

	public void onCreationRotationCellClick(
			Cell rotatedCellCopy,
			MouseEvent mouseEvent
	) {
		if (gameGUIModel.getGuiState() != GUIState.CREATE_PIECE_ROTATION) {
			return;
		}

		PieceCell rotatedPieceCopy = (PieceCell) rotatedCellCopy;
		PieceCell baseCell = gameGUIModel.getCreationSelectedCell();

		if (rotatedPieceCopy.getSideCombination() != baseCell.getSideCombination()) {
			throw new IllegalArgumentException("Somehow wrong cell was selected");
		}

		Direction direction = rotatedPieceCopy.getDirection();

		gameGUIModel.performGameAction(() ->
			gameGUIModel
					.getGameModel()
					.create(baseCell.getId(), direction.ordinal())
		);

		// TODO AFTER draw special board stuff
	}

	private Player getCurrentPlayer() {
		return gameGUIModel.getGameModel().getCurrentPlayerData();
	}
}
