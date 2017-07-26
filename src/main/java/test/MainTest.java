package test;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

/**
 * The test runner
 */
public class MainTest {

	/**
	 * List all classes to test here
	 */
	private static final Class<?>[] TEST_CLASSES = new Class[] {
			ExampleTests.class
	};

	public static void main(String[] args) {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		for (Class<?> testClass : TEST_CLASSES) {
			junit.run(testClass);
		}
	}
}
