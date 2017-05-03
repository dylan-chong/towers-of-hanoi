package swen221.cardgame.cards.core;

import java.util.Comparator;

public class Card implements Comparable<Card>, Cloneable {
	
	/**
	 * Represents a card suit.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public enum Suit {
		HEARTS,
		CLUBS,
		DIAMONDS,
		SPADES;
	}
	
	/**
	 * Represents the different card "numbers".
	 * 
	 * @author David J. Pearce
	 *
	 */
	public enum Rank {
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		JACK,
		QUEEN,
		KING,
		ACE;
	}

	// =======================================================
	// Card stuff
	// =======================================================
	
	private Suit suit; // HEARTS, CLUBS, DIAMONDS, SPADES
	private Rank rank; // 2 <= number <= 14 (ACE)
	
	/**
	 * Construct a card in the given suit, with a given number
	 * 
	 * @param suit
	 *            --- between 0 (HEARTS) and 3 (SPADES)
	 * @param number
	 *            --- between 2 and 14 (ACE)
	 */
	public Card(Suit suit, Rank number) {				
		this.suit = suit;
		this.rank = number;
	}

	/**
	 * Get the suit of this card, between 0 (HEARTS) and 3 (SPADES).
	 * 
	 * @return
	 */
	public Suit suit() {
		return suit;
	}

	/**
	 * Get the number of this card, between 2 and 14 (ACE).
	 * 
	 * @return
	 */
	public Rank rank() {
		return rank;
	}	
		
	private static String[] suits = { "Hearts","Clubs","Diamonds","Spades"};
	private static String[] ranks = { "2 of ", "3 of ", "4 of ",
			"5 of ", "6 of ", "7 of ", "8 of ", "9 of ", "10 of ", "Jack of ",
			"Queen of ", "King of ", "Ace of " };
	
	public String toString() {
		return ranks[rank.ordinal()] + suits[suit.ordinal()];		
	}

	@Override
	public int compareTo(Card o) {
		return new SuitFirstComparator().compare(this, o);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Card card = (Card) o;

		if (suit != card.suit) return false;
		return rank == card.rank;
	}

	@Override
	public int hashCode() {
		int result = suit != null ? suit.hashCode() : 0;
		result = 31 * result + (rank != null ? rank.hashCode() : 0);
		return result;
	}

	@Override
	public Card clone() {
		try {
			return (Card) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Prioritises suit
	 */
	public static class SuitFirstComparator implements Comparator<Card> {
		@Override
		public int compare(Card o1, Card o2) {
			if (o1.suit.ordinal() < o2.suit.ordinal()) return -1;
			if (o1.suit.ordinal() > o2.suit.ordinal()) return 1;

			if (o1.rank.ordinal() < o2.rank.ordinal()) return -1;
			if (o1.rank.ordinal() > o2.rank.ordinal()) return 1;

			return 0;
		}
	}

	/**
	 * Prioritises rank
	 */
	public static class RankFirstComparator implements Comparator<Card> {
		@Override
		public int compare(Card o1, Card o2) {
			if (o1.rank.ordinal() < o2.rank.ordinal()) return -1;
			if (o1.rank.ordinal() > o2.rank.ordinal()) return 1;

			if (o1.suit.ordinal() < o2.suit.ordinal()) return -1;
			if (o1.suit.ordinal() > o2.suit.ordinal()) return 1;

			return 0;
		}
	}
}
