package main.gamemodel;

import main.gamemodel.cells.BoardCell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;

import java.util.Arrays;
import java.util.List;

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
	private TurnState turnState = TurnState.CREATING_PIECE;

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

	public PlayerData getCurrentPlayerData() {
		return playerData.get(currentPlayerIndex);
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
	public void create(char pieceID, AbsDirection orientation) throws InvalidMoveException {
		requireState(TurnState.CREATING_PIECE);

		PlayerData player = getCurrentPlayerData();

		BoardCell existingCell = board.getCellAt(
				player.getCreationRow(), player.getCreationCol()
		);
		if (existingCell != null) {
			throw new InvalidMoveException(
					"There is a cell in your creation square"
			);
		}

		board.addCell(
				player.useUnusedPiece(pieceID), // throws
				player.getCreationRow(), player.getCreationCol()
		);

// TODO: orientation

		nextState();
	}

	public void move(char pieceID, AbsDirection direction) throws InvalidMoveException {
		requireState(TurnState.MOVING_OR_ROTATING_PIECE);

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

		nextState();
		//TODO collisions
	}

	public TurnState getTurnState() {
		return turnState;
	}

	public void passTurn() throws InvalidMoveException {
		nextState();
	}

	private void nextState() throws InvalidMoveException {
		if (!turnState.canMoveToNextState(this)) {
			throw new InvalidMoveException("Can't move to the next state");
		}

		int current = turnState.ordinal();
		int next = (current + 1) % TurnState.values().length;
		turnState = TurnState.values()[next];

		if (next < current) {
			currentPlayerIndex++;
			currentPlayerIndex %= playerData.size();
		}
	}

	private void requireState(TurnState turnState) throws InvalidMoveException {
		if (turnState != this.turnState) {
			throw new InvalidMoveException(
					"You can't performed this operation in this state"
			);
		}
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

}
