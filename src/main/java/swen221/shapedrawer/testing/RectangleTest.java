package swen221.shapedrawer.testing;

import org.junit.Test;
import swen221.shapedrawer.shapes.Rectangle;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dylan on 3/04/17.
 *
 * Use test naming format: methodName_input_expectedResult
 */
public class RectangleTest {
	@Test
	public void contains_pointInCenter_true() {
		Rectangle rectangle = new Rectangle(1, 1, 2, 2);
		assertTrue(rectangle.contains(2, 2));
	}
}
