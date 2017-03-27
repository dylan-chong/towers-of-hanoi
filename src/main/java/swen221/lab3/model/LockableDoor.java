package swen221.lab3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by Dylan on 28/03/17.
 */
public class LockableDoor extends Door {
	private final int code;
	private boolean isLocked = true;

	public LockableDoor(Room oneSide, Room otherSide, int code) {
		super(oneSide, otherSide);
		this.code = code;
	}

	@Override
	public String[] getActions() {
		List<String> actions = new ArrayList<>(Arrays.asList(super.getActions()));
		actions.addAll(Arrays.asList(
				"Lock", "Unlock"
		));
		return actions.toArray(new String[actions.size()]);
	}

	@Override
	public boolean performAction(String action, Player player) {
		if (action.toLowerCase().endsWith("lock")) {
			Key key = player.getInventory()
					.stream()
					.filter(item -> item instanceof Key)
					.map(item -> (Key) item)
					.filter(k -> k.getCode() == code)
					.findAny()
					.orElse(null);
			if (key == null) {
				return false;
			}
			isLocked = action.equals("Lock");
			return true;
		}
		if (action.equals("Enter")) {
			if (isLocked) return false;
		}
		return super.performAction(action, player);
	}
}
