package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cell with sword/shield parts
 */
public class Piece extends BoardCell {
	/**
	 * These are relative to the piece's direction
	 *
	 * TODO this will break after adding rotation
	 */
	private final SideCombination sides;

	private final char id;

	public Piece(char id, SideCombination sides, int row, int col) {
		super(row, col);
		this.id = id;
		this.sides = sides;
	}

	@Override
	public char[][] toTextualRep() {
		char[][] representation = blankTextualRep();
		representation[1][1] = id; // middle

		representation[1][0] = sides.left.toTextualRep(AbsDirection.WEST);
		representation[0][1] = sides.up.toTextualRep(AbsDirection.NORTH);
		representation[1][2] = sides.right.toTextualRep(AbsDirection.EAST);
		representation[2][1] = sides.down.toTextualRep(AbsDirection.SOUTH);

		return representation;
	}

	/**
	 * The different amount of combinations of sword and shield
	 *
	 * Naming order: left, up, down, right.
	 * {@link SideType} in the name of each value for conciseness.
	 */
	public enum SideCombination {
		// TODO NEXT ALL COMBOX
		// TODO AFTER board (class) and printing
		EMPTY_EMPTY_EMPTY_EMPTY, // 4 empty sides
		SWORD_SWORD_SWORD_SHIELD,
		SWORD_SWORD_SHIELD_SHIELD,
		SWORD_SWORD_SWORD_EMPTY,
		;

		public final SideType left, up, down, right;

		SideCombination() {
			String name = this.name().toUpperCase();
			// Match the name with the sides
			List<SideType> sideTypes = new ArrayList<>();
			for (String sidePattern : name.split("_")) {
				sideTypes.add(SideType.valueOf(sidePattern));
			}

			this.left = sideTypes.get(0);
			this.up = sideTypes.get(1);
			this.down = sideTypes.get(2);
			this.right = sideTypes.get(3);
		}
	}

	/**
	 * The different possibilities for each side (90 degrees) of the piece
	 */
	public enum SideType {
		EMPTY {
			@Override
			public char toTextualRep(AbsDirection direction) {
				return ' ';
			}
		},
		SWORD {
			@Override
			public char toTextualRep(AbsDirection direction) {
				if (direction == AbsDirection.NORTH ||
						direction == AbsDirection.SOUTH) {
					return '|';
				}
				return '-';
			}
		},
		SHIELD {
			@Override
			public char toTextualRep(AbsDirection direction) {
				return '#';
			}
		},
		;

		public abstract char toTextualRep(AbsDirection direction);
	}
}
