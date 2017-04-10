package robotwar;

import robotwar.core.Battle;
import robotwar.ui.BattleFrame;

import java.util.Random;

public class Main {
	private static Random random = new Random();
	
	/**
	 * Generate a random integer between 0 and n
	 */
	public static int randomInteger(int n) {
		return random.nextInt(n);
	}
		
	public static void main(String[] args) {	
		Battle battle = new Battle(16,16);		
		new BattleFrame(battle);
	}
}
