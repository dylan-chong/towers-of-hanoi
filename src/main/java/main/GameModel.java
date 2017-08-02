package main;

import main.PieceCell.SideCombination;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		try {
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
		} catch (InvalidMoveException e) {
			throw new Error(e);
		}
	}

	@Override
	public char[][] toTextualRep() {
		return board.toTextualRep();
	}

	/**
	 * Create a piece on the current player's creation square
	 *
	 * @param pieceID     a,b,c,... the piece to get from. Case insensitive.
	 *                    {@link PlayerData#unusedPieces}
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

		board.addCell(
				player.useUnusedPiece(pieceID), // throws
				player.creationRow, player.creationCol
		);

// TODO: orientation
	}

	public void move(char pieceID, AbsDirection direction) throws InvalidMoveException {
		PlayerData player = getCurrentPlayerData();
		PieceCell piece = player.findUsedPiece(pieceID);
		if (piece == null) {
			throw new InvalidMoveException(
					"You do not have a cell on the board under this name"
			);
		}

		int[] position = board.positionOf(piece);
		int[] newPosition = direction.shift(position);

		board.removeCell(position[0], position[1]);
		board.addCell(piece, newPosition[0], newPosition[1]);
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
		/**
		 * Pieces that have been placed on the board, and haven't died
		 */
		private Map<Character, PieceCell> usedPieces;

		public PlayerData(PlayerCell playerCell,
						  boolean isUppercase,
						  int creationRow,
						  int creationCol) {
			this.playerCell = playerCell;
			this.isUppercase = isUppercase;
			this.creationRow = creationRow;
			this.creationCol = creationCol;

			usedPieces = new HashMap<>();

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

		/**
		 * @param pieceID Case insensitive
		 * @return The piece if it is unused, null if the piece is already used
		 */
		public PieceCell useUnusedPiece(char pieceID) throws InvalidMoveException {
			pieceID = ensureCase(pieceID);

			PieceCell newCell = unusedPieces.remove(pieceID);
			if (newCell == null) {
				throw new InvalidMoveException(
						"That cell has already been created, or does not exist " +
								"on this player"
				);
			}

			usedPieces.put(pieceID, newCell);
			return newCell;
		}

		/**
		 * Finds a piece that is on the board
		 * May return null
		 */
		public PieceCell findUsedPiece(char pieceID) {
			pieceID = ensureCase(pieceID);
			return usedPieces.get(pieceID);
		}

		private char ensureCase(char pieceID) {
			if (isUppercase) {
				return Character.toUpperCase(pieceID);
			} else {
				return Character.toLowerCase(pieceID);
			}
		}
	}
}
