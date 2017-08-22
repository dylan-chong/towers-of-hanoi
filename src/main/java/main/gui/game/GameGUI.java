package main.gui.game;

import main.gamemodel.GameModel;
import main.gui.GUICard;
import main.gui.GUICardName;

import javax.swing.*;
import java.util.Observer;

public class GameGUI implements GUICard, Observer {
	private final JPanel rootJPanel;
	private final GameModel gameModel;
	private final GameGUIController gameGUIController;

	public GameGUI(GameModel gameModel, GameGUIController gameGUIController) {
		this.gameModel = gameModel;
		this.gameGUIController = gameGUIController;

		rootJPanel = new JPanel();
	}

	@Override
	public JComponent getRootComponent() {
		return rootJPanel;
	}

	@Override
	public GUICardName getCardName() {
		return GUICardName.GAME;
	}
}
