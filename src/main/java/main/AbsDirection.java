package main;

import java.util.Arrays;

/**
 * An absolute direction - a direction that is not relative to anything
 * (as opposed to a relative direction)
 */
public enum AbsDirection {
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
	NORTH("up") {
		@Override
		public int[] shift(int[] rowCol) {
			return new int[]{
					rowCol[0] - 1,
					rowCol[1],
			};
		}
	},
	;

	public static AbsDirection valueOfAlternateName(String alternateName)
			throws InvalidMoveException {
		return Arrays.stream(values())
				.filter(direction -> direction.alternateName.equals(alternateName))
				.findAny()
				.orElseThrow(() -> new InvalidMoveException(
						"Invalid direction name: " + alternateName
				));
	}

	private final String alternateName;

	AbsDirection(String alternateName) {
		this.alternateName = alternateName;
	}

	public String getAlternateName() {
		return alternateName;
	}

	public abstract int[] shift(int[] rowCol);
}
