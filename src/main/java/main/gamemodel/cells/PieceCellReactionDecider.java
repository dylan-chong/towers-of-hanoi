package main.gamemodel.cells;

import main.gamemodel.cells.PieceCell.SideType;
import main.gamemodel.cells.PieceCell.SideType.Mapper;

public class PieceCellReactionDecider {

	/**
	 * @param otherCellSide The side of another cell that is touching this
	 *                      cell's side (thisSide)
	 * @return The reaction that this cell should do
	 */
	public static Reaction getReaction(SideType thisSide, SideType otherCellSide) {
		Mapper<Reaction> thisSideMapper = otherCellSide.getFromMap(new Decider());
		return thisSide.getFromMap(thisSideMapper);
	}

	/**
	 * The outer Mapper that is for otherCellSide
	 * The inner Mapper is for thisSide
	 */
	private static class Decider implements Mapper<Mapper<Reaction>> {
		@Override
		public Mapper<Reaction> getEmptyValue() { // otherCellSide is empty
			return new SideType.Mapper<Reaction>() {
				@Override
				public Reaction getEmptyValue() {
					return Reaction.DO_NOTHING;
				}

				@Override
				public Reaction getSwordValue() { // thisSide is a sword
					return Reaction.DO_NOTHING;
				}

				@Override
				public Reaction getShieldValue() {
					return Reaction.DO_NOTHING;
				}
			};
		}

		@Override
		public Mapper<Reaction> getSwordValue() {
			return new SideType.Mapper<Reaction>() {
				@Override
				public Reaction getEmptyValue() {
					return Reaction.GET_KILLED;
				}

				@Override
				public Reaction getSwordValue() {
					return Reaction.GET_KILLED;
				}

				@Override
				public Reaction getShieldValue() {
					return Reaction.DO_NOTHING;
				}
			};
		}

		@Override
		public Mapper<Reaction> getShieldValue() {
			return new SideType.Mapper<Reaction>() {
				@Override
				public Reaction getEmptyValue() {
					return Reaction.DO_NOTHING;
				}

				@Override
				public Reaction getSwordValue() {
					return Reaction.GET_BUMPED_BACK;
				}

				@Override
				public Reaction getShieldValue() {
					return Reaction.DO_NOTHING;
				}
			};
		}
	}
}
