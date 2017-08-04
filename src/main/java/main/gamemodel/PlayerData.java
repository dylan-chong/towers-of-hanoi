package main.gamemodel;

import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static main.gamemodel.cells.PieceCell.SideCombination;

/**
 * Represents a single player in the game
 */
public class PlayerData {

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

	public int getCreationRow() {
		return creationRow;
	}

	public int getCreationCol() {
		return creationCol;
	}

	public Collection<Character> getUnusedPieceIds() {
		return Collections.unmodifiableSet(unusedPieces.keySet());
	}

	public Collection<Character> getUsedPieceIds() {
		return Collections.unmodifiableSet(usedPieces.keySet());
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

	public void unusedUsedPiece(PieceCell piece) {
		usedPieces.remove(piece.getId());
		unusedPieces.put(piece.getId(), piece);
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

	public String getName() {
		return playerCell.getName();
	}
}
