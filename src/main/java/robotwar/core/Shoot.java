package robotwar.core;

/**
 * Represents the act of one robot shooting another.
 * 
 * @author David J. Pearce
 * 
 */
public class Shoot implements Action {
	public final Robot shooter;
	public final Robot shootee;
	public final int strength;

	public Shoot(Robot shooter, Robot shootee, int strength) {
		this.shooter = shooter;
		this.shootee = shootee;
		this.strength = strength;
	}

	@Override
	public void apply(Battle b) {
		shootee.isShot(strength);
		b.log("Robot " + shooter.name + " shoots robot " + shootee.name
				+ ".  It's strength is now: " + shootee.strength);
	}
}
