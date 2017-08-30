package main.gui.game.celldrawers.cellcanvas;

import aurelienribon.slidinglayout.*;
import main.gamemodel.CellConsumer;
import main.gamemodel.Direction;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gui.game.Events.EventGameGUIViewUpdated;
import main.gui.game.GameGUIController;
import main.gui.game.GameGUIModel;
import main.gui.game.GameGUIView;
import main.gui.game.celldrawers.CellDrawer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Draws a grid of board cells (by delegating to {@link InnerCellCanvas}
 */
public abstract class CellCanvas extends JLayeredPane {
	protected final GameGUIModel gameGUIModel;
	protected final CellDrawer cellDrawer;

	private final EventGameGUIViewUpdated eventGameGUIViewUpdated;
	private final JPanel defaultLayerPanel;
	private final JPanel paletteLayerPanel;
	private final Collection<CellClickListener> listeners =
			Collections.synchronizedList(new ArrayList<>());
	private final JLabel titleLabel;
	private final InnerCellCanvas cellCanvas;

	private final Map<Cell, SingleCellComponent> cellComponents = new HashMap<>();
	private Set<ComponentPosition> showingCellComps = Collections.emptySet();

	public CellCanvas(
			GameGUIModel gameGUIModel,
			CellDrawer cellDrawer,
			String titleOrNull,
			EventGameGUIViewUpdated eventGameGUIViewUpdated
	) {
		this.gameGUIModel = gameGUIModel;
		this.cellDrawer = cellDrawer;
		this.eventGameGUIViewUpdated = eventGameGUIViewUpdated;

		setResizeOnChange();

		setBorder(new LineBorder(Color.GRAY, 1));

		// Default layer
		{
			defaultLayerPanel = new JPanel();
			defaultLayerPanel.setLayout(new BoxLayout(defaultLayerPanel, BoxLayout.Y_AXIS));
			add(defaultLayerPanel, DEFAULT_LAYER);

			if (titleOrNull != null) {
				titleLabel = new JLabel(titleOrNull, SwingConstants.CENTER);
				titleLabel.setAlignmentX(CENTER_ALIGNMENT);
				defaultLayerPanel.add(titleLabel);
			} else {
				titleLabel = null;
			}

			cellCanvas = new InnerCellCanvas();
			cellCanvas.setTweenManager(SLAnimator.createTweenManager());
			cellCanvas.setAlignmentX(CENTER_ALIGNMENT);
			cellCanvas.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					onClickCellCanvas(e);
				}
			});
			SwingUtilities.invokeLater(() -> cellCanvas.initialize(newSLConfig()));

			defaultLayerPanel.add(cellCanvas);
		}

		// Palette layer
		{
			paletteLayerPanel = new JPanel() {
				@Override
				public Dimension getPreferredSize() {
					return CellCanvas.this.getPreferredSize();
				}
			};
			paletteLayerPanel.setLayout(null);
			paletteLayerPanel.setOpaque(false);
			add(paletteLayerPanel, PALETTE_LAYER);
		}

		// TODO Remove listener on dispose
		eventGameGUIViewUpdated.registerListener((param) -> this.refreshChildren());
		SwingUtilities.invokeLater(this::refreshChildren);
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

	private SLConfig newSLConfig() {
		int[] rowsCols = calculatePreferredRowsCols();

		SLConfig newConfig = new SLConfig(cellCanvas);
		for (int r = 0; r < rowsCols[0]; r++) {
			newConfig.row(1f);
		}
		for (int c = 0; c < rowsCols[1]; c++) {
			newConfig.col(1f);
		}
		return newConfig;
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

		// TODO dont animate if no change
		SLConfig newConfig = newSLConfig();

		Set<ComponentPosition> newShowingCellComps = new HashSet<>();
		Set<ComponentPosition> oldShowingCellComps = showingCellComps;

		forEachCell((cell, row, col) -> {
			if (cell == null) {
				return;
			}

			SingleCellComponent cellComponent = cellComponents.computeIfAbsent(
					cell,
					SingleCellComponent::new
			);

			newShowingCellComps.add(new ComponentPosition(cellComponent, row, col));
			newConfig.place(row, col, cellComponent);
		});

		showingCellComps = newShowingCellComps;

		if (newShowingCellComps.equals(oldShowingCellComps)) {
			// No change. No need to animate
			return;
		}

		Set<Component> slideInCellComps = ComponentPosition.setFromSet(newShowingCellComps);
		slideInCellComps.removeAll(ComponentPosition.setFromSet(oldShowingCellComps));
		Set<Component> slideOutCellComps = ComponentPosition.setFromSet(oldShowingCellComps);
		slideOutCellComps.removeAll(ComponentPosition.setFromSet(newShowingCellComps));

		cellCanvas.queueTransition(
				new SLKeyframe(newConfig, GameGUIView.TRANSITION_DURATION)
						.setStartSide(SLSide.TOP, slideInCellComps.toArray(
								new Component[slideInCellComps.size()]
						))
						.setEndSide(SLSide.BOTTOM, slideOutCellComps.toArray(
								new Component[slideOutCellComps.size()]
						))
		);
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

	/**
	 * Override this to use your own elements
	 */
	protected void updatePalette(JPanel paletteLayerPanel) {
		int labelHeight = titleLabel == null ? 0 : titleLabel.getSize().height;
		paletteLayerPanel.setBorder(new EmptyBorder(
				labelHeight,
				0, 0, 0
		));
	}

	protected int getCellSize() {
		return GameGUIView.PREFERRED_BOARD_CELL_SIZE;
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

	private void refreshChildren() {
		for (Component comp : getComponents()) {
			Dimension size = comp.getPreferredSize();
			Insets insets = getInsets();
			comp.setBounds(insets.left, insets.top, size.width, size.height);
		}

		updatePalette(paletteLayerPanel);
	}

	private void setResizeOnChange() {
		addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
				refreshChildren();
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				refreshChildren();
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
				refreshChildren();
			}
		});
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreshChildren();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				refreshChildren();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				refreshChildren();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				refreshChildren();
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
	private class InnerCellCanvas extends SLPanel {

		private final Queue<SLTransition> transitionsToDo = new ConcurrentLinkedQueue<>();

		private final AtomicBoolean isAnimating = new AtomicBoolean(false);

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			paintCanvasComponent(((Graphics2D) g));

			// TODO call in revalidate
		}

		@Override
		public Dimension getPreferredSize() {
			int[] preferredRowsCols = calculatePreferredRowsCols();
			return new Dimension(
					preferredRowsCols[1] * GameGUIView.PREFERRED_BOARD_CELL_SIZE,
					preferredRowsCols[0] * GameGUIView.PREFERRED_BOARD_CELL_SIZE
			);
		}

		public void queueTransition(SLKeyframe keyframe) {
			SLTransition animation = cellCanvas.createTransition()
					.push(keyframe.setCallback(this::animationFinish));
			transitionsToDo.add(animation);

			startNextAnimation();
		}

		private void animationFinish() {
			if (transitionsToDo.peek() == null) {
				return;
			}

			startNextAnimation();
		}

		private void startNextAnimation() {
			if (isAnimating.get() || transitionsToDo.peek() == null) {
				return;
			}

			SLTransition transition = transitionsToDo.poll();
			SwingUtilities.invokeLater(transition::play);
		}
	}

	private class SingleCellComponent extends JComponent {
		private final Cell cell;
		private final Player player;

		public SingleCellComponent(Cell cell) {
			this.cell = cell;
			this.player = gameGUIModel.getPlayerOfCellOrRotatedCopy(cell);

			setPreferredSize(new Dimension(
					getCellSize(), getCellSize()
			));
		}

		@Override
		protected void paintComponent(Graphics g) {
			cellDrawer.valueOf(cell).draw(
					player,
					((Graphics2D) g),
					0, // row and col are 0 to draw at top left of component
					0,
					getCellSize()
			);
		}
	}

	private static class ComponentPosition {

		private static Set<Component> setFromSet(
				Set<ComponentPosition> componentPositions
		) {
			return componentPositions.stream()
					.map(componentPosition -> componentPosition.component)
					.collect(Collectors.toSet());
		}

		public final Component component;
		public final int row, col;

		private ComponentPosition(Component component, int row, int col) {
			this.component = component;
			this.row = row;
			this.col = col;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ComponentPosition that = (ComponentPosition) o;

			if (row != that.row) return false;
			if (col != that.col) return false;
			return component != null ? component.equals(that.component) : that.component == null;
		}

		@Override
		public int hashCode() {
			int result = component != null ? component.hashCode() : 0;
			result = 31 * result + row;
			result = 31 * result + col;
			return result;
		}
	}
}
