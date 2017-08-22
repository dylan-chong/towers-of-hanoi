package main;

import main.gamemodel.Board;
import main.gamemodel.GameModel;
import main.gui.cardview.GUICardView;
import main.gui.game.GameGUI;
import main.gui.game.GameGUIController;
import main.gui.menu.MenuGUIAndController;
import test.TestRunner;

import javax.swing.*;
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

			// TODO LATER create a new game each time
			GameModel gameModel = new GameModel(new Board());
			GameGUIController gameGUIController = new GameGUIController(gameModel);
			GameGUI gameGUI = new GameGUI(gameModel, gameGUIController);
			guiCardView.addView(gameGUI);

			guiCardView.setCurrentView(menuGUIAndController);
			guiCardView.show();
		});
	}

}
