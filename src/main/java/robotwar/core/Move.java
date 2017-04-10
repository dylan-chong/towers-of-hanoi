package robotwar.core;

/**
 * Represents a move made by a Robot in the game.
 * 
 * @author David J. Pearce
 *
 */
public class Move implements Action {
	/**
	 * Destination x position for robot
	 */
	public int xDestination;
	
	/**
	 * Destination y position for robot
	 */
	public int yDestination;
	
	/**
	 * Original x position for robot
	 */
	public int xOriginal;
	
	/**
	 * Original y position for robot
	 */
	public int yOriginal;
	
	/**
	 * Robot doing the move
	 */
	public Robot robot;
			
	public Move(int xDest, int yDest, Robot robot) {
		this.robot = robot;
		this.xDestination = xDest;
		this.yDestination = yDest;
		this.xOriginal = robot.getxPosition();
		this.yOriginal = robot.getyPosition();
	}
	
	public void apply(Battle b) {
		// Update the location of the robot
		robot.setxPosition(xDestination);
		robot.setyPosition(yDestination);
		b.log("Robot " + robot.name + " moves to " + xDestination + ", " + yDestination);
	}
}
