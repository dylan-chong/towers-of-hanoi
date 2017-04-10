package robotwar.ui;

/**
 * The Clock Thread is responsible for producing a consistent "pulse" which is
 * used to update the game state, and refresh the display. Setting the pulse
 * rate too high may cause problems, when the point is reached at which the work
 * done to service a given pulse exceeds the time between pulses.
 * 
 * @author David J. Pearce
 * 
 */
public class ClockThread extends Thread {
	private final int delay; // delay between ticks in us	
	private final BattleFrame display;
	private boolean active;
	
	public ClockThread(int delay, BattleFrame display) {
		this.delay = delay;		
		this.display = display;
		this.active = false;
	}
	
	public void run() {
		while(1 == 1) {
			// Loop forever			
			try {
				Thread.sleep(delay);
				if(active) {
					display.clockTick();
				}
			} catch(InterruptedException e) {
				// should never happen
			}			
		}
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
}
