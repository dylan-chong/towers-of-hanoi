package main;

import main.gamemodel.cells.Cell;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Various stuff
 */
public class Utils {

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

	public static JSplitPane compositeSplit(List<? extends JComponent> components) {
		if (components.size() == 1) {
			JComponent c = components.get(0);
			if (!(c instanceof JSplitPane)) {
				throw new IllegalArgumentException("Illegal input size");
			}
			return (JSplitPane) c;
		}

		List<JComponent> componentsCopy = new ArrayList<>(components);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(componentsCopy.remove(0));
		splitPane.setRightComponent(componentsCopy.remove(0));

		splitPane.setBorder(null);

		componentsCopy.add(0, splitPane);

		assert componentsCopy.size() == components.size() - 1;

		return compositeSplit(componentsCopy);

	}

}
