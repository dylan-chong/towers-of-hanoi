package main.gui.game;

import main.gamemodel.GameModel;
import main.gamemodel.cells.BoardCell;

import java.awt.event.MouseEvent;

public class GameGUIController {
	public GameGUIController(GameModel gameModel) {

	}

	public void onBoardCellClick(BoardCell cell, MouseEvent e) {
		System.out.println(cell.getId());
	}

	public void onCreationCellClick(BoardCell cell, MouseEvent e) {
		System.out.println(cell.getId());
	}
}
