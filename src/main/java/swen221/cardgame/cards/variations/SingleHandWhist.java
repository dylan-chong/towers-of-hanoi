package swen221.cardgame.cards.variations;

import java.util.List;
import java.util.Set;

import swen221.cardgame.cards.core.Card;
import swen221.cardgame.cards.core.IllegalMove;
import swen221.cardgame.cards.core.Player;
import swen221.cardgame.cards.core.Trick;
import swen221.cardgame.cards.core.Player.Direction;
import swen221.cardgame.cards.util.AbstractCardGame;

/**
 * An implementation of the "classical" rules of Whist.
 * 
 * @author David J. Pearce
 *
 */
public class SingleHandWhist extends AbstractCardGame {

	public SingleHandWhist() {

	}

	public String getName() {
		return "Classic Whist";
	}
	
	public boolean isGameFinished() {
		for (Player.Direction d : Player.Direction.values()) {
			if (scores.get(d) == 1) {
				return true;
			}
		}
		return false;
	}
	
	public void deal(List<Card> deck) {	
		currentTrick = null;
		for (Player.Direction d : Player.Direction.values()) {
			players.get(d).getHand().clear();
		}
		Player.Direction d = Player.Direction.NORTH;
		for (int i = 0; i < deck.size(); ++i) {
			Card card = deck.get(i);
			players.get(d).getHand().add(card);
			d = d.next();
		}		
	}		
}
