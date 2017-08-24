package main.gamemodel;

import main.gamemodel.cells.BoardCell;
import main.gamemodel.cells.Reaction;

import java.util.function.Function;

public class ReactionData {
	public final BoardCell cell;
	public final int[] cellRowCol;
	public final BoardCell cellReactedTo;
	public final int[] cellReactedToRowCol;
	public final Reaction reaction;
	public final Player cellPlayer;

	public ReactionData(BoardCell cell,
						int[] cellRowCol,
						BoardCell cellReactedTo,
						int[] cellReactedToRowCol,
						Player cellPlayer) {
		this.cell = cell;
		this.cellRowCol = cellRowCol;
		this.cellReactedTo = cellReactedTo;
		this.cellReactedToRowCol = cellReactedToRowCol;
		this.cellPlayer = cellPlayer;
		this.reaction = cell.getReactionTo(
				cellReactedTo,
				Direction.fromAToB(cellRowCol, cellReactedToRowCol)
		);
	}

	public static class Pair {
		public final ReactionData dataA;
		public final ReactionData dataB;

		private final Board.CellPair cellPair;

		public Pair(Board.CellPair cellPair,
					Function<BoardCell, int[]> getRowColOfCell,
					Function<BoardCell, Player> getPlayerOfCell) {
			this.cellPair = cellPair;
			BoardCell cellA = cellPair.cellA;
			BoardCell cellB = cellPair.cellB;

			int[] cellARowCol = getRowColOfCell.apply(cellA);
			int[] cellBRowCol = getRowColOfCell.apply(cellB);

			dataA = new ReactionData(
					cellA, cellARowCol, cellB, cellBRowCol,
					getPlayerOfCell.apply(cellA)
			);
			dataB = new ReactionData(
					cellB, cellBRowCol, cellA, cellARowCol,
					getPlayerOfCell.apply(cellB)
			);
		}

		/**
		 * @return true iff there is no point counting this reaction (see code)
		 */
		public boolean isBlankReaction() {
			return dataA.reaction == Reaction.DO_NOTHING &&
					dataB.reaction == Reaction.DO_NOTHING;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Pair pair = (Pair) o;
			return pair.cellPair.equals(cellPair);
		}

		@Override
		public int hashCode() {
			return cellPair.hashCode();
		}
	}

}
