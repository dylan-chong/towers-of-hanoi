package main;

import main.PieceCell.SideCombination;

import java.util.*;

public class GameModel implements TextualRepresentable {

	private final Board board;
	private final List<PlayerData> playerData;

	/**
	 * Index in playerData
	 */
	private int currentPlayerIndex;

	public GameModel(Board emptyBoard) {
		assert emptyBoard.isEmpty();

		this.board = emptyBoard;
		this.playerData = Arrays.asList(
				new PlayerData(new PlayerCell(PlayerCell.Token.ANGRY), true),
				new PlayerData(new PlayerCell(PlayerCell.Token.HAPPY), false)
		);
		this.currentPlayerIndex = 0;

		setupPlayers();
	}

	private PlayerData getCurrentPlayerData() {
		return playerData.get(currentPlayerIndex);
	}

	private void setupPlayers() {
		board.addCell(playerData.get(0).getPlayerCell(), 1, 1);
		board.addCell(
				playerData.get(1).getPlayerCell(),
				board.getNumRows() - 2,
				board.getNumCols() - 2
		);
	}

	@Override
	public char[][] toTextualRep() {
		return board.toTextualRep();
	}

	/**
	 * Create a piece on the current player's creation square
	 * @param pieceID a,b,c,... the piece to get from
	 * {@link PlayerData#unusedPieces}
	 * @param orientation 0/90/180/270
	 */
	public void create(char pieceID, int orientation) {

	}

	private class PlayerData {
		private final PlayerCell playerCell;
		private final boolean isUppercase;

		/**
		 * Pieces that haven't been placed on the board yet
		 */
		private Map<Character, PieceCell> unusedPieces;

		public PlayerData(PlayerCell playerCell, boolean isUppercase) {
			this.playerCell = playerCell;
			this.isUppercase = isUppercase;

			unusedPieces = new HashMap<>();
			for (int i = 0; i < SideCombination.values().length; i++) {
				SideCombination sides = SideCombination.values()[i];
				char id = (char) ('a' + i);
				if (isUppercase) {
					id = Character.toUpperCase(id);
				}

				PieceCell pieceCell = new PieceCell(id, sides);
				unusedPieces.put(id, pieceCell);
			}
		}

		public PlayerCell getPlayerCell() {
			return playerCell;
		}
	}
}
