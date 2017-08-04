package main.gamemodel;

import main.gamemodel.cells.BoardCell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
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
	// TODO AFTER limit turns

	private Deque<TurnCommand> undoStack = new ArrayDeque<>();

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

	public void undo() throws InvalidMoveException {
		if (undoStack.isEmpty()) {
			throw new IllegalStateException("Nothing to undo");
		}

		undoStack.pop().undoWork();
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
	 * @param pieceId     a,b,c,... the piece to get from. Case insensitive.
	 *                    {@link PlayerData#unusedPieces}
	 * @param numberOfClockwiseRotations Used to sit the original orientation
	 *                                   of the piece
	 */
	public void create(char pieceId, int numberOfClockwiseRotations)
			throws InvalidMoveException {
		requireState(TurnState.CREATING_PIECE);

		PlayerData player = getCurrentPlayerData();
		final int creationRow = player.getCreationRow();
		final int creationCol = player.getCreationCol();

		BoardCell existingCell = board.getCellAt(
				creationRow, creationCol
		);
		if (existingCell != null) {
			throw new InvalidMoveException(
					"There is a cell in your creation square"
			);
		}

		PieceCell newPiece = player.useUnusedPiece(pieceId);

		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				board.addCell(newPiece, creationRow, creationCol);

				for (int i = 0; i < numberOfClockwiseRotations; i++) {
					newPiece.rotateClockwise();
				}

				turnState = TurnState.MOVING_OR_ROTATING_PIECE;
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				board.removeCell(creationRow, creationCol);
				player.unusedUsedPiece(newPiece);
				newPiece.setDirection(Direction.NORTH);

				turnState = TurnState.CREATING_PIECE;
			}
		});
	}

	public void move(char pieceId, Direction direction)
			throws InvalidMoveException {
		requireState(TurnState.MOVING_OR_ROTATING_PIECE);

		PlayerData player = getCurrentPlayerData();
		PieceCell piece = player.findUsedPiece(pieceId);
		if (piece == null) {
			throw new InvalidMoveException(
					"You do not have a cell on the board under this name"
			);
		}

		int[] position = board.positionOf(piece);
		int[] newPosition = direction.shift(position);

		if (board.getCellAt(newPosition[0], newPosition[1]) != null) {
			throw new InvalidMoveException("You can't move onto a cell");
		}

		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				board.removeCell(position[0], position[1]);
				board.addCell(piece, newPosition[0], newPosition[1]);
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				board.removeCell(newPosition[0], newPosition[1]);
				board.addCell(piece, position[0], position[1]);
			}
		});
	}

	public void rotate(char pieceId, int clockwiseRotations)
			throws InvalidMoveException {
		if (clockwiseRotations < 1 || clockwiseRotations > 3) {
			throw new InvalidMoveException("Invalid number of rotations");
		}

		requireState(TurnState.MOVING_OR_ROTATING_PIECE);

		PlayerData player = getCurrentPlayerData();
		PieceCell piece = player.findUsedPiece(pieceId);
		if (piece == null) {
			throw new InvalidMoveException(
					"You do not have a cell on the board under this name"
			);
		}

		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				for (int i = 0; i < clockwiseRotations; i++) {
					piece.rotateClockwise();
				}
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				for (int i = 0; i < 4 - clockwiseRotations; i++) {
					piece.rotateClockwise();
				}
			}
		});
	}

	public TurnState getTurnState() {
		return turnState;
	}

	public void passTurnState() throws InvalidMoveException {
		// TODO CommandProvider
		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				switch (turnState) {
					case CREATING_PIECE:
						turnState = TurnState.MOVING_OR_ROTATING_PIECE;
						break;
					case MOVING_OR_ROTATING_PIECE:
						turnState = TurnState.CREATING_PIECE;
						currentPlayerIndex++;
						currentPlayerIndex %= playerData.size();
						break;
				}
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				switch (turnState) {
					case CREATING_PIECE:
						turnState = TurnState.MOVING_OR_ROTATING_PIECE;
						currentPlayerIndex--;
						currentPlayerIndex += playerData.size();
						currentPlayerIndex %= playerData.size();
						break;
					case MOVING_OR_ROTATING_PIECE:
						turnState = TurnState.CREATING_PIECE;
						break;
				}
			}
		});
	}

	private void doCommandWork(TurnCommand command) throws InvalidMoveException {
		command.doWork();
		undoStack.push(command);
	}

	private void requireState(TurnState turnState) throws InvalidMoveException {
		if (turnState != this.turnState) {
			throw new InvalidMoveException(
					"You can't performed this operation in this state"
			);
		}
	}

	/**
	 * Just for readability and consistency
	 */
	private void undoToPreviousState(TurnState turnState) {
		this.turnState = turnState;
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

	private interface TurnCommand {
		void doWork() throws InvalidMoveException;
		void undoWork() throws InvalidMoveException;
	}
}
