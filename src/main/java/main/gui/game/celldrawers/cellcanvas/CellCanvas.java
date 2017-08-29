package main.gui.game.celldrawers.cellcanvas;

import main.gamemodel.CellConsumer;
import main.gamemodel.Direction;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gui.game.GameGUIController;
import main.gui.game.GameGUIModel;
import main.gui.game.GameGUIView;
import main.gui.game.celldrawers.CellDrawer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static java.awt.Color.RED;

/**
 * Draws a grid of board cells (by delegating to {@link InnerCellCanvas}
 */
public abstract class CellCanvas extends JLayeredPane {
	protected final GameGUIModel gameGUIModel;
	protected final CellDrawer cellDrawer;

	protected final JPanel defaultLayerPanel;
	protected final JPanel paletteLayerPanel;

	private final Collection<CellClickListener> listeners =
			Collections.synchronizedList(new ArrayList<>());

	public CellCanvas(
			GameGUIModel gameGUIModel,
			CellDrawer cellDrawer,
			String titleOrNull
	) {
		this.gameGUIModel = gameGUIModel;
		this.cellDrawer = cellDrawer;

		setResizeOnChange();

		setBorder(new LineBorder(Color.GRAY, 1));

		defaultLayerPanel = new JPanel();
		defaultLayerPanel.setLayout(new BoxLayout(defaultLayerPanel, BoxLayout.Y_AXIS));
		add(defaultLayerPanel, DEFAULT_LAYER);

		paletteLayerPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return CellCanvas.this.getPreferredSize();
			}
		};
		paletteLayerPanel.setLayout(null);
		paletteLayerPanel.setOpaque(false);
		add(paletteLayerPanel, PALETTE_LAYER);

		if (titleOrNull != null) {
			JLabel titleLabel = new JLabel(titleOrNull, SwingConstants.CENTER);
			titleLabel.setAlignmentX(CENTER_ALIGNMENT);
			defaultLayerPanel.add(titleLabel);
			titleLabel.setBackground(RED);
		}

		JComponent cellCanvas = new InnerCellCanvas();
		cellCanvas.setAlignmentX(CENTER_ALIGNMENT);
		cellCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClickCellCanvas(e);
			}
		});
		defaultLayerPanel.add(cellCanvas);

		SwingUtilities.invokeLater(this::resizeChildren);
	}

	@Override
	public Dimension getPreferredSize() {
		Insets insets = getInsets();
		Dimension size = defaultLayerPanel.getPreferredSize();
		size.width += insets.left + insets.right;
		size.height += insets.top + insets.bottom;
		return size;
	}

	public void addCellClickListener(CellClickListener listener) {
		listeners.add(listener);
	}

	protected void paintCanvasComponent(Graphics2D graphics2D) {
		int[] rowsCols = calculatePreferredRowsCols();
		graphics2D.setColor(Color.GRAY);
		graphics2D.setStroke(new BasicStroke(1));
		graphics2D.drawRect(
				0, 0,
				getCellSize() * rowsCols[1],
				getCellSize() * rowsCols[0]
		);

		forEachCell((cell, row, col) -> {
			if (cell == null) {
				return;
			}

			Player player = gameGUIModel.getPlayerOfCellOrRotatedCopy(cell);
			cellDrawer.valueOf(cell).draw(
					player,
					graphics2D,
					col,
					row,
					getCellSize()
			);
		});
	}

	protected abstract void forEachCell(CellConsumer cellConsumer);

	protected int[] calculatePreferredRowsCols() {
		int[] rowsCols = {-1, -1};
		forEachCell((cell, row, col) -> {
			rowsCols[0] = Math.max(row, rowsCols[0]);
			rowsCols[1] = Math.max(col, rowsCols[1]);
		});
		rowsCols[0] += 1;
		rowsCols[1] += 1;
		return rowsCols;
	}

	private void onClickCellCanvas(MouseEvent e) {
		int size = getCellSize();
		int col = e.getX() / size;
		int row = e.getY() / size;
		float colOffset = ((e.getX() % size) * 1f) / size;
		float rowOffset = ((e.getY() % size) * 1f) / size;

		Cell clickedCell = getCellAt(row, col);
		synchronized (listeners) {
			for (CellClickListener listener : listeners) {
				listener.onCellClick(clickedCell, new CellClickEvent(
						e, row, col, rowOffset, colOffset
				));
			}
		}
	}

	private Cell getCellAt(int targetRow, int targetCol) {
		AtomicReference<Cell> resultCell = new AtomicReference<>();

		forEachCell((cell, row, col) -> {
			if (row == targetRow && col == targetCol) {
				resultCell.set(cell);
			}
		});

		return resultCell.get();
	}

	private void resizeChildren() {
		for (Component comp : getComponents()) {
			Dimension size = comp.getPreferredSize();
			Insets insets = getInsets();
			comp.setBounds(insets.left, insets.top, size.width, size.height);
		}
		repaint();
	}

	private int getCellSize() {
		return GameGUIView.PREFERRED_BOARD_CELL_SIZE;
	}

	private void setResizeOnChange() {
		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				resizeChildren();
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeChildren();
			}
		});
	}

	public interface CellClickListener {
		void onCellClick(Cell cellOrNull, CellClickEvent e);
	}

	public static class CellClickEvent {
		public final MouseEvent e;
		public final int row;
		public final int col;
		public final float rowOffset;
		public final float colOffset;

		public CellClickEvent(
				MouseEvent e,
				int row,
				int col,
				float rowOffset,
				float colOffset
		) {
			this.e = e;
			this.row = row;
			this.col = col;
			this.rowOffset = rowOffset;
			this.colOffset = colOffset;
		}

		public Direction getClickedEdge() {
			double distance = GameGUIController.MOVE_CLICK_EDGE_DISTANCE;
			if (rowOffset < distance) {
				return Direction.NORTH;
			} else if (rowOffset > 1 - distance) {
				return Direction.SOUTH;
			} else if (colOffset < distance) {
				return Direction.WEST;
			} else if (colOffset > 1 - distance) {
				return Direction.EAST;
			}

			return null;
		}
	}

	/**
	 * Actually does the drawing
	 */
	private class InnerCellCanvas extends JComponent {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			paintCanvasComponent(((Graphics2D) g));
		}

		@Override
		public Dimension getPreferredSize() {
			int[] preferredRowsCols = calculatePreferredRowsCols();
			return new Dimension(
					preferredRowsCols[1] * GameGUIView.PREFERRED_BOARD_CELL_SIZE,
					preferredRowsCols[0] * GameGUIView.PREFERRED_BOARD_CELL_SIZE
			);
		}
	}
}
