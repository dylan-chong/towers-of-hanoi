package main;

import main.gamemodel.Board;
import main.gamemodel.GameModel;
import main.gui.cardview.GUICardView;
import main.gui.game.GameGUI;
import main.gui.game.GameGUIController;
import main.gui.game.GameGUIModel;
import main.gui.game.drawersandviews.CellDrawer;
import main.gui.menu.MenuGUIAndController;
import main.textcontroller.GameTextController;
import test.TestRunner;

import javax.swing.*;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

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
			GUICardView guiCardView = new GUICardView();

			MenuGUIAndController menuGUIAndController =
					new MenuGUIAndController(guiCardView);
			guiCardView.addView(menuGUIAndController);

			Supplier<GameModel> gameModelFactory = () -> new GameModel(new Board());
			GameGUIModel gameGUIModel = new GameGUIModel(gameModelFactory);
			GameGUIController gameGUIController = new GameGUIController(gameGUIModel);
			GameGUI gameGUI = new GameGUI(
					gameGUIModel,
					gameGUIController,
					new CellDrawer()
			);
			guiCardView.addView(gameGUI);

			guiCardView.setCurrentView(menuGUIAndController);
			guiCardView.show();
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
