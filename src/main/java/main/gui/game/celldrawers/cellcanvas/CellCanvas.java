package main.gui.game.celldrawers.cellcanvas;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLSide;
import main.gamemodel.CellConsumer;
import main.gamemodel.Player;
import main.gamemodel.cells.Cell;
import main.gui.game.Events.EventGameGUIViewUpdated;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Draws a grid of board cells (by delegating to {@link InnerCellCanvas}
 */
public abstract class CellCanvas extends JLayeredPane {
	protected final GameGUIModel gameGUIModel;
	protected final CellDrawer cellDrawer;

	private final JPanel defaultLayerPanel;
	private final JPanel paletteLayerPanel;
	private final Collection<CellClickListener> listeners =
			Collections.synchronizedList(new ArrayList<>());
	private final JLabel titleLabel;
	private final InnerCellCanvas innerCellCanvas;

	private final Map<Cell, SingleCellComponent> cellComponents = new HashMap<>();
	private Set<ComponentPosition> showingCellComps = Collections.emptySet();
	private int cellSize = GameGUIView.PREFERRED_CELL_SIZE;

	public CellCanvas(
			GameGUIModel gameGUIModel,
			CellDrawer cellDrawer,
			String titleOrNull,
			EventGameGUIViewUpdated eventGameGUIViewUpdated
	) {
		this.gameGUIModel = gameGUIModel;
		this.cellDrawer = cellDrawer;

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

			innerCellCanvas = new InnerCellCanvas(this);
			innerCellCanvas.setTweenManager(SLAnimator.createTweenManager());
			innerCellCanvas.setAlignmentX(CENTER_ALIGNMENT);
			innerCellCanvas.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					onClickCellCanvas(e);
				}
			});
			SwingUtilities.invokeLater(() -> innerCellCanvas.initialize(newSLConfig()));

			defaultLayerPanel.add(innerCellCanvas);
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

		if (titleLabel != null) {
			// Hack to get height to be right
			size.height += titleLabel.getPreferredSize().height;
		}

		return size;
	}

	public void addCellClickListener(CellClickListener listener) {
		listeners.add(listener);
	}

	@Override
	public String toString() {
		return titleLabel.getText();
	}

	private SLConfig newSLConfig() {
		int[] rowsCols = calculateRowsCols();

		SLConfig newConfig = new SLConfig(innerCellCanvas);
		for (int r = 0; r < rowsCols[0]; r++) {
			newConfig.row(1f);
		}
		for (int c = 0; c < rowsCols[1]; c++) {
			newConfig.col(1f);
		}
		return newConfig;
	}

	protected void paintCanvasComponent(Graphics2D graphics2D) {
		resetCellComponents();
	}

	private void resetCellComponents() {
		SLConfig newConfig = newSLConfig();

		Set<ComponentPosition> newShowingCellComps = getNewCellComponents(newConfig);
		Set<ComponentPosition> oldShowingCellComps = showingCellComps;
		showingCellComps = newShowingCellComps;

		if (newShowingCellComps.equals(oldShowingCellComps)) {
			// No change. No need to animate
			return;
		}

		queueTransition(newShowingCellComps, oldShowingCellComps, newConfig);
	}

	/**
	 * @return The new set of components showing
	 * @param newConfig Config to apply cells to
	 */
	private Set<ComponentPosition> getNewCellComponents(SLConfig newConfig) {
		Set<ComponentPosition> newShowingCellComps = new HashSet<>();
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
		return newShowingCellComps;
	}

	private void queueTransition(
			Set<ComponentPosition> newShowingCellComps,
			Set<ComponentPosition> oldShowingCellComps,
			SLConfig newConfig
	) {
		Set<Component> slideInCellComps = ComponentPosition.setFromSet(newShowingCellComps);
		slideInCellComps.removeAll(ComponentPosition.setFromSet(oldShowingCellComps));
		Set<Component> slideOutCellComps = ComponentPosition.setFromSet(oldShowingCellComps);
		slideOutCellComps.removeAll(ComponentPosition.setFromSet(newShowingCellComps));

		innerCellCanvas.queueTransition(
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

	protected int[] calculateRowsCols() {
		int[] rowsCols = {-1, -1};
		forEachCell((cell, row, col) -> {
			rowsCols[0] = Math.max(row, rowsCols[0]);
			rowsCols[1] = Math.max(col, rowsCols[1]);
		});
		rowsCols[0] += 1;
		rowsCols[1] += 1;

		rowsCols[0] = Math.max(minRowsCols()[0], rowsCols[0]);
		rowsCols[1] = Math.max(minRowsCols()[1], rowsCols[1]);
		return rowsCols;
	}

	protected abstract int[] minRowsCols();

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
		return cellSize;
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

	private Dimension getSizeToContainChildren() {
		Dimension size = null;
		Dimension thisSize = getSize();
		Insets insets = getInsets();

		if (thisSize.width * thisSize.height == 0) {
			size = Arrays.stream(getComponents())
					.map(Component::getPreferredSize)
					.reduce((s1, s2) -> new Dimension(
							(int) Math.max(s1.getWidth(), s2.getWidth()),
							(int) Math.max(s1.getHeight(), s2.getHeight())
					))
					.orElse(null);
		}
		if (size == null) {
			size = thisSize;
			size.width -= insets.left + insets.right;
			size.height -= insets.top + insets.bottom;
		}

		return size;
	}

	private Dimension getSizeForChildren() {
		Dimension size = getSizeToContainChildren();
		// Shrink size so that rows and cols are even size
		int[] rowsCols = calculateRowsCols();
		cellSize = IntStream
				.of(size.height / rowsCols[0], size.width / rowsCols[1])
				.filter(pxPerCell -> pxPerCell > 0)
				.min()
				.orElse(GameGUIView.PREFERRED_CELL_SIZE);
		size.width = rowsCols[1] * cellSize;
		size.height = rowsCols[0] * cellSize;

		if (titleLabel != null) {
			size.height += titleLabel.getPreferredSize().height;
		}

		return size;
	}

	private void refreshChildren() {
		Dimension size = getSizeForChildren();
		Insets insets = getInsets();

		for (Component layer : getComponents()) {
			layer.setBounds(insets.left, insets.top, size.width, size.height);
		}

		updatePalette(paletteLayerPanel);
		resetCellComponents();
		revalidate();
		repaint();
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
					getWidth()
			);
		}
	}

}
