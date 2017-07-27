package test;

import static main.Board.representationToString;
import static org.junit.Assert.assertEquals;

public class TestUtils {

	public static void assertRepresentationEquals(char[][] expected,
												  char[][] actualArray) {
		// Hack to easily compare the 2d arrays, and show the output
		String expectedStr = representationToString(expected);
		String arrayStr = representationToString(actualArray);
		assertEquals(expectedStr, arrayStr);
	}

}
