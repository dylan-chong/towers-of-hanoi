package swen221.monopoly.locations;

/**
 * Represents any location on the monopoly board. This includes streets,
 * utilities, special areas and stations.
 */
public abstract class Location {
	private String name;

	/**
	 * Construct a location with a given name. Every location on the Monopoly
	 * board has a unique name which identifies it.
	 * 
	 * @param name
	 */
	public Location(String name) {
		this.name = name;
	}

	/**
	 * Get the name of this location on the board.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Check whether this location has an owner or not. Note that not all
	 * locations can actually have owners.
	 */
	public abstract boolean hasOwner();
}
