package test;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

/**
 * The test runner
 */
public class TestRunner {

	/**
	 * List all classes to test here
	 */
	private static final Class<?>[] TEST_CLASSES = new Class[] {
			BoardCellTest.class,
			BoardTest.class,
			GameModelTest.class,
			GameTextControllerTest.class,
			DirectionTest.class,
	};

	public static void run() {
		System.out.println("Running tests in: ");
		for (Class<?> testClass : TEST_CLASSES) {
			System.out.println("- " + testClass.getSimpleName());
		}

		System.out.println();

		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		junit.run(TEST_CLASSES);
	}
}
