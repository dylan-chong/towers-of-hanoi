package robotwar.core;

import java.util.*;

import robotwar.Main;

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
	public void takeTurn(Battle battle) {		
		// First, look to see if there is anything to fire at.
		LinkedList<Robot> robotsInSight = findRobotsInSight(battle, 10);
		
		if(!robotsInSight.isEmpty()) {
			// shoot a robot then!
			Robot target = robotsInSight.get(0);
			battle.actions.add(new Shoot(this,target,0));
			target.strength = target.strength - 1;
			if(this.strength < 0) {
				isDead = true;
			}			
		} 
		// Now, make a random move
		int dx = Main.randomInteger(3) - 1;
		int dy = Main.randomInteger(3) - 1;
		int newXPos = xPosition + dx;
		int newYPos = yPosition + dy;

		// Try to move, whilst watching out for the arena wall!
		if(newXPos >= 0 && newXPos < battle.arenaWidth) {
			if(newYPos >= 0 && newYPos < battle.arenaHeight) {
				battle.log("Robot " + name + " moves to " + xPosition + ", " + yPosition);
			} else {
				battle.log("Robot " + name + " bumps into arena wall!");
			}
		} else {
			battle.log("Robot " + name + " bumps into arena wall!");
		}		
		
		battle.actions.add(new Move(newXPos,newYPos,this));
	}
}
