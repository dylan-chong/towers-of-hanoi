package swen221.monopoly;

import java.util.*;

import swen221.monopoly.locations.Location;
import swen221.monopoly.locations.Property;

/**
 * This class repesents a player in the game. In particular, it records what
 * properties are currently owned by that player, how much cash he/she has, what
 * token they are, etc.
 * 
 * @author David J. Pearce
 */
public class Player implements Iterable<Property> {
	public enum Token {
		ScottishTerrier,
		Battleship,
		Automobile,
		TopHat,
		Thimble,
		Boot,
		WheelBarrow,
		Iron
	}
	
	private ArrayList<Property> portfolio;
	private Location location;
	private int cash;
	private Token token;
	private String name;

	/**
	 * Construct a new player with a given name, token, opening balance and
	 * starting location.
	 * 
	 * @param name
	 * @param token
	 * @param cash
	 * @param location
	 */
	public Player(String name, Token token, int cash, Location location) {
		this.cash = cash;
		this.token = token;
		this.location = location;
		this.name = name;
		portfolio = new ArrayList<Property>();
	}

	public String getName() {
		return name;
	}

	/**
	 * Get current location of player.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get the player's current balance in NZ$.
	 * 
	 * @return
	 */
	public int getBalance() {
		return cash;
	}

	/**
	 * Get the token this player uses on the board.
	 * 
	 * @return
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Set current location of player.
	 */
	public void setLocation(Location l) {
		location = l;
	}
	
	/**
	 * Deduct amount in $ from player.
	 */
	public void deduct(int amount) {
		cash -= amount;
	}

	/**
	 * Credit amount in $ to player.
	 */
	public void credit(int amount) {
		cash += amount;
	}

	/**
	 * Buy property. This will deduct cash from players balance and add property
	 * to list of those owned by this player. If player has insufficient funds,
	 * balance will go negative. Assumes property not already owned by anyone!
	 */
	public void buy(Property p) {
		if(p.hasOwner()) {
			throw new IllegalArgumentException("cannot buy property!");
		}

		cash -= p.getPrice();
		portfolio.add(p);
		p.setOwner(this);
	}

	/**
	 * Sell property. This will credit cash from players balance and remove
	 * property from list of those owned by this player. Assumes property is
	 * owned by player!
	 */
	public void sell(Property p) {
		if(p.getOwner() != this) {
			throw new IllegalArgumentException("cannot sell property!");
		}

		cash += p.getPrice();
		portfolio.remove(p);
		p.setOwner(null);
	}

	/**
	 * Iterate properties owned by player.  
	 */
	public Iterator<Property> iterator() {
		return portfolio.iterator();
	}
}
