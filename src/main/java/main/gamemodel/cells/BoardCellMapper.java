package main.gamemodel.cells;

public interface BoardCellMapper<ReturnT> {
	default ReturnT valueOf(BoardCell cell) {
		return cell.getValue(this);
	}

	ReturnT valueOfPieceCell(PieceCell cell);
	ReturnT valueOfPlayerCell(PlayerCell cell);
}
