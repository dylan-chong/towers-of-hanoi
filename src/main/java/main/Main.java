package main;

import test.TestRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
		new TextBoardController(
				new Scanner(System.in),
				System.out,
				new Board()
		).listenUntilGameEnd();
	}
}
