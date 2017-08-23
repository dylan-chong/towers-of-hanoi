package main.gamemodel;

import java.util.Arrays;

/**
 * An absolute direction - a direction that is not relative to anything
 * (as opposed to a relative direction)
 */
public enum Direction {
	NORTH("up") {
		@Override
		public float[] shift(float[] rowCol, float amount) {
			return new float[]{
					rowCol[0] - amount,
					rowCol[1],
			};
		}
	},
	EAST("right") {
		@Override
		public float[] shift(float[] rowCol, float amount) {
			return new float[]{
					rowCol[0],
					rowCol[1] + amount,
			};
		}
	},
	SOUTH("down") {
		@Override
		public float[] shift(float[] rowCol, float amount) {
			return new float[]{
					rowCol[0] + amount,
					rowCol[1],
			};
		}
	},
	WEST("left") {
		@Override
		public float[] shift(float[] rowCol, float amount) {
			return new float[]{
					rowCol[0],
					rowCol[1] - amount,
			};
		}
	},
	;

	public static Direction valueOfAlternateName(String alternateName) {
		return Arrays.stream(values())
				.filter(direction -> direction.alternateName.equals(alternateName))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(
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

	public static Direction fromAToB(int[] rowColA, int[] rowColB) {
		for (Direction direction : values()) {
			int[] shiftedA = direction.shift(rowColA);
			if (Arrays.equals(shiftedA, rowColB)) {
				return direction;
			}
		}

		throw new IllegalArgumentException("Points are not touching");
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

	public Direction reversed() {
		return values()[(ordinal() + 2) % values().length];
	}

	public int[] shift(int[] rowCol) {
		float[] shifted = shift(new float[]{
				rowCol[0],
				rowCol[1]
		}, 1);

		return new int[]{
				(int) shifted[0],
				(int) shifted[1]
		};
	}

	public abstract float[] shift(float[] rowCol, float amount);
}
