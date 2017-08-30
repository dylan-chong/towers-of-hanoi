package main.gui.game.celldrawers.cellcanvas;

import main.gamemodel.*;
import main.gui.game.Events.EventGameGUIViewUpdated;
import main.gui.game.Events.EventReactionClicked;
import main.gui.game.GameGUIModel;
import main.gui.game.GameGUIView;
import main.gui.game.celldrawers.CellDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BoardCanvas extends CellCanvas {

	private final Board board;
	private final EventReactionClicked eventReactionClicked;

	public BoardCanvas(
			Board board,
			GameGUIModel gameGUIModel,
			CellDrawer cellDrawer,
			String titleOrNull,
			EventGameGUIViewUpdated eventGameGUIViewUpdated,
			EventReactionClicked eventReactionClicked
	) {
		super(gameGUIModel, cellDrawer, titleOrNull, eventGameGUIViewUpdated);
		this.board = board;
		this.eventReactionClicked = eventReactionClicked;
	}

	@Override
	protected void paintCanvasComponent(Graphics2D graphics2D) {
		// Draw chequered grid
		for (int r = 0; r < board.getNumRows(); r++) {
			boolean isDark = r % 2 == 0;
			for (int c = 0; c < board.getNumCols(); c++) {
				fillSquare(r, c, graphics2D, isDark ? Color.GRAY : Color.WHITE);
				isDark = !isDark;
			}
		}

		drawCreationSquares(graphics2D);

		super.paintCanvasComponent(graphics2D);
	}

	@Override
	protected void forEachCell(CellConsumer cellConsumer) {
		board.forEachCell(cellConsumer);
	}

	@Override
	protected int[] minRowsCols() {
		return new int[] {
				board.getNumRows(),
				board.getNumCols()
		};
	}

	@Override
	protected void updatePalette(JPanel paletteLayerPanel) {
		super.updatePalette(paletteLayerPanel);

		Set<ReactionData.Pair> reactions;
		if (gameGUIModel.getGameModel().getTurnState() == TurnState.RESOLVING_REACTIONS) {
			reactions = new HashSet<>(gameGUIModel
					.getGameModel()
					.getReactions());
		} else {
			reactions = Collections.emptySet();
		}

		// TODO SOMETIME check equals

		Arrays.stream(paletteLayerPanel.getComponents())
				.filter(ReactionHighlighter.class::isInstance)
				.forEach(paletteLayerPanel::remove);

		reactions.stream()
				.map(reaction -> new ReactionHighlighter(
						reaction, paletteLayerPanel.getInsets()
				))
				.forEach(paletteLayerPanel::add);
	}

	private void drawCreationSquares(Graphics2D graphics2D) {
		List<Color> playerColours = gameGUIModel.getGameModel()
				.getPlayers()
				.stream()
				.map(p -> p.getPlayerCell()
						.getToken()
						.color
						.darker()
				)
				.collect(Collectors.toList());
		int rows = board.getNumRows();
		int cols = board.getNumCols();
		int off = GameModel.CREATION_CELL_OFFSET;

		fillSquare(off, off, graphics2D, playerColours.get(0));
		fillSquare(rows - off - 1, cols - off - 1, graphics2D, playerColours.get(1));
	}

	private void fillSquare(int r, int c, Graphics2D graphics2D, Color color) {
		int cellSize = GameGUIView.PREFERRED_BOARD_CELL_SIZE;

		graphics2D.setColor(color);
		graphics2D.fillRect(
				r * cellSize,
				c * cellSize,
				cellSize,
				cellSize
		);
	}

	private class ReactionHighlighter extends JComponent {
		private static final int STROKE_WIDTH = 8;
		private final int[] startRC;
		private final int[] endRC;

		private ReactionHighlighter(
				ReactionData.Pair reaction,
				Insets containerInsets
		) {
			startRC = reaction.dataA.cellRowCol;
			endRC = Arrays.stream(reaction.dataB.cellRowCol)
					.map(ordinate -> ordinate + 1)
					.toArray();

			setBounds(
					containerInsets.left + startRC[1] * getCellSize(),
					containerInsets.top + startRC[0] * getCellSize(),
					(endRC[1] - startRC[1]) * getCellSize(),
					(endRC[0] - startRC[0]) * getCellSize()
			);


			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					eventReactionClicked.broadcast(reaction);
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D graphics2D = (Graphics2D) g;
			graphics2D.setColor(new Color(255, 0, 255, 180));
			graphics2D.setStroke(new BasicStroke(STROKE_WIDTH));

			int w = (endRC[1] - startRC[1]);
			int h = (endRC[0] - startRC[0]);
			int off = STROKE_WIDTH;

			graphics2D.drawOval(
					off,
					off,
					w * getCellSize() - (2 * off),
					h * getCellSize() - (2 * off)
			);
		}
	}
}
