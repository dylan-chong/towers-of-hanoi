package test;

import static main.gamemodel.Textable.convertToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestUtils {

	public static void assertRepresentationEquals(char[][] expected,
												  char[][] actualArray) {
		// Hack to easily compare the 2d arrays, and show the output
		String expectedStr = convertToString(expected);
		String arrayStr = convertToString(actualArray);
		assertEquals(expectedStr, arrayStr);
	}

	public static void assertRepresentationNotEquals(char[][] expected,
													 char[][] actualArray) {
		String expectedStr = convertToString(expected);
		String arrayStr = convertToString(actualArray);
		assertNotEquals(expectedStr, arrayStr);
	}

	public interface ThrowingConsumer<T> {
		void accept(T param) throws Exception;
	}
}
