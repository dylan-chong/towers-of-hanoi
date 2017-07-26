package main;

import test.TestRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Delete this when you put your assignments here
 */
public class Main {
	public static void main(String[] args) {
		List<String> argsList = Arrays.asList(args);

		if (argsList.contains("--test")) {
			TestRunner.run();
		} else {
			startApp();
		}
	}

	private static void startApp() {
		throw new RuntimeException("\n\n\n" +

				"*************** YAY, THE PROJECT RUNS! ***************" +

				"\n\n" +

				"NOTE: You don't have to follow these instructions if you " +
				"only run the program with JUnit tests" +

				"\n\n" +

				"Just switch the 'Main class' in the 'Main App' build " +
				"configuration (if you are using IntelliJ) to whatever " +
				"class has the 'public static void main(String[] args)' " +
				"method in it" +

				"\n\n" +

				"******************************************************" +

				"\n\n\n"
		);
	}
}
