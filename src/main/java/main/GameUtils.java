package main;

import main.gamemodel.cells.BoardCell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameUtils {
	public static List<List<BoardCell>> packCells(
			List<? extends BoardCell> cells,
			int columns
	) {
		LinkedList<List<BoardCell>> packedCells = new LinkedList<>();
		for (BoardCell cell : cells) {
			List<BoardCell> lastPackList;

			if (packedCells.isEmpty()) {
				lastPackList = new ArrayList<>();
				packedCells.add(lastPackList);
			} else {
				lastPackList = packedCells.getLast();
				if (lastPackList.size() == columns) {
					lastPackList = new ArrayList<>();
					packedCells.add(lastPackList);
				}
			}

			lastPackList.add(cell);
		}
		return packedCells;
	}

}
