package main;

import aurelienribon.slidinglayout.SLAnimator;
import main.gamemodel.Board;
import main.gamemodel.GameModel;
import main.gui.cardview.GUICardFrame;
import main.gui.game.GameGUIResetter;
import main.gui.menu.MenuGUI;
import main.textcontroller.GameTextController;
import test.TestRunner;

import javax.swing.*;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import static main.gui.game.Events.*;

/**
 * Delete this when you put your assignments here
 */
public class Main {

	public static void main(String[] args) {
		List<String> argsList = Arrays.asList(args);
		if (argsList.contains("--test")) {
			TestRunner.run();
		} else if (argsList.contains("--text")){
			startTextApp();
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
		SwingUtilities.invokeLater(() -> {
			EventGameGUIViewUpdated eventGameGUIViewUpdated = new EventGameGUIViewUpdated();
			EventReactionClicked eventReactionClicked = new EventReactionClicked();
			EventGameReset eventGameReset = new EventGameReset();

			GUICardFrame guiCardFrame = new GUICardFrame();

			MenuGUI menuGUI = new MenuGUI(guiCardFrame);
			guiCardFrame.addView(menuGUI);

			Supplier<GameModel> gameModelFactory = () -> new GameModel(new Board());

			// The GameGUIResetter serves as part of the DI container
			GameGUIResetter gameGUIResetter = new GameGUIResetter(
					eventGameReset,
					eventGameGUIViewUpdated,
					eventReactionClicked,
					guiCardFrame,
					gameModelFactory
			);
			gameGUIResetter.reset();

			SLAnimator.start();
			guiCardFrame.setCurrentView(menuGUI);
			guiCardFrame.show();
		});
	}

	private static void startTextApp() {
		GameModel game = new GameModel(new Board());
		PrintStream out = System.out;

		GameTextController controller = new GameTextController(
				new Scanner(System.in),
				out,
				new GameTextController.AppExceptionHandler(out),
				game
		);

		controller.runUntilGameEnd();
	}
}
