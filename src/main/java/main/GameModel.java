package main;

import main.PieceCell.SideCombination;

import java.util.*;

public class GameModel implements Textable {

	/**
	 * The offsets from the corners of the board
	 */
	private static final int PLAYER_CELL_OFFSET = 1;
	private static final int CREATION_CELL_OFFSET = 2;

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
				new PlayerData(
						new PlayerCell(PlayerCell.Token.ANGRY),
						true,
						CREATION_CELL_OFFSET,
						CREATION_CELL_OFFSET
				),
				new PlayerData(
						new PlayerCell(PlayerCell.Token.HAPPY),
						false,
						emptyBoard.getNumRows() - 1 - CREATION_CELL_OFFSET,
						emptyBoard.getNumCols() - 1 - CREATION_CELL_OFFSET
				)
		);
		this.currentPlayerIndex = 0;

		setupPlayers();
	}

	private PlayerData getCurrentPlayerData() {
		return playerData.get(currentPlayerIndex);
	}

	private void setupPlayers() {
		board.addCell(
				playerData.get(0).getPlayerCell(),
				PLAYER_CELL_OFFSET,
				PLAYER_CELL_OFFSET
		);
		board.addCell(
				playerData.get(1).getPlayerCell(),
				board.getNumRows() - 1 - PLAYER_CELL_OFFSET,
				board.getNumCols() - 1 - PLAYER_CELL_OFFSET
		);
	}

	@Override
	public char[][] toTextualRep() {
		return board.toTextualRep();
	}

	/**
	 * Create a piece on the current player's creation square
	 * @param pieceID a,b,c,... the piece to get from. Case insensitive.
	 * {@link PlayerData#unusedPieces}
	 * @param orientation 0/90/180/270
	 */
	public void create(char pieceID, int orientation) throws InvalidMoveException {
		PlayerData player = getCurrentPlayerData();

		if (player.isUppercase) {
			pieceID = Character.toUpperCase(pieceID);
		} else {
			pieceID = Character.toLowerCase(pieceID);
		}

		BoardCell existingCell = board.getCellAt(
				player.creationRow, player.creationCol
		);
		if (existingCell != null) {
			throw new InvalidMoveException(
					"There is a cell in your creation square"
			);
		}

		PieceCell newCell = player.unusedPieces.get(pieceID);
		if (newCell == null) {
			throw new InvalidMoveException(
					"That cell has already been created, or does not exist " +
							"on this player"
			);
		}

		board.addCell(
				newCell,
				player.creationRow, player.creationCol
		);

// TODO: orientation
	}

	private class PlayerData {
		private final PlayerCell playerCell;
		private final boolean isUppercase;
		private final int creationRow;
		private final int creationCol;

		/**
		 * Pieces that haven't been placed on the board yet
		 */
		private Map<Character, PieceCell> unusedPieces;

		public PlayerData(PlayerCell playerCell,
						  boolean isUppercase,
						  int creationRow,
						  int creationCol) {
			this.playerCell = playerCell;
			this.isUppercase = isUppercase;
			this.creationRow = creationRow;
			this.creationCol = creationCol;

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
