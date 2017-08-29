package main.gui.cardview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;

/**
 * The main frame and layout for the gui
 */
public class GUICardFrame {
	public static final String GAME_NAME = "Swords and Shields";
	public static final String WINDOW_TITLE = GAME_NAME;

	private final JPanel jPanel;
	private final JFrame jFrame;
	private final CardLayout cardLayout;

	public GUICardFrame() {
		jFrame = new JFrame(WINDOW_TITLE);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setFocusable(true);
		jFrame.setGlassPane(new GrayGlass());

		cardLayout = new CardLayout();

		jPanel = new JPanel(cardLayout);
		jFrame.add(jPanel);
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

	public void setGrayGlassVisible(boolean visible) {
		jFrame.getGlassPane().setVisible(visible);
		jFrame.revalidate();
		jFrame.repaint();
	}

	private static class GrayGlass extends JComponent {
		public GrayGlass() {
			// Capture mouse events
			addMouseListener(new MouseAdapter() {});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphics2D = (Graphics2D) g;
			Dimension size = getSize();
			graphics2D.setColor(new Color(200, 200, 200, 150));
			graphics2D.fillRect(0, 0, size.width, size.height);
		}
	}
}
