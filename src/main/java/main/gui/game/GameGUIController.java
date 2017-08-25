package main.gui.game;

import main.gamemodel.IllegalGameStateException;
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
		System.out.println("ROT NOW" + cell.getId());
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
			throw new IllegalGameStateException("Somehow wrong cell was selected");
		}

		// TODO NEXT create rotation copies
		// TODO AFTER do create

		gameGUIModel.setCreationSelectedCell(null);
		System.out.println("CREATE!" + baseCell.getId());
	}

	private Player getCurrentPlayer() {
		return gameGUIModel.getGameModel().getCurrentPlayerData();
	}
}
