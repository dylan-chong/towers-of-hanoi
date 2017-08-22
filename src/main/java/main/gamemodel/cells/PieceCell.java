package main.gamemodel.cells;

import main.gamemodel.Direction;
import main.gamemodel.Textable;

import java.util.*;

/**
 * Represents a cell with sword/shield parts
 */
public class PieceCell extends BoardCell {
	/**
	 * These assume the piece is facing north
	 */
	private final SideCombination sides;
	private final char id;

	private Direction direction;

	public PieceCell(char id, SideCombination sides) {
		this(id, sides, Direction.NORTH);
	}

	public PieceCell(char id, SideCombination sides, Direction direction) {
		this.id = id;
		this.sides = sides;
		this.direction = direction;
	}

	@Override
	public char[][] toTextualRep() {
		char[][] representation = blankCellTextualRep();
		representation[1][1] = id; // middle

		representation[0][1] = getSide(Direction.NORTH).toTextualRep(Direction.NORTH);
		representation[1][2] = getSide(Direction.EAST).toTextualRep(Direction.EAST);
		representation[2][1] = getSide(Direction.SOUTH).toTextualRep(Direction.SOUTH);
		representation[1][0] = getSide(Direction.WEST).toTextualRep(Direction.WEST);

		return representation;
	}

	@Override
	public char getId() {
		return id;
	}

	@Override
	public <ReturnT> ReturnT accept(BoardCellGenericGetter<ReturnT> getter) {
		return getter.visitPieceCell(this);
	}

	@Override
	protected Reaction getReactionToPieceCell(PieceCell cell, Direction fromThisToCell) {
		SideType thisSide = getSide(fromThisToCell);
		SideType cellSide = cell.getSide(fromThisToCell.reversed());

		return PieceCellReactionDecider.getReaction(thisSide, cellSide);
	}

	@Override
	protected Reaction getReactionToPlayerCell(PlayerCell cell, Direction fromThisToCell) {
		return Reaction.DO_NOTHING;
	}

	@Override
	Reaction getReactionToByVisiting(BoardCell cell, Direction fromCellToThis) {
		return cell.getReactionToPieceCell(this, fromCellToThis);
	}

	/**
	 * @param newAbsoluteDirection This will effectively become the north of
	 *                             this.sides
	 */
	public void setDirection(Direction newAbsoluteDirection) {
		this.direction = newAbsoluteDirection;
	}

	public void rotateClockwise() {
		int nextOrdinal = (direction.ordinal() + 1) % Direction.values().length;
		direction = Direction.values()[nextOrdinal];
	}

	public SideType getSide(Direction absoluteDirection) {
		int relativeSideOrdinal = (absoluteDirection.ordinal()
				- this.direction.ordinal() + Direction.values().length)
						% Direction.values().length;

		// Direction that is relative to this.sides' north
		Direction relativeDirection = Direction.values()[relativeSideOrdinal];

		return sides.getSide(relativeDirection);
	}

	/**
	 * Gets the side of this piece that is touching the other piece
	 * @param thisRowCol Position of this (e.g. on a board)
	 * @param otherRowCol Position of the other piece (e.g. on a board)
	 */
	public SideType getTouchingSide(int[] thisRowCol,
									int[] otherRowCol) {
		Direction dirToOtherPiece = Direction.fromAToB(thisRowCol, otherRowCol);
		return getSide(dirToOtherPiece);
	}

	/**
	 * The different amount of combinations of sword and shield
	 *
	 * Naming order: west, north, south, east
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

		private final Map<Direction, SideType> relativeDirectionToSideType;

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

			SideType westSide = sideTypes.get(0);
			SideType northSide = sideTypes.get(1);
			SideType southSide = sideTypes.get(2);
			SideType eastSide = sideTypes.get(3);

			Map<Direction, SideType> sidesMap = new HashMap<>();
			sidesMap.put(Direction.NORTH, northSide);
			sidesMap.put(Direction.EAST, eastSide);
			sidesMap.put(Direction.SOUTH, southSide);
			sidesMap.put(Direction.WEST, westSide);
			relativeDirectionToSideType = Collections.unmodifiableMap(sidesMap);
		}

		public SideType getSide(Direction relativeDirection) {
			return relativeDirectionToSideType.get(relativeDirection);
		}
	}

	/**
	 * The different possibilities for each side (90 degrees) of the piece
	 */
	public enum SideType {
		EMPTY {
			@Override
			public char toTextualRep(Direction absoluteDirection) {
				return Textable.BLANK_CELL_TEXT_REP_CHAR;
			}

			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getEmptyValue();
			}
		},
		SWORD {
			@Override
			public char toTextualRep(Direction absoluteDirection) {
				if (absoluteDirection == Direction.NORTH ||
						absoluteDirection == Direction.SOUTH) {
					return '|';
				}
				return '-';
			}

			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getSwordValue();
			}
		},
		SHIELD {
			@Override
			public char toTextualRep(Direction absoluteDirection) {
				return '#';
			}

			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getShieldValue();
			}
		},
		;

		public abstract char toTextualRep(Direction absoluteDirection);

		public abstract <T> T getFromMap(Mapper<T> mapper);

		public interface Mapper<ValueT> {
			ValueT getEmptyValue();
			ValueT getSwordValue();
			ValueT getShieldValue();
		}
	}
}
