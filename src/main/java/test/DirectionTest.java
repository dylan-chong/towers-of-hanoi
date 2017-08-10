package test;

import main.gamemodel.Direction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DirectionTest {
	@Test
	public void fromAToB_aAboveB_returnsSouth() {
		Direction direction = Direction.fromAToB(new int[]{2, 2}, new int[]{3, 2});
		assertEquals(Direction.SOUTH, direction);
	}
}
