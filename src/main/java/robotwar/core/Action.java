package robotwar.core;

/**
 * Represents an action that a robot can take in the game
 * 
 * @author David J. Pearce
 * 
 */
public interface Action {
	
	/**
	 * Apply this action.
	 */
	public void apply(Battle b);
}
