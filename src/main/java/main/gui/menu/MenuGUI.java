package main.gui.menu;

import main.gui.GUICard;
import main.gui.GUICardName;

import javax.swing.*;

public class MenuGUI implements GUICard {
	private final MenuModel menuModel;
	private final JPanel rootJPanel;

	public MenuGUI(MenuModel menuModel, MenuGUIController menuGUIController) {
		this.menuModel = menuModel;

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));

		JButton beginNewGameButton = new JButton("Begin new game");
		beginNewGameButton.addActionListener(menuGUIController::beginGameButtonClicked);
		rootJPanel.add(beginNewGameButton);

		JButton infoButton = new JButton("Info");
		infoButton.addActionListener(menuGUIController::infoButtonClicked);
		rootJPanel.add(infoButton);

		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(menuGUIController::quitButtonClicked);
		rootJPanel.add(quitButton);
	}

	@Override
	public JComponent getRootComponent() {
		return rootJPanel;
	}

	@Override
	public GUICardName getCardName() {
		return GUICardName.MENU;
	}
}
