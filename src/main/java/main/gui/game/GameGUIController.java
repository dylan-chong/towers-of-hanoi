package main.gui.game;

import main.gamemodel.GameModel;
import main.gamemodel.cells.Cell;

import java.awt.event.MouseEvent;

public class GameGUIController {
	public GameGUIController(GameModel gameModel) {

	}

	public void onBoardCellClick(Cell cell, MouseEvent e) {
		System.out.println(cell.getId());
	}

	public void onCreationCellClick(Cell cell, MouseEvent e) {
		System.out.println(cell.getId());
	}
}
