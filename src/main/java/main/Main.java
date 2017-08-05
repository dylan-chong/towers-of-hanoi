package main;

import main.gamemodel.Board;
import main.gamemodel.GameModel;
import main.textcontroller.GameTextController;
import main.textcontroller.TextCommandStateMapper;
import test.TestRunner;

import java.io.PrintStream;
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

	public static boolean areAssertionsEnabled() {
		boolean enabled = false;
		// noinspection ConstantConditions,AssertWithSideEffects
		assert enabled = true;
		// noinspection ConstantConditions
		return enabled;
	}

	private static void startApp() {
		GameModel game = new GameModel(new Board());
		PrintStream out = System.out;
		GameTextController controller = new GameTextController(
				new Scanner(System.in),
				System.out,
				new GameTextController.AppExceptionHandler(out),
				new TextCommandStateMapper(game),
				game
		);

		controller.runUntilGameEnd();
	}
}
