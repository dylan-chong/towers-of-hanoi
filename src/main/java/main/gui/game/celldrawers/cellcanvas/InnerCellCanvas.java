package main.gui.game.celldrawers.cellcanvas;

import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLTransition;
import main.gui.game.GameGUIView;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actually does the drawing
 */
class InnerCellCanvas extends SLPanel {

	private CellCanvas cellCanvas;
	private final Queue<SLTransition> transitionsToDo = new ConcurrentLinkedQueue<>();

	private final AtomicBoolean isAnimating = new AtomicBoolean(false);

	public InnerCellCanvas(CellCanvas cellCanvas) {
		this.cellCanvas = cellCanvas;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		cellCanvas.paintCanvasComponent(((Graphics2D) g));
	}

	@Override
	public Dimension getPreferredSize() {
		int[] preferredRowsCols = cellCanvas.calculatePreferredRowsCols();
		return new Dimension(
				preferredRowsCols[1] * GameGUIView.PREFERRED_BOARD_CELL_SIZE,
				preferredRowsCols[0] * GameGUIView.PREFERRED_BOARD_CELL_SIZE
		);
	}

	public void queueTransition(SLKeyframe keyframe) {
		SLTransition animation = createTransition().push(
				keyframe.setCallback(this::animationFinish)
		);
		transitionsToDo.add(animation);

		startNextAnimation();
	}

	private void animationFinish() {
		isAnimating.set(false);
		startNextAnimation();
	}

	private synchronized void startNextAnimation() {
		if (isAnimating.get() || transitionsToDo.peek() == null) {
			return;
		}

		isAnimating.set(true);

		SLTransition transition = transitionsToDo.poll();
		SwingUtilities.invokeLater(transition::play);
	}
}
