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
	public final int strengthLoss;

	public Shoot(Robot shooter, Robot shootee, int strengthLoss) {
		this.shooter = shooter;
		this.shootee = shootee;
		this.strengthLoss = strengthLoss;
	}

	@Override
	public void apply(Battle b) {
		shootee.isShot(strengthLoss);
		b.log("Robot " + shooter.name + " shoots robot " + shootee.name
				+ ".  It's strength is now: " + shootee.getStrength());
	}
}
