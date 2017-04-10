package robotwar.core;

import robotwar.ui.GameImages;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * An abstract class representing a Robot in the battle. Subclasses of this can
 * implement different strategies for moving and fighting within the arena.
 *
 * @author David J. Pearce
 *
 */
public abstract class Robot {
	public final String name;
	private int xPosition;
	private int yPosition;
	private int strength;
	private boolean isDead;

	/**
	 * Construct a robot at a given x and y position with a given strength.
	 * 
	 * @param xPosition starting x position of robot
	 * @param yPosition starting y position of robot
	 * @param strength strength of robot
	 */
	public Robot(String name, int xPosition, int yPosition, int strength) {
		this.name = name;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.strength = strength;
		this.isDead = checkIsDead(strength);
	}
	
	/**
	 * This method is called to allow a robot to do something.
	 * 
	 * @param battle
	 */
	public void takeTurn(Battle battle) {
		// First, look to see if there is anything to fire at.
		List<Robot> robotsInSight = findRobotsInSight(battle, 10);

		if(!robotsInSight.isEmpty()) {
			// shoot a robot then!
			Robot target = robotsInSight.get(0);
			battle.actions.add(new Shoot(this,target,1));
		}

		Point newPosition = getNewPositionForTurn(battle);
		newPosition.x = Math.max(newPosition.x, 0);
		newPosition.x = Math.min(newPosition.x, battle.arenaWidth - 1);
		newPosition.y = Math.max(newPosition.y, 0);
		newPosition.y = Math.min(newPosition.y, battle.arenaHeight - 1);
		battle.log("Robot " + name + " moves to " +
				newPosition.x + ", " + newPosition.y);
		battle.actions.add(new Move(newPosition.x,newPosition.y,this));
	}

	public abstract Point getNewPositionForTurn(Battle battle);

	/**
	 * This method is called when a robot is shot by another robot.
	 */
	public void isShot(int strengthLoss) {
		if (isDead) return;
		this.strength = this.strength - strengthLoss;

		isDead = checkIsDead(this.strength);
	}
	
	/**
	 * This helper method determines what robots are in sight of this robot.
	 * Robots which are in sight can be attacked.
	 * 
	 * @param distance
	 *            the distance that this robot can see.
	 * @return list of robots in sight
	 */
	protected List<Robot> findRobotsInSight(Battle battle,
			int distance) {
		
		List<Robot> robots = battle.robots;
		List<Robot> visibleRobots = new ArrayList<>();
		for(Robot r : robots) {
			if(r != this && !r.isDead) {
				int dx = xPosition - r.xPosition;
				int dy = yPosition - r.yPosition;
				// Calculate distance from me to robot r using pythagorus
				// theorem.
				double distanceToR = Math.sqrt(dx*dx + dy*dy);
				if(((double)distance) > distanceToR) {
					// this robot is in range of sight
					visibleRobots.add(r);
				}
			}
		}

		return visibleRobots;
	}

	public GameImages getGameImage() {
		if (isDead) return GameImages.DEAD_BOT;
		return getAliveImage();
	}

	/**
	 * @return The GameImages instance that should be used when the image is alive
	 */
	protected abstract GameImages getAliveImage();

	private static boolean checkIsDead(int strength) {
		return strength <= 0;
	}


	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean dead) {
		isDead = dead;
	}
}
