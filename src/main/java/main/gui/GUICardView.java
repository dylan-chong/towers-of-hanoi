package main.gui;

import javax.swing.*;
import java.awt.*;

public class GUICardView {
	private static final String WINDOW_TITLE = "Swords and Shields";

	private final JPanel jPanel;
	private final JFrame jFrame;
	private final CardLayout cardLayout;

	public GUICardView() {
		jFrame = new JFrame(WINDOW_TITLE);

		cardLayout = new CardLayout();
		jPanel = new JPanel(cardLayout);
		jFrame.add(jPanel);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void addView(GUICard guiCard) {
		jPanel.add(guiCard.getCardName().name(), guiCard.getRootComponent());
	}

	public void setCurrentView(GUICardName cardName) {
		cardLayout.show(jPanel, cardName.name());
	}

	public void setCurrentView(GUICard guiCard) {
		setCurrentView(guiCard.getCardName());
	}

	public void show() {
		jFrame.pack();
		jFrame.setVisible(true);
	}

	public void quit() {
		jFrame.setVisible(false);
		System.exit(0);
	}
}
