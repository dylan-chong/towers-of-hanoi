package main.gui.game.celldrawers.cellcanvas;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

class ComponentPosition {

	public static Set<Component> setFromSet(
			Set<ComponentPosition> componentPositions
	) {
		return componentPositions.stream()
				.map(componentPosition -> componentPosition.component)
				.collect(Collectors.toSet());
	}

	public final Component component;
	public final int row, col;

	ComponentPosition(Component component, int row, int col) {
		this.component = component;
		this.row = row;
		this.col = col;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ComponentPosition that = (ComponentPosition) o;

		if (row != that.row) return false;
		if (col != that.col) return false;
		return component != null ? component.equals(that.component) : that.component == null;
	}

	@Override
	public int hashCode() {
		int result = component != null ? component.hashCode() : 0;
		result = 31 * result + row;
		result = 31 * result + col;
		return result;
	}
}
