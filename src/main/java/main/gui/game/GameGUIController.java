package main.gui.game;

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

		Player currentPlayer = gameGUIModel.getCurrentPlayer();
		if (!currentPlayer.ownsPiece(cell)) {
			return;
		}

		gameGUIModel.setCreationSelectedCell((PieceCell) cell);
	}

	public void onCreationRotationCellClick(
			Cell rotatedCellCopy,
			MouseEvent mouseEvent
	) {
		gameGUIModel.createPiece(rotatedCellCopy);





		// TODO NEXT draw special board stuff





	}
}
