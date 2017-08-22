package main.gui.menu;

import main.gui.cardview.GUICard;
import main.gui.cardview.GUICardName;
import main.gui.cardview.GUICardView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The menu is pretty simplistic that is no need for separate control and
 * model classes
 */
public class MenuGUIAndController implements GUICard {
	private static final String INFO_TITLE = "Info";
	private static final String INFO_MESSAGE = "By Dylan Chong";

	private final GUICardView guiCardView;
	private final JPanel rootJPanel;

	public MenuGUIAndController(GUICardView guiCardView) {
		this.guiCardView = guiCardView;

		rootJPanel = new JPanel();
		rootJPanel.setLayout(new BoxLayout(rootJPanel, BoxLayout.Y_AXIS));

		JButton beginNewGameButton = new JButton("Begin new game");
		beginNewGameButton.addActionListener(this::beginGameButtonClicked);
		rootJPanel.add(beginNewGameButton);

		JButton infoButton = new JButton("Info");
		infoButton.addActionListener(this::infoButtonClicked);
		rootJPanel.add(infoButton);

		JButton quitButton = new JButton("Quit");
		quitButton.addActionListener(this::quitButtonClicked);
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

	public void beginGameButtonClicked(ActionEvent actionEvent) {
		guiCardView.setCurrentView(GUICardName.GAME);
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
		guiCardView.quit();
	}
}
