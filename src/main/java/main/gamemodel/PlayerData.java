package main.gamemodel;

import main.gamemodel.cells.BoardCell;
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
	private Map<Character, PieceCell> usedPieces = new HashMap<>();
	private Map<Character, PieceCell> deadPieces = new HashMap<>();

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

	public void unuseUsedPiece(PieceCell piece) {
		usedPieces.remove(piece.getId());
		unusedPieces.put(piece.getId(), piece);
	}

	public void killPiece(PieceCell cell) throws InvalidMoveException {
		if (!usedPieces.containsKey(cell.getId()) ||
				deadPieces.containsKey(cell.getId())) {
			throw new InvalidMoveException("Cell is not alive or is not used");
		}

		usedPieces.remove(cell.getId());
		deadPieces.put(cell.getId(), cell);
	}

	public void revivePiece(PieceCell cell) throws InvalidMoveException {
		if (!deadPieces.containsKey(cell.getId()) ||
				usedPieces.containsKey(cell.getId())) {
			throw new InvalidMoveException("Cell is not dead");
		}

		deadPieces.remove(cell.getId());
		usedPieces.put(cell.getId(), cell);
	}

	/**
	 * Finds a piece that is on the board
	 * May return null
	 */
	public PieceCell findUsedPiece(char pieceID) {
		pieceID = ensureCase(pieceID);
		return usedPieces.get(pieceID);
	}

	public PieceCell findUnusedPiece(char pieceID) {
		pieceID = ensureCase(pieceID);
		return unusedPieces.get(pieceID);
	}

	public String getName() {
		return playerCell.getName();
	}

	public boolean ownsPiece(BoardCell cell) {
		if (cell == playerCell) {
			return true;
		}

		return ensureCase(cell.getId()) == cell.getId();
	}

	private char ensureCase(char pieceID) {
		if (isUppercase) {
			return Character.toUpperCase(pieceID);
		} else {
			return Character.toLowerCase(pieceID);
		}
	}
}
