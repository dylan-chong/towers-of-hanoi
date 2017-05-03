package swen221.cardgame.cards.util;

import swen221.cardgame.cards.core.Card;
import swen221.cardgame.cards.core.Player;
import swen221.cardgame.cards.core.Trick;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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
		List<Card> cardsPlayed = trick.getCardsPlayed();
		Set<Card> playerCards = player.getHand().getCards();
		Set<Card> playerTrumpCards = player.getHand().matches(trick.getTrumps());
		Comparator<Card> cardComparator = new Card.RankFirstComparator();

		if (cardsPlayed.isEmpty()) {
			if (!playerTrumpCards.isEmpty()) {
				// Play best trump card if we have one
				return Collections.max(playerTrumpCards, cardComparator);
			} else {
				// otherwise return best card of any suit
				return Collections.max(playerCards, cardComparator);
			}
		}

		// Find what cards can be used to win
		Card.Suit leadSuit = trick.getCardsPlayed().get(0).suit();
		Set<Card> winnablePlayerCards = getWinnablePlayerCards(trick, leadSuit);

		if (winnablePlayerCards.isEmpty()) {
			// Can't win
			// Pick lowest card of correct suit if possible, otherwise any suit
			Set<Card> playerCorrectSuitCards = playerCards.stream()
					.filter(card -> card.suit() == leadSuit)
					.collect(Collectors.toSet());
			if (playerCorrectSuitCards.isEmpty())
				return Collections.min(playerCards, cardComparator);
			else
				return Collections.min(playerCorrectSuitCards, cardComparator);
		}

		if (cardsPlayed.size() < 3) {
			// Might win
			// Use best card
			return Collections.max(winnablePlayerCards, cardComparator);
		} else {
			// Can win (we play last move)
			// Choose smallest winning card
			return Collections.min(winnablePlayerCards, cardComparator);
		}
	}

	/**
	 * Assumes that there is at least one card played before
	 *
	 * @param leadSuit The suit of the lead card (this is only here to avoid
	 *                 coe duplication)
	 * @return The cards that could be used to win this {@link Trick}
	 */
	private Set<Card> getWinnablePlayerCards(Trick trick,
											 Card.Suit leadSuit) {
		List<Card> cardsPlayed = trick.getCardsPlayed();
		Set<Card> playerCards = player.getHand().getCards();
		Comparator<Card> cardComparator = new TrumpableComparator(trick.getTrumps());
		Predicate<Card> isValidSuit = card -> card.suit() == leadSuit ||
						card.suit() == trick.getTrumps();

		Card bestPlayedCard = cardsPlayed.stream()
				.filter(isValidSuit)
				// comparator ignores what the required suit is
				.max(cardComparator)
				.orElseThrow(AssertionError::new);
		return playerCards.stream()
				.filter(isValidSuit)
				.filter(playerCard ->
						cardComparator.compare(playerCard, bestPlayedCard) > 0
				)
				.collect(Collectors.toSet());
	}

	/**
	 * Compares cards that may or may not be a trump suit card. This ignores
	 * the required (lead) suit
	 */
	private class TrumpableComparator implements Comparator<Card> {
		private final Card.Suit trumpSuit;

		private TrumpableComparator(Card.Suit trumpSuit) {
			this.trumpSuit = trumpSuit;
		}

		@Override
		public int compare(Card c1, Card c2) {
			boolean isC1Trump = c1.suit() == trumpSuit;
			boolean isC2Trump = c2.suit() == trumpSuit;
			if (isC1Trump == isC2Trump) {
				// Both are trump, or neither are
				return new Card.RankFirstComparator().compare(c1, c2);
			}
			// Only one is trump
			if (isC1Trump) return 1;
			return -1;
		}
	}
}
