package main;

import main.gamemodel.cells.Cell;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameUtils {
	public static List<List<Cell>> packCells(
			List<? extends Cell> cells,
			int columns
	) {
		LinkedList<List<Cell>> packedCells = new LinkedList<>();
		for (Cell cell : cells) {
			List<Cell> lastPackList;

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
