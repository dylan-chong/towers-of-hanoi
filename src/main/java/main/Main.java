package main;

import aurelienribon.slidinglayout.SLAnimator;
import main.gamemodel.Board;
import main.gamemodel.GameModel;
import main.gui.cardview.GUICardFrame;
import main.gui.cardview.GUICardName;
import main.gui.game.CellRotationDialogShower;
import main.gui.game.GameGUIController;
import main.gui.game.GameGUIModel;
import main.gui.game.GameGUIView;
import main.gui.game.celldrawers.CellDrawer;
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

			// Reset the game gui
			eventGameReset.registerListener((aVoid) -> {
				guiCardFrame.removeView(GUICardName.GAME);

				GameGUIModel gameGUIModel = new GameGUIModel(gameModelFactory.get());
				GameGUIController gameGUIController = new GameGUIController(
						gameGUIModel,
						eventReactionClicked
				);
				CellDrawer cellDrawer = new CellDrawer(gameGUIModel);
				CellRotationDialogShower cellRotationDialogShower =
						new CellRotationDialogShower(cellDrawer, guiCardFrame);
				GameGUIView gameGUIView = new GameGUIView(
						gameGUIModel,
						gameGUIController,
						cellDrawer,
						cellRotationDialogShower,
						eventGameGUIViewUpdated,
						eventReactionClicked,
						eventGameReset,
						guiCardFrame
				);
				guiCardFrame.addView(gameGUIView);
			});

			eventGameReset.broadcast(null); // create game gui stuff

			guiCardFrame.setCurrentView(menuGUI);
			guiCardFrame.show();

			SLAnimator.start();
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
