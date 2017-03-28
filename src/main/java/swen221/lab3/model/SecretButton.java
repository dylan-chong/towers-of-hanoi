package swen221.lab3.model;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 28/03/17.
 */
public class SecretButton implements Item {
	private final int code;

	public SecretButton(int code) {
		this.code = code;
	}


	@Override
	public String getDescription() {
		return "A secret button";
	}

	@Override
	public String[] getActions() {
		return new String[]{"Press"};
	}

	@Override
	public boolean performAction(String action, Player player) {
		if (!action.equals("Press")) {
			throw new IllegalArgumentException("Illegal action: " + action);
		}

		List<LockableDoor> unlockableDoors = Arrays.stream(player.getLocation()
				.getItems())
				.filter(item -> item instanceof LockableDoor)
				.map(item -> (LockableDoor) item)
				.filter(door -> door.getCode() == code)
				.collect(Collectors.toList());
		if (unlockableDoors.isEmpty()) return false;
		unlockableDoors.forEach(door -> door.setLocked(false));
		return true;
	}

	@Override
	public void draw(Graphics g) {

	}
}
