package test;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

import java.util.Arrays;

/**
 * The test runner
 */
public class TestRunner {

	/**
	 * List all classes to test here
	 */
	private static final Class<?>[] TEST_CLASSES = new Class[] {
			ExampleTests.class
	};

	public static void run() {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		Arrays.stream(TEST_CLASSES).forEach(junit::run);
	}
}
