package swen221.cardgame.cards.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Whist-like card game.
 * 
 * @author David J. Pearce
 *
 */
public interface CardGame extends Cloneable {

	/**
	 * Get the name of the game being played (e.g. classic Whist).
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the player sitting at the given direction.
	 * 
	 * @param direction
	 * @return
	 */
	public Player getPlayer(Player.Direction direction);
	
	/**
	 * Return the current trick being played, or null if the game is finished.
	 * 
	 * @return
	 */
	public Trick getTrick();
	
	/**
	 * Check whether the game is finished or not.
	 * 
	 * @return
	 */
	public boolean isGameFinished();
	
	/**
	 * Check whether the hand is finished or not.
	 * 
	 * @return
	 */
	public boolean isHandFinished();
	
	
	/**
	 * Return the winner(s) of this game. Note that there can be multiple
	 * winners in games where players form partnerships, or where there is a
	 * draw.
	 * 
	 * @return
	 */
	public Set<Player.Direction> getWinnersOfGame();
	
	/**
	 * Return the number of tricks each player has won in the current hand.
	 * 
	 * @return
	 */
	public Map<Player.Direction,Integer> getTricksWon();

	/**
	 * Return the overall score in the game as a whole.
	 * 
	 * @return
	 */
	public Map<Player.Direction,Integer> getOverallScores();
	
	/**
	 * The given player plays the given card.
	 * 
	 * @param player
	 * @param card
	 */
	public void play(Player.Direction player, Card card) throws IllegalMove;
	
	/**
	 * Start a new hand by dealing out the given deck.
	 */
	public void deal(List<Card> deck);

	/**
	 * Signal that the current hand is over. This is a useful opportunity to
	 * update player scores, etc.
	 */
	public void endHand();
	
	/**
	 * Signal that the current round is over. This is a useful opportunity to
	 * update player trick counts, etc.
	 */
	public void endRound();
	
	/**
	 * Start the current trick
	 */
	public void startRound();
	
	/**
	 * Clone this game
	 * @return
	 */
	public CardGame clone();
		
}
