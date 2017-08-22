package main.gui.game.gamemodelviews;

import javax.swing.*;
import java.awt.*;

/**
 * TODO
 */
public class BoardCellView extends JComponent {
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.fillOval(0, 0, 50, 50);
	}
}
