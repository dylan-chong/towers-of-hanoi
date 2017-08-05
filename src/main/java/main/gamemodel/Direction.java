package main.gamemodel;

import java.util.Arrays;

/**
 * An absolute direction - a direction that is not relative to anything
 * (as opposed to a relative direction)
 */
public enum Direction {
	NORTH("up") {
		@Override
		public int[] shift(int[] rowCol) {
			return new int[]{
					rowCol[0] - 1,
					rowCol[1],
			};
		}
	},
	EAST("right") {
		@Override
		public int[] shift(int[] rowCol) {
			return new int[]{
					rowCol[0],
					rowCol[1] + 1,
			};
		}
	},
	SOUTH("down") {
		@Override
		public int[] shift(int[] rowCol) {
			return new int[]{
					rowCol[0] + 1,
					rowCol[1],
			};
		}
	},
	WEST("left") {
		@Override
		public int[] shift(int[] rowCol) {
			return new int[]{
					rowCol[0],
					rowCol[1] - 1,
			};
		}
	},
	;

	public static Direction valueOfAlternateName(String alternateName)
			throws InvalidMoveException {
		return Arrays.stream(values())
				.filter(direction -> direction.alternateName.equals(alternateName))
				.findAny()
				.orElseThrow(() -> new InvalidMoveException(
						"Invalid direction name: " + alternateName
				));
	}

	public static int clockwiseRotationsFromDegrees(int degrees) {
		if (!isValidDegrees(degrees)) {
			throw new IllegalArgumentException("Invalid degrees: " + degrees);
		}
		return degrees / 90;
	}

	public static boolean isValidDegrees(int degrees) {
		return Arrays.stream(values())
				.map(Direction::degrees)
				.anyMatch(dirDegrees -> dirDegrees == degrees);
	}

	private final String alternateName;

	Direction(String alternateName) {
		this.alternateName = alternateName;
	}

	public String getAlternateName() {
		return alternateName;
	}

	public int degrees() {
		return ordinal() * 90;
	}

	public abstract int[] shift(int[] rowCol);
}