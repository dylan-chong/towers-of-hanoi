package swen221.lab3.model;

import java.awt.*;

/**
 * Created by Dylan on 28/03/17.
 */
public class Book extends PickupableItem {

	private static final String DESCRIPTION =
			"A book entitled \"%s\""; // %s for title
	private static final String READ_DESCRIPTION_SUFFIX =
			"; it looks like it has been read";

	private final String title;

	private boolean hasBeenRead = false;
	private static final int BORDER = 5;

	public Book(String title) {
		super();
		this.title = title;
	}

	@Override
	public String getDescription() {
		String description = String.format(DESCRIPTION, title);
		if (hasBeenRead) description += READ_DESCRIPTION_SUFFIX;
		return description;
	}

	@Override
	public String[] getActions() {
		return new String[]{
				"Pickup", "Drop", "Read"
		};
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.ORANGE);
		g.fillRect(
				BORDER,
				BORDER,
				g.getClipBounds().width - BORDER * 2,
				g.getClipBounds().width - BORDER * 2
		);
	}

	@Override
	public boolean performAction(String action, Player player) {
		switch (action) {
			case "Read": {
				if (!player.getInventory().contains(this)) {
					return false;
				}
				hasBeenRead = true;
				return true;
			}
		}

		return super.performAction(action, player);
	}
}
