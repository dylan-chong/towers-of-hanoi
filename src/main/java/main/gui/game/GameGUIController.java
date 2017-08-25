package main.gui.game;

import main.gamemodel.Player;
import main.gamemodel.cells.Cell;

import java.awt.event.MouseEvent;

import static main.gui.game.GameGUIModel.*;

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
		//TODO next
	}

	public void onCreationRotationCellClick(Cell cell, MouseEvent mouseEvent) {
		System.out.println(cell.getId());
	}

	private Player getCurrentPlayer() {
		return gameGUIModel.getGameModel().getCurrentPlayerData();
	}
}
