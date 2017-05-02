package swen221.cardgame.cards.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a trick being played. This includes the cards that have been
 * played so far, as well as what the suit of trumps is for this trick.
 * 
 * @author David J. Pearce
 * 
 */
public class Trick implements Cloneable {
	private Card[] cards = new Card[4];
	private Player.Direction lead;
	private Card.Suit trumps;
	
	/**
	 * Contruct a new trick with a given lead player and suit of trumps.
	 * 
	 * @param lead
	 *            --- lead player for this trick.
	 * @param trumps
	 *            --- maybe null if no trumps.
	 */
	public Trick(Player.Direction lead, Card.Suit trumps) {
		this.lead = lead;
		this.trumps = trumps;
	}
	
	/**
	 * Determine who the lead player for this trick is.
	 * 
	 * @return
	 */
	public Player.Direction getLeadPlayer() {
		return lead;
	}
	
	/**
	 * Determine which suit are trumps for this trick, or null if there are no
	 * trumps.
	 * 
	 * @return
	 */
	public Card.Suit getTrumps() {
		return trumps;
	}
	
	/**
	 * Get the list of cards played so far in the order they were played.
	 * 
	 * @return
	 */
	public List<Card> getCardsPlayed() {
		ArrayList<Card> cs = new ArrayList<Card>();
		for(int i=0;i!=4;++i) {
			if(cards[i] != null) {
				cs.add(cards[i]);
			} else {
				break;
			}
		}
		return cs;
	}
	
	/**
	 * Get the card played by a given player, or null if that player has yet to
	 * play.
	 * 
	 * @param p --- player
	 * @return
	 */
	public Card getCardPlayed(Player.Direction p) {		
		Player.Direction player = lead;
		for(int i=0;i!=4;++i) {
			if(player.equals(p)) {
				return cards[i];
			}
			player = player.next();
		}
		// deadcode
		return null;
	}
	
	/**
	 * Determine the next player to play in this trick.
	 * 
	 * @return
	 */
	public Player.Direction getNextToPlay() {
		Player.Direction dir = lead;
		for(int i=0;i!=4;++i) {
			if(cards[i] == null) {
				return dir;
			}
			dir = dir.next();			
		}
		return null;
	}
	
	/**
	 * Determine the winning player for this trick. This requires looking to see
	 * which player led the highest card that followed suit; or, was a trump.
	 * 
	 * @return
	 */
	public Player.Direction getWinner() {
		Player.Direction player = lead;
		Player.Direction winningPlayer = null;
		Card winningCard = cards[0];
		for (int i = 0; i != 4; ++i) {
			if (cards[i].suit() == winningCard.suit()
					&& cards[i].compareTo(winningCard) >= 0) {
				winningPlayer = player;
				winningCard = cards[i];
			} else if (trumps != null && cards[i].suit() == trumps
					&& winningCard.suit() != trumps) {
				// in this case, the winning card is a trump
				winningPlayer = player;
				winningCard = cards[i];
			}
			player = player.next();
		}
		return winningPlayer;
	}
	
	/**
	 * Player attempts to play a card. This method checks that the given player
	 * is entitled to play, and that the played card follows suit. If either of
	 * these are not true, it throws an IllegalMove exception.
	 */
	public void play(Player p, Card c) throws IllegalMove {
		int nextCardIndex;
		for (nextCardIndex = 0; true; ++nextCardIndex) {
			if (nextCardIndex >= 4) throw new IllegalMove("All slots filled");
			if (cards[nextCardIndex] == null) {
				break;
			}
		}

		if (Arrays.stream(cards).anyMatch(c::equals))
			throw new IllegalMove("Card already played");

		if (!getNextToPlay().equals(p.direction))
			throw new IllegalMove("Not your turn");

		if (!p.getHand().contains(c))
			throw new IllegalMove("Card is not in your hand");

		if (nextCardIndex > 0) {
			Card firstCard = cards[0];
			boolean canPlayFirstSuit = p.getHand()
					.matches(firstCard.suit())
					.size() > 0;
			if (canPlayFirstSuit) {
				if (!c.suit().equals(firstCard.suit()))
					throw new IllegalMove("You must play a card with the same" +
							"suit as the first card in the trick, if you have" +
							"such a card");
			}
		}

		// Finally, play the card.
		cards[nextCardIndex] = c;
		p.getHand().remove(c);
	}

	@Override
	public Trick clone() {
		try {
			Trick clone = (Trick) super.clone();
			clone.cards = cards.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
