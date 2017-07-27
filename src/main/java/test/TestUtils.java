package test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestUtils {

	public static void assertRepresentationEquals(char[][] expected,
												  char[][] actualArray) {
		// Hack to easily compare the 2d arrays, and show the output
		String expectedStr = Arrays.deepToString(expected);
		String arrayStr = Arrays.deepToString(actualArray);
		assertEquals(expectedStr, arrayStr);
	}

}
