import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ChapterExercise5_1And5_2 {
	public static void main(String[] args) {
		new TilingFrame();
	}

	private static class TilingFrame extends JFrame {
		private static final int CELL_SIZE = 100;
		private static final int BOARD_SIZE = CELL_SIZE * 4;

		private Rectangle missingSquare = new Rectangle(3, 2, 1, 1);

		public TilingFrame() throws HeadlessException {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
			add(panel);

			JComponent canvasComponent = new Canvas();
			canvasComponent.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
			panel.add(canvasComponent);

			setVisible(true);
			pack();
		}

		private Collection<? extends Collection<Rectangle>> getTrominoRectangles() {
			return new ArrayList<>();
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
	}
}
