package robotwar.core;

import java.util.*;

/**
 * An abstract class representing a Robot in the battle. Subclasses of this can
 * implement different strategies for moving and fighting within the arena.
 * 
 * @author David J. Pearce
 * 
 */
public abstract class Robot {
	public String name;
	public int xPosition;	
	public int yPosition;
	public int strength;
	public boolean isDead;
	
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
		this.isDead = false;
	}
	
	/**
	 * This method is called to allow a robot to do something.
	 * 
	 * @param battle
	 */
	public abstract void takeTurn(Battle battle);	
	/**
	 * This method is called when a robot is shot by another robot.
	 */
	public void isShot(int strength) {
		this.strength = this.strength - 1;
		// should check isDead here?
	}
	
	/**
	 * This helper method determines what robots are in sight of this robot.
	 * Robots which are in sight can be attacked.
	 * 
	 * @param distance
	 *            the distance that this robot can see.
	 * @return list of robots in sight
	 */
	protected LinkedList<Robot> findRobotsInSight(Battle battle,
			int distance) {
		
		LinkedList<Robot> robots = battle.robots;
		LinkedList<Robot> visibleRobots = new LinkedList<Robot>();
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
}
