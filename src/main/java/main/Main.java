package main;

import main.gamemodel.Board;
import main.gamemodel.GameModel;
import main.gui.GUICardManager;
import main.gui.game.GameGUI;
import main.gui.game.GameGUIController;
import main.gui.menu.MenuGUI;
import main.gui.menu.MenuGUIController;
import main.gui.menu.MenuModel;
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

	public static boolean areAssertionsEnabled() {
		boolean enabled = false;
		// noinspection ConstantConditions,AssertWithSideEffects
		assert enabled = true;
		// noinspection ConstantConditions
		return enabled;
	}

	private static void startApp() {
		GUICardManager guiCardManager = new GUICardManager();

		MenuModel menuModel = new MenuModel();
		MenuGUIController menuGUIController = new MenuGUIController(
				menuModel, guiCardManager
		);
		MenuGUI menuGUI = new MenuGUI(menuModel, menuGUIController);
		guiCardManager.addView(menuGUI);

		GameModel gameModel = new GameModel(new Board());
		GameGUIController gameGUIController = new GameGUIController(gameModel);
		GameGUI gameGUI = new GameGUI(gameGUIController);
		guiCardManager.addView(gameGUI);

		guiCardManager.setCurrentView(menuGUI);
		guiCardManager.show();
	}

}
