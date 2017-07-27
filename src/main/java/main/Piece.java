package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cell with sword/shield parts
 */
public class Piece extends BoardCell {
	/**
	 * These are relative to the piece's direction
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
		representation[1][1] = id;
		return representation;
	}

	/**
	 * The different amount of combinations of sword and shield
	 *
	 * Naming order: left, up, down, right.
	 * {@link SideType} in the name of each value for conciseness.
	 */
	public enum SideCombination {
		EMPTY_EMPTY_EMPTY_EMPTY, // 4 empty sides
		SWORD_SWORD_SWORD_EMPTY;

		private final SideType left, up, down, right;

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
		EMPTY,
		SWORD,
		SHIELD,
	}
}
