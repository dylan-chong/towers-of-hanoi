package main.gamemodel.cells;

import main.gamemodel.AbsDirection;
import main.gamemodel.Textable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cell with sword/shield parts
 */
public class PieceCell extends BoardCell {
	/**
	 * These are relative to the piece's direction
	 *
	 * TODO this will break after adding rotation
	 */
	private final SideCombination sides;

	private final char id;

	public PieceCell(char id, SideCombination sides) {
		this.id = id;
		this.sides = sides;
	}

	@Override
	public char[][] toTextualRep() {
		char[][] representation = blankCellTextualRep();
		representation[1][1] = id; // middle

		representation[1][0] = sides.left.toTextualRep(AbsDirection.WEST);
		representation[0][1] = sides.up.toTextualRep(AbsDirection.NORTH);
		representation[1][2] = sides.right.toTextualRep(AbsDirection.EAST);
		representation[2][1] = sides.down.toTextualRep(AbsDirection.SOUTH);

		return representation;
	}

	public SideCombination getOriginalSide() {
		return sides;
	}

	public char getId() {
		return id;
	}

	/**
	 * The different amount of combinations of sword and shield
	 *
	 * Naming order: left, up, down, right.
	 * {@link SideType} in the name of each value for conciseness.
	 */
	public enum SideCombination {
		// 1st row in assignment diagram
		SWORD_SWORD_SWORD_SHIELD,
		SWORD_SWORD_SWORD_EMPTY,
		SHIELD_SHIELD_SHIELD_SHIELD,
		EMPTY_SWORD_SHIELD_EMPTY,

		// 2nd row
		EMPTY_EMPTY_EMPTY_EMPTY, // 4 empty sides
		SWORD_SWORD_SHIELD_SHIELD,
		SWORD_SWORD_SWORD_SWORD,
		SHIELD_SWORD_SHIELD_EMPTY,
		EMPTY_EMPTY_EMPTY_SHIELD,

		// 3rd row
		SHIELD_SWORD_SWORD_SHIELD,
		SWORD_SWORD_EMPTY_SHIELD,
		EMPTY_SWORD_EMPTY_EMPTY,
		EMPTY_SWORD_SHIELD_SHIELD,
		EMPTY_EMPTY_SHIELD_SHIELD,

		// 4th row
		SHIELD_SWORD_SWORD_EMPTY,
		SWORD_SWORD_SHIELD_EMPTY,
		SHIELD_SWORD_EMPTY_EMPTY,
		SHIELD_SWORD_EMPTY_SHIELD,
		SHIELD_EMPTY_EMPTY_SHIELD,

		// 5th row
		EMPTY_SWORD_SWORD_EMPTY,
		SWORD_SWORD_EMPTY_EMPTY,
		EMPTY_SWORD_EMPTY_SHIELD,
		SHIELD_SWORD_SHIELD_SHIELD,
		SHIELD_EMPTY_SHIELD_SHIELD,
		;

		public final SideType left, up, down, right;

		SideCombination() {
			String name = this.name().toUpperCase();
			// Match the name with the sides
			List<SideType> sideTypes = new ArrayList<>();
			for (String sidePattern : name.split("_")) {
				sideTypes.add(SideType.valueOf(sidePattern));
			}

			if (sideTypes.size() != 4) {
				throw new Error("Invalid SideCombination name");
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
				return Textable.BLANK_CELL_TEXT_REP_CHAR;
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
