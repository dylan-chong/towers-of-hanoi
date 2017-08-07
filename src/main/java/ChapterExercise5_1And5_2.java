import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @see TilingFrame.Board#getTessellatedTiles() for the tiling algorithm
 */
public class ChapterExercise5_1And5_2 {
	public static void main(String[] args) {
		new TilingFrame();
	}

	private static class TilingFrame extends JFrame {
		private static final int CELL_SIZE = 100;
		private static final int NUMBER_OF_CELLS = 4;
		private static final int BOARD_SIZE = CELL_SIZE * NUMBER_OF_CELLS;

		private Rectangle missingSquare = new Rectangle(3, 2, 1, 1);

		public TilingFrame() throws HeadlessException {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
			add(panel);

			JComponent canvasComponent = new Canvas();
			canvasComponent.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
			panel.add(canvasComponent);

			JButton randomiseButton = new JButton("Randomise missing square");
			randomiseButton.addActionListener(e -> randomiseMissingSquare());
			panel.add(randomiseButton);

			setVisible(true);
			pack();
		}

		private void randomiseMissingSquare() {
			missingSquare = new Rectangle(
					(int) (Math.random() * NUMBER_OF_CELLS),
					(int) (Math.random() * NUMBER_OF_CELLS),
					1, 1
			);
			repaint();
		}

		private Collection<? extends Collection<Rectangle>> getTrominoRectangles() {
			return new Board(0, 0, NUMBER_OF_CELLS, missingSquare.getLocation())
					.getTessellatedTiles()
					.stream()
					.map(Tromino::getRectangles)
					.collect(Collectors.toList());
		}

		private class Canvas extends JComponent {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g;
				g2d.draw(new Rectangle(BOARD_SIZE, BOARD_SIZE)); // border

				paintRectangleGroup(Arrays.asList(missingSquare), g2d);
				for (Collection<Rectangle> rectangles : getTrominoRectangles()) {
					paintRectangleGroup(rectangles, g2d);
				}
			}

			private void paintRectangleGroup(
					Collection<Rectangle> rectangles,
					Graphics2D g2d
			) {
				g2d.setColor(new Color(
						0,
						(float) Math.random(),
						(float) Math.random()
				));
				for (Rectangle rectangle : rectangles) {
					int x = rectangle.x * CELL_SIZE;
					int y = BOARD_SIZE - (rectangle.y + 1) * CELL_SIZE;
					int w = rectangle.width * CELL_SIZE;
					int h = rectangle.height * CELL_SIZE;
					g2d.fill(new Rectangle(x, y, w, h));
				}
			}
		}

		private static class Board {
			private int startX;
			private int startY;
			private int n; // width and height
			private Point missingSquare;

			public Board(int startX, int startY, int n, Point missingSquare) {
				this.startX = startX;
				this.startY = startY;
				this.n = n;
				this.missingSquare = missingSquare;
			}

			/**
			 * The tiling algorithm
			 */
			public Collection<Tromino> getTessellatedTiles() {
				if (!asRectangle().contains(missingSquare)) {
					throw new IllegalArgumentException(String.format(
							"\n" +
									"ms: %s,\n" +
									"x: %d\n, y: %d\n, n: %d",
							missingSquare,
							startX,
							startY,
							n
					));
				}

				if (n == 2) {
					List<Point> squareCorners = new ArrayList<>(Arrays.asList(
							new Point(startX, startY),
							new Point(startX + 1, startY),
							new Point(startX + 1, startY + 1),
							new Point(startX, startY + 1)
					));
					squareCorners.remove(missingSquare);
					Tromino tromino = Tromino.fromSquares(squareCorners);
					return Arrays.asList(tromino);
				}

				List<Board> subBoards = getSubBoards();
				List<Board> missingSquareBoards = subBoards.stream()
						.filter(board -> board.asRectangle().contains(missingSquare))
						.collect(Collectors.toList());
				assert missingSquareBoards.size() == 1;
				missingSquareBoards.get(0).missingSquare = missingSquare;

				Set<Tromino> trominos = new HashSet<>();
				subBoards.stream()
						.map(Board::getTessellatedTiles)
						.forEach(trominos::addAll);
				return trominos;
			}

			private List<Board> getSubBoards() {
				if (n < 2) {
					throw new IllegalStateException();
				}

				int n2 = n / 2;
				int midX = startX + n2;
				int midY = startY + n2;
				return new ArrayList<>(Arrays.asList(
						// lower left
						new Board(0, 0, n2, new Point(midX - 1, midY - 1)),
						// lower right
						new Board(midX, 0, n2, new Point(midX, midY - 1)),
						// upper right
						new Board(midX, midY, n2, new Point(midX, midY)),
						// upper left
						new Board(0, midY, n2, new Point(midX - 1, midY))
				));
			}

			private Rectangle asRectangle() {
				return new Rectangle(startX, startY, n, n);
			}
		}

	}
}
