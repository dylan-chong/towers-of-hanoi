package main.gui.menu;

import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardFrame;
import main.gui.cardview.GUICardName;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The menu is pretty simplistic that is no need for separate control and
 * model classes
 */
public class MenuGUI implements GUICard {
	private static final String INFO_TITLE = "Info";
	private static final String INFO_MESSAGE = "By Dylan Chong";
	private static final int BORDER_SIZE = 20;

	private final GUICardFrame guiCardFrame;
	private final JPanel rootJPanel;

	public MenuGUI(GUICardFrame guiCardFrame) {
		this.guiCardFrame = guiCardFrame;

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));
		rootJPanel.setBorder(new EmptyBorder(
				BORDER_SIZE,
				BORDER_SIZE,
				BORDER_SIZE,
				BORDER_SIZE
		));

		JLabel gameTitle = new JLabel(GUICardFrame.GAME_NAME);
		gameTitle.setFont(new Font(null, Font.BOLD, 30));
		addMenuComponent(gameTitle);

		JButton beginNewGameButton = new JButton("Begin new game");
		beginNewGameButton.addActionListener(this::beginGameButtonClicked);
		addMenuComponent(beginNewGameButton);

		JButton infoButton = new JButton("Info");
		infoButton.addActionListener(this::infoButtonClicked);
		addMenuComponent(infoButton);

		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(this::quitButtonClicked);
		addMenuComponent(quitButton);
	}

	public void addMenuComponent(JComponent jComponent) {
		jComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
		jComponent.setAlignmentY(Component.CENTER_ALIGNMENT);
		rootJPanel.add(jComponent);
	}

	@Override
	public JComponent getRootComponent() {
		return rootJPanel;
	}

	@Override
	public GUICardName getCardName() {
		return GUICardName.MENU;
	}

	public void beginGameButtonClicked(ActionEvent actionEvent) {
		guiCardFrame.setCurrentView(GUICardName.GAME);
	}

	public void infoButtonClicked(ActionEvent actionEvent) {
		JOptionPane.showMessageDialog(
				rootJPanel,
				INFO_MESSAGE,
				INFO_TITLE,
				JOptionPane.INFORMATION_MESSAGE
		);
	}

	public void quitButtonClicked(ActionEvent actionEvent) {
		guiCardFrame.quit();
	}
}
