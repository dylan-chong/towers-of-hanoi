import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @see Board#getTessellatedTiles() for the tiling algorithm
 */
public class ChapterExercise5_1And5_2 {
	public static void main(String[] args) {
		new TilingFrame();
	}

	private static class TilingFrame extends JFrame {
		private static final int BOARD_SIZE = 600;

		private int n = 4;
		private Rectangle missingSquare;

		public TilingFrame() throws HeadlessException {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			randomiseMissingSquare();

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
			add(panel);

			JComponent canvasComponent = new Canvas();
			canvasComponent.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
			panel.add(canvasComponent);

			JButton randomiseButton = new JButton("Randomise missing square");
			randomiseButton.addActionListener(e -> randomiseMissingSquare());
			panel.add(randomiseButton);

			for (int i = 1; i <= 10; i++) {
				int n = (int) Math.pow(2, i);
				JButton settingsButton = new JButton("n = " + n);
				settingsButton.addActionListener(e -> {
					this.n = n;
					randomiseMissingSquare();
				});
				panel.add(settingsButton);
			}

			setVisible(true);
			pack();
		}

		private float cellSize() {
			return 1f * BOARD_SIZE / n;
		}

		private void randomiseMissingSquare() {
			missingSquare = new Rectangle(
					(int) (Math.random() * n),
					(int) (Math.random() * n),
					1, 1
			);
			repaint();
		}

		private Collection<? extends Collection<Rectangle>> getTrominoRectangles() {
			return new Board(0, 0, n, missingSquare.getLocation())
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
						(float) Math.random(),
						(float) Math.random(),
						(float) Math.random()
				));
				for (Rectangle rectangle : rectangles) {
					int x = Math.round(rectangle.x * cellSize());
					int y = Math.round(BOARD_SIZE - (rectangle.y + 1) * cellSize());
					int w = Math.round(rectangle.width * cellSize());
					int h = Math.round(rectangle.height * cellSize());
					g2d.fill(new Rectangle(x, y, w, h));
				}
			}
		}

	}
}
