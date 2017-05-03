package swen221.cardgame.cards.util;

import swen221.cardgame.cards.core.Card;
import swen221.cardgame.cards.core.Player;
import swen221.cardgame.cards.core.Trick;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements a simple computer player who plays the highest card available when
 * the trick can still be won, otherwise discards the lowest card available. In
 * the special case that the player must win the trick (i.e. this is the last
 * card in the trick), then the player conservatively plays the least card
 * needed to win.
 * 
 * @author David J. Pearce
 * 
 */
public class SimpleComputerPlayer extends AbstractComputerPlayer {

	public SimpleComputerPlayer(Player player) {
		super(player);
	}

	@Override
	public Card getNextCard(Trick trick) {
		List<Card> playedCards = trick.getCardsPlayed();
		Set<Card> cards = player.getHand().getCards();
		Set<Card> trumpCards = player.getHand().matches(trick.getTrumps());
		Comparator<Card> rankFirstComparator = new Card.RankFirstComparator();
		boolean isLastTurn = playedCards.size() == 3;

		if (playedCards.isEmpty()) {
			if (!trumpCards.isEmpty()) {
				// Play best trump card if we have one
				return Collections.max(trumpCards, rankFirstComparator);
			} else {
				// otherwise return best card of any suit
				return Collections.max(cards, rankFirstComparator);
			}
		}

		// Find best cards
		Card.Suit leadSuit = trick.getCardsPlayed().get(0).suit();
		Card bestPlayedLeadSuitCard = getBestCardOfSuit(playedCards, leadSuit);
		assert bestPlayedLeadSuitCard != null;
		Card bestPlayedTrumpCard = getBestCardOfSuit(playedCards, trick.getTrumps());
		// bestPlayedTrumpCard is null when no trumps have been played

		// Group player cards
		Set<Card> leadSuitCards = player.getHand().matches(leadSuit);
		Set<Card> winnableLeadSuitCards = leadSuitCards.stream()
				.filter(card -> card.compareTo(bestPlayedLeadSuitCard) > 0)
				.collect(Collectors.toSet());

		// We must play a card of correct suit if possible
		if (!leadSuitCards.isEmpty()) {
			boolean canWin;
			if (leadSuit == trick.getTrumps()) {
				// Lead suit is same as trump suit
				canWin = !winnableLeadSuitCards.isEmpty();
			} else {
				// We can't beat a trump card because we have to play a lead suit card
				canWin = !winnableLeadSuitCards.isEmpty() && bestPlayedTrumpCard == null;
			}
			if (!canWin) return Collections.min(leadSuitCards);

			if (isLastTurn) {
				// We know we can win. Choose worst winning card
				return Collections.min(winnableLeadSuitCards);
			} else {
				// We might or might not win. Play best card
				return Collections.max(winnableLeadSuitCards);
			}
		}

		// We don't have a leading suit card, we should play a trump card to win
		// if possible

		Set<Card> winnableTrumpCards;
		if (bestPlayedTrumpCard == null) {
			// All our trump cards can win (no trumps played yet)
			winnableTrumpCards = trumpCards;
		} else {
			winnableTrumpCards = trumpCards.stream()
					.filter(card -> card.compareTo(bestPlayedTrumpCard) > 0)
					.collect(Collectors.toSet());
		}

		// Play a trump card if we have one that can/will win
		if (!trumpCards.isEmpty()) {
			boolean canWin = !winnableTrumpCards.isEmpty();
			if (canWin) {
				if (isLastTurn) {
					// We know we can win. Choose worst winning card
					return Collections.min(winnableTrumpCards);
				} else {
					// We might win if we play our best card.
					return Collections.max(winnableTrumpCards);
				}
			}
		}

		// We can't win, and don't have lead suit card.
		// Play the smallest card of any suit.
		return Collections.min(
				player.getHand().getCards(),
				rankFirstComparator
		);
	}

	private Card getBestCardOfSuit(Collection<Card> cards,
								   Card.Suit suit) {
		return cards.stream()
				.filter(card -> card.suit() == suit)
				.max(new Card.RankFirstComparator())
				.orElse(null);
	}

}
