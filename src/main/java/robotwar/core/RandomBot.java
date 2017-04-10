package robotwar.core;

import robotwar.Main;
import robotwar.ui.GameImages;

import java.awt.*;

/**
 * The RandomBot just moves around randomly within the arena and fires at
 * whatever it can see.
 * 
 * @author David J. Pearce
 * 
 */
public class RandomBot extends Robot {
	public RandomBot(String name, int xPosition, int yPosition, int strength) {
		super(name,xPosition,yPosition,strength);
	}
	
	/**
	 * Move the robot in a random direction. If another robot is sighted then it
	 * is attacked immediately.
	 */
	@Override
	public Point getNewPositionForTurn(Battle battle) {
		// Now, make a random move
		int dx = Main.randomInteger(3) - 1;
		int dy = Main.randomInteger(3) - 1;
		int newXPos = getxPosition() + dx;
		int newYPos = getyPosition() + dy;
		return new Point(newXPos, newYPos);
	}

	@Override
	protected GameImages getAliveImage() {
		return GameImages.RANDOM_BOT;
	}
}
