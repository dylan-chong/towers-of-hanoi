import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Tromino {
	public static Tromino fromSquares(Collection<Point> squareCorners) {
		if (squareCorners.size() != 3) {
			throw new IllegalArgumentException();
		}

		Point centre = centreOf(squareCorners);

		List<Point> shiftedCorners = squareCorners.stream()
				.map(p -> new Point(p.x - centre.x, p.y - centre.y))
				.collect(Collectors.toList());
		assert centreOf(shiftedCorners).equals(new Point());
		Rotation rotation = Rotation.valueOf(shiftedCorners);

		return new Tromino(rotation, centre);
	}

	private static Point centreOf(Collection<Point> squares1x1) {
		if (squares1x1.isEmpty()) {
			throw new IllegalArgumentException();
		}

		Point botLeft = null;
		Point topRight = null;
		for (Point corner : squares1x1) {
			if (botLeft == null) {
				botLeft = new Point(corner);
				topRight = new Point(corner);
				continue;
			}

			botLeft.x = Math.min(botLeft.x, corner.x);
			botLeft.y = Math.min(botLeft.y, corner.y);
			topRight.x = Math.max(topRight.x, corner.x + 1);
			topRight.y = Math.max(topRight.y, corner.y + 1);
		}

		return new Point(
				Math.round((botLeft.x + topRight.x) / 2f),
				Math.round((botLeft.y + topRight.y) / 2f)
		);
	}

	private final Rotation rotation;
	private final Point centre;

	public Tromino(Rotation rotation, Point centre) {
		this.rotation = rotation;
		this.centre = centre;
	}

	public Collection<Rectangle> getRectangles() {
		Collection<Point> corners = rotation.getThreeCorners();
		for (Point corner : corners) {
			corner.translate(centre.x, centre.y);
		}

		return corners.stream()
				.map(corner -> new Rectangle(corner, new Dimension(1, 1)))
				.collect(Collectors.toList());
	}

	public enum Rotation {
		UPPER_LEFT {
			@Override
			public Collection<Point> getThreeCorners() {
				return Arrays.asList(
						new Point(-1, -1),
						new Point(-1, 0),
						new Point(0, 0)
				);
			}
		},
		LOWER_LEFT {
			@Override
			public Collection<Point> getThreeCorners() {
				return Arrays.asList(
						new Point(0, -1),
						new Point(-1, -1),
						new Point(-1, 0)
				);
			}
		},
		UPPER_RIGHT {
			@Override
			public Collection<Point> getThreeCorners() {
				return Arrays.asList(
						new Point(0, -1),
						new Point(-1, 0),
						new Point(0, 0)
				);
			}
		},
		LOWER_RIGHT {
			@Override
			public Collection<Point> getThreeCorners() {
				return Arrays.asList(
						new Point(0, -1),
						new Point(-1, -1),
						new Point(0, 0)
				);
			}
		},
		;

		public abstract Collection<Point> getThreeCorners();

		public static Rotation valueOf(Collection<Point> threeCorners) {
			Set<Point> argumentCorners = new HashSet<>(threeCorners);

			if (threeCorners.size() != 3) {
				throw new IllegalArgumentException();
			}

			for (Rotation rotation : values()) {
				Set<Point> rotationCorners = new HashSet<>(rotation.getThreeCorners());
				if (argumentCorners.equals(rotationCorners)) {
					return rotation;
				}
			}

			throw new IllegalArgumentException();
		}
	}
}
