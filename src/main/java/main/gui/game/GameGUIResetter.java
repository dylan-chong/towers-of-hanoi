package main.gui.game;

import main.gamemodel.GameModel;
import main.gui.cardview.GUICardFrame;
import main.gui.cardview.GUICardName;
import main.gui.game.celldrawers.CellDrawer;

import javax.swing.*;
import java.util.function.Supplier;

import static main.gui.game.Events.*;

public class GameGUIResetter {
	private final EventGameReset eventGameReset;
	private final EventGameGUIViewUpdated eventGameGUIViewUpdated;
	private final EventReactionClicked eventReactionClicked;
	private final GUICardFrame guiCardFrame;
	private final Supplier<GameModel> gameModelFactory;

	public GameGUIResetter(
			EventGameReset eventGameReset,
			EventGameGUIViewUpdated eventGameGUIViewUpdated,
			EventReactionClicked eventReactionClicked,
			GUICardFrame guiCardFrame,
			Supplier<GameModel> gameModelFactory
	) {

		this.eventGameReset = eventGameReset;
		this.eventGameGUIViewUpdated = eventGameGUIViewUpdated;
		this.eventReactionClicked = eventReactionClicked;
		this.guiCardFrame = guiCardFrame;
		this.gameModelFactory = gameModelFactory;

		eventGameReset.registerListener((aVoid) -> {
			SwingUtilities.invokeLater(this::reset);
		});
	}

	public void reset() {
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
	}
}
