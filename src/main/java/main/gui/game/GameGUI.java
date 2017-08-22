package main.gui.game;

import main.gui.GUICard;
import main.gui.GUICardName;

import javax.swing.*;
import java.awt.*;

public class GameGUI implements GUICard {
	private final JPanel rootJPanel;

	public GameGUI(GameGUIController gameGUIController) {
		rootJPanel = new JPanel();
		rootJPanel.setPreferredSize(new Dimension(500, 500));
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
