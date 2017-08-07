import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
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
							"x: %d,\n y: %d,\n n: %d",
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

		// Find the subBoard that contains missingSquare
		List<Board> missingSquareBoards = subBoards.stream()
				.filter(board -> board.asRectangle().contains(missingSquare))
				.collect(Collectors.toList());
		assert missingSquareBoards.size() == 1;
		missingSquareBoards.get(0).missingSquare = missingSquare;

		// Find the middle tromino
		List<Point> middleMissingSquares = subBoards.stream()
				.filter(b -> b != missingSquareBoards.get(0))
				.map(b -> b.missingSquare)
				.collect(Collectors.toList());
		Set<Tromino> trominos = new HashSet<>();
		trominos.add(Tromino.fromSquares(middleMissingSquares));

		// Do recursion
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
				new Board(startX, startY, n2, new Point(midX - 1, midY - 1)),
				// lower right
				new Board(midX, startY, n2, new Point(midX, midY - 1)),
				// upper right
				new Board(midX, midY, n2, new Point(midX, midY)),
				// upper left
				new Board(startX, midY, n2, new Point(midX - 1, midY))
		));
	}

	private Rectangle asRectangle() {
		return new Rectangle(startX, startY, n, n);
	}
}

