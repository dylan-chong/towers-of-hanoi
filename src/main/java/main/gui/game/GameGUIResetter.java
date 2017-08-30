package main.gui.game;

import main.gamemodel.Direction;
import main.gamemodel.GameModel;
import main.gui.cardview.GUICardFrame;
import main.gui.cardview.GUICardName;
import main.gui.game.celldrawers.CellDrawer;

import javax.swing.*;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
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

		GameAction[] gs = {
				() -> gameGUIModel.getGameModel().create('a', 0),
				() -> gameGUIModel.getGameModel().move('a', Direction.SOUTH),
				() -> gameGUIModel.getGameModel().passTurnState(),
				() -> gameGUIModel.getGameModel().create('c', 0),
				() -> gameGUIModel.getGameModel().move('c', Direction.NORTH),
				() -> gameGUIModel.getGameModel().passTurnState(),
				() -> gameGUIModel.getGameModel().create('b', 0),
				() -> gameGUIModel.getGameModel().react(gameGUIModel.getGameModel().getReactions().stream().findAny().orElseGet(null))
		};
		AtomicInteger i = new AtomicInteger(0);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (i.get() >= gs.length) return;

				SwingUtilities.invokeLater(() -> gameGUIModel.performGameAction(() -> {
					gs[i.getAndIncrement()].perform();
					gameGUIModel.update(null, null);
				}));
			}
		}, new Date(System.currentTimeMillis() + 2000), 300);

	}
}
