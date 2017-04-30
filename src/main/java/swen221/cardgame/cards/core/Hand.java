package swen221.cardgame.cards.core;

import java.util.*;

/**
 * Represents a hand of cards held by a player. As the current round proceeds,
 * the number of cards in the hand will decrease. When the round is over, new
 * cards will be delt and added to this hand.
 * 
 * @author David J. Pearce
 * 
 */
public class Hand implements Cloneable, Iterable<Card> {
	private SortedSet<Card> cards = new TreeSet<Card>();
	

	public Iterator<Card> iterator() {
		return cards.iterator();
	}
	
	/**
	 * Check with a given card is contained in this hand, or not.
	 * 
	 * @param card
	 * @return
	 */
	public boolean contains(Card card) {
		return cards.contains(card);
	}
	
	/**
	 * Return all cards in this hand which match the given suit.
	 * @param suit
	 * @return
	 */
	public Set<Card> matches(Card.Suit suit) {
		HashSet<Card> r = new HashSet<Card>();
		for(Card c : cards) {
			if(c.suit() == suit) {
				r.add(c);
			}
		}
		return r;
	}
	
	
	/**
	 * Add a card to the hand.
	 */
	public void add(Card card) {		
		cards.add(card);
	}
	
	/**
	 * Remove a card from the hand.
	 */
	public void remove(Card card) {		
		cards.remove(card);
	}
	
	/**
	 * Get number of cards in this hand.
	 * 
	 * @return
	 */
	public int size() {
		return cards.size();
	}
	
	/**
	 * Remove all cards from this hand.
	 */
	public void clear() {
		cards.clear();
	}	
}
