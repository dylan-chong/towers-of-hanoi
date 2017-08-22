package main.gamemodel.cells;

public interface BoardCellGenericGetter<ReturnT> {
	default ReturnT getFrom(BoardCell cell) {
		return cell.accept(this);
	}

	ReturnT visitPieceCell(PieceCell cell);
	ReturnT visitPlayerCell(PlayerCell cell);
}
