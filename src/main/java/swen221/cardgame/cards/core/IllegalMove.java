package swen221.cardgame.cards.core;

/**
 * Use to signal an illegal move has occurred. This typically happens when a
 * human play attempts to play out of turn and/or with a card that doesn't
 * follow suit, etc.
 * 
 * @author David J. Pearce
 * 
 */
public class IllegalMove extends Exception {
	public IllegalMove(String e) {
		super(e);
	}
}
