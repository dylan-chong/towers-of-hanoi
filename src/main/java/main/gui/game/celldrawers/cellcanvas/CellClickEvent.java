package main.gui.game.celldrawers.cellcanvas;

import main.gamemodel.Direction;
import main.gui.game.GameGUIController;

import java.awt.event.MouseEvent;

public class CellClickEvent {
	public final MouseEvent e;
	public final int row;
	public final int col;
	public final float rowOffset;
	public final float colOffset;

	public CellClickEvent(
			MouseEvent e,
			int row,
			int col,
			float rowOffset,
			float colOffset
	) {
		this.e = e;
		this.row = row;
		this.col = col;
		this.rowOffset = rowOffset;
		this.colOffset = colOffset;
	}

	public Direction getClickedEdge() {
		double distance = GameGUIController.MOVE_CLICK_EDGE_DISTANCE;
		if (rowOffset < distance) {
			return Direction.NORTH;
		} else if (rowOffset > 1 - distance) {
			return Direction.SOUTH;
		} else if (colOffset < distance) {
			return Direction.WEST;
		} else if (colOffset > 1 - distance) {
			return Direction.EAST;
		}

		return null;
	}
}
