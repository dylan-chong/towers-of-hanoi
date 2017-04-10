package robotwar.core;

import robotwar.ui.GameImages;

import java.awt.*;

/**
 * The GuardBot marks out its starting location and hangs around there
 * protecting it.
 *
 * @author David J. Pearce
 *
 */
public class GuardBot extends Robot {
	// protected final int guardArea;
	protected final int startXPosition;
	protected final int startYPosition;

	/**
	 * Construct a gaurd bot with a given name, starting position, strength.
	 *
	 * @param name
	 * @param xPosition
	 * @param yPosition
	 * @param strength
	 */
	public GuardBot(String name,  int xPosition, int yPosition, int strength) {
		super(name, xPosition,yPosition,strength);
		startXPosition = xPosition;
		startYPosition = yPosition;
	}

	/**
	 * Move the robot according to a simple strategy which sees the robot
	 * circling (and protecting) its originating position. If another robot is
	 * sighted then it is attacked immediately.
	 */
	@Override
	public Point getNewPositionForTurn(Battle battle) {
		int radius = 5;
		int dy = getyPosition() - startYPosition;
		int newXPosition = getxPosition();
		int newYPosition = getyPosition();

		// This implements a simple alternating walk pattern.
		if (getxPosition() < startXPosition) {
			if(dy < radius && getyPosition() < battle.arenaHeight) {
				newYPosition = getyPosition() + 1;
			} else {
				newXPosition = getxPosition() + 1;
			}
		} else {
			if(dy > -radius && getyPosition() >= 0) {
				newYPosition = getyPosition() - 1;
			} else {
				newXPosition = getxPosition() - 1;
			}
		}
		return new Point(newXPosition, newYPosition);
	}

	@Override
	protected GameImages getAliveImage() {
		return GameImages.GUARD_BOT;
	}
}
