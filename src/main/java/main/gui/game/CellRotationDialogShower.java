package main.gui.game;

import main.gamemodel.Player;
import main.gamemodel.cells.PieceCell;
import main.gui.cardframe.GUICardFrame;
import main.gui.game.celldrawers.CellDrawer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CellRotationDialogShower {
	private static final String ROTATION_DIALOG_MESSAGE = "Click the cell to rotate it";

	private final CellDrawer cellDrawer;
	private final GUICardFrame guiCardFrame;

	public CellRotationDialogShower(CellDrawer cellDrawer, GUICardFrame guiCardFrame) {
		this.cellDrawer = cellDrawer;
		this.guiCardFrame = guiCardFrame;
	}

	public ShowResult showDialog(PieceCell cell, Player cellPlayer) {
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
		dialogPanel.setBorder(new EmptyBorder(15, 15, 0, 15));

		CellDrawerAndRotator cellDrawerAndRotator = new CellDrawerAndRotator(cell, cellPlayer);
		cellDrawerAndRotator.setAlignmentX(Component.CENTER_ALIGNMENT);
		dialogPanel.add(cellDrawerAndRotator);

		JDialog dialog = new JDialog((Frame) null, true);
		dialog.setTitle(ROTATION_DIALOG_MESSAGE);

		JOptionPane optionPane = new JOptionPane(
				ROTATION_DIALOG_MESSAGE,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION
		);
		optionPane.addPropertyChangeListener(evt -> {
			// Copied chunk from a shitty java example by oracle.
			// Like, really shitty. If you dare:
			// https://docs.oracle.com/javase/tutorial/uiswing/examples/zipfiles/components-DialogDemoProject.zip
			String prop = evt.getPropertyName();
			if (dialog.isVisible()
					&& (evt.getSource() == optionPane)
					&& (JOptionPane.VALUE_PROPERTY.equals(prop))) {
				dialog.setVisible(false);
			}
		});
		optionPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		dialogPanel.add(optionPane);

		dialog.setContentPane(dialogPanel);

		guiCardFrame.setGrayGlassVisible(true);
		dialog.pack();
		dialog.setVisible(true); // blocks thread
		guiCardFrame.setGrayGlassVisible(false);

		return new ShowResult(
				(int) optionPane.getValue(),
				cellDrawerAndRotator.cellToRotate
		);
	}

	public static class ShowResult {
		public final int jOptionPaneValue;
		public final PieceCell rotatedPieceCell;

		private ShowResult(int jOptionPaneValue, PieceCell rotatedPieceCell) {
			this.jOptionPaneValue = jOptionPaneValue;
			this.rotatedPieceCell = rotatedPieceCell;
		}
	}

	private class CellDrawerAndRotator extends JComponent {
		private static final int CELL_SIZE = 100;

		private final PieceCell cellToRotate;
		private final Player cellPlayer;

		public CellDrawerAndRotator(PieceCell cellToRotate, Player cellPlayer) {
			this.cellToRotate = cellToRotate;
			this.cellPlayer = cellPlayer;

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					cellToRotate.rotateClockwise();
					repaint();
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphics2D = (Graphics2D) g;
			cellDrawer.valueOf(cellToRotate).draw(
					cellPlayer,
					graphics2D,
					0, 0,
					CELL_SIZE
			);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(CELL_SIZE, CELL_SIZE);
		}

		@Override
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}
	}
}
