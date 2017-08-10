package main.gamemodel;

import main.gamemodel.cells.BoardCell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PieceCell.SideType;
import main.gamemodel.cells.PlayerCell;

import java.util.*;
import java.util.stream.Collectors;

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
	/**
	 * The user can move or rotate each piece they have on the board when the
	 * state is MOVING_OR_ROTATING_PIECE. This variable is for keeping track of
	 * which pieces have been played.
	 */
	private Collection<PieceCell> piecesPlayedThisTurn;

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

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public void undo() throws InvalidMoveException {
		if (!canUndo()) {
			throw new IllegalGameStateException("Nothing to undo");
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
		TurnCommand nextTurnState = turnState.getFromMap(
				new NextTurnStateCommandMapper()
		);

		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				board.addCell(newPiece, creationRow, creationCol);

				for (int i = 0; i < numberOfClockwiseRotations; i++) {
					newPiece.rotateClockwise();
				}

				// Don't add this to the undoStack,
				nextTurnState.doWork();
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				board.removeCell(creationRow, creationCol);
				player.unusedUsedPiece(newPiece);
				newPiece.setDirection(Direction.NORTH);

				nextTurnState.undoWork();
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

		if (piecesPlayedThisTurn.contains(piece)) {
			throw new InvalidMoveException(
					"Piece already moved"
			);
		}

		int[] position = board.rowColOf(piece);
		int[] newPosition = direction.shift(position);

		if (board.getCellAt(newPosition[0], newPosition[1]) != null) {
			throw new InvalidMoveException("You can't move onto a cell");
		}

		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				board.removeCell(position[0], position[1]);
				board.addCell(piece, newPosition[0], newPosition[1]);
				piecesPlayedThisTurn.add(piece);
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				board.removeCell(newPosition[0], newPosition[1]);
				board.addCell(piece, position[0], position[1]);
				piecesPlayedThisTurn.remove(piece);
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

		if (piecesPlayedThisTurn.contains(piece)) {
			throw new InvalidMoveException(
					"Piece already moved"
			);
		}

		doCommandWork(new TurnCommand() {
			@Override
			public void doWork() throws InvalidMoveException {
				for (int i = 0; i < clockwiseRotations; i++) {
					piece.rotateClockwise();
				}
				piecesPlayedThisTurn.add(piece);
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				for (int i = 0; i < 4 - clockwiseRotations; i++) {
					piece.rotateClockwise();
				}
				piecesPlayedThisTurn.remove(piece);
			}
		});
	}

	public Collection<Character> getPlayablePieceIds() {
		requireState(TurnState.MOVING_OR_ROTATING_PIECE);

		PlayerData player = getCurrentPlayerData();
		List<PieceCell> pieces = player.getUsedPieceIds()
				.stream()
				.map(player::findUsedPiece)
				.collect(Collectors.toList());

		pieces.removeAll(piecesPlayedThisTurn);
		return pieces.stream()
				.map(PieceCell::getId)
				.collect(Collectors.toList());
	}

	public TurnState getTurnState() {
		return turnState;
	}

	public void passTurnState() throws InvalidMoveException {
		if (turnState == TurnState.RESOLVING_REACTIONS && hasReactions()) {
			throw new InvalidMoveException(
					"You can't pass when there are reactions to resolve"
			);
		}

		doCommandWork(turnState.getFromMap(
				new NextTurnStateCommandMapper()
		));
	}

	public boolean hasReactions() {
		return !getReactions().isEmpty();
	}

	public Set<Board.CellPair> getReactions() {
		return board.findTouchingCellPairs()
				.stream()
				.filter(cellPair -> {
					if (!areBothPieces(cellPair)) {
						return true;
					}
					if (!isShieldVsShield(cellPair)) {
						return true;
					}
					return false;
				})
				.collect(Collectors.toSet());
	}

	public void react(Board.CellPair cellPair) throws InvalidMoveException {
		if (!getReactions().contains(cellPair)) {
			throw new InvalidMoveException("These cells aren't able to react");
		}

		System.out.println("*************** Reaction: " + cellPair);
		// TODO
	}

	private SideType[] getTouchingSides(PieceCell pieceA,
										PieceCell pieceB) {
		SideType sideA = pieceA.getTouchingSide(
				board.rowColOf(pieceA),
				board.rowColOf(pieceB)
		);
		SideType sideB = pieceA.getTouchingSide(
				board.rowColOf(pieceB),
				board.rowColOf(pieceA)
		);
		return new SideType[]{sideA, sideB};
	}

	private boolean areBothPieces(Board.CellPair pair) {
		return pair.getCellA() instanceof PieceCell &&
				pair.getCellB() instanceof PieceCell;
	}

	private boolean isShieldVsShield(Board.CellPair pair) {
		if (!areBothPieces(pair)) {
			throw new RuntimeException(
					"One or more cells are not pieces so they both cannot be " +
						"facing each other with shields"
			);
		}

		SideType[] touchingSides = getTouchingSides(
				(PieceCell) pair.getCellA(),
				(PieceCell) pair.getCellB()
		);
		return Arrays
				.stream(touchingSides)
				.allMatch(sideType -> sideType == SideType.SHIELD);
	}

	private void doCommandWork(TurnCommand command) throws InvalidMoveException {
		command.doWork();
		undoStack.push(command);
	}

	private void requireState(TurnState turnState) {
		if (turnState != this.turnState) {
			throw new IllegalGameStateException(
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

	private interface TurnCommand {
		void doWork() throws InvalidMoveException;
		void undoWork() throws InvalidMoveException;
	}

	private class NextTurnStateCommandMapper implements TurnState.Mapper<TurnCommand> {
		/**
		 * Assumes that there may be reactions
		 */
		@Override
		public TurnCommand getCreatingPiecesValue() {
			return new TurnCommand() {
				@Override
				public void doWork() throws InvalidMoveException {
					if (!hasReactions()) {
						piecesPlayedThisTurn = new ArrayList<>();
						turnState = TurnState.MOVING_OR_ROTATING_PIECE;
					} else {
						turnState = TurnState.RESOLVING_REACTIONS;
					}
				}

				/**
				 * NOTE: This gets called when trying to undo when the
				 * {@link GameModel#turnState} was
				 * {@link TurnState#CREATING_PIECE} before doWork was called.
				 */
				@Override
				public void undoWork() throws InvalidMoveException {
					turnState = TurnState.CREATING_PIECE;
					piecesPlayedThisTurn = null;
				}
			};
		}

		@Override
		public TurnCommand getMovingOrRotatingPieceValue() {
			return new TurnCommand() {
				private Collection<PieceCell> piecesPlayed;

				@Override
				public void doWork() throws InvalidMoveException {
					if (hasReactions()) {
						turnState = TurnState.RESOLVING_REACTIONS;
						return;
					}
					turnState = TurnState.CREATING_PIECE;
					piecesPlayed = piecesPlayedThisTurn;
					piecesPlayedThisTurn = null;
					currentPlayerIndex++;
					currentPlayerIndex %= playerData.size();
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					if (turnState == TurnState.RESOLVING_REACTIONS) {
						turnState = TurnState.MOVING_OR_ROTATING_PIECE;
						return;
					}
					turnState = TurnState.MOVING_OR_ROTATING_PIECE;
					piecesPlayedThisTurn = piecesPlayed;
					piecesPlayed = null;
					currentPlayerIndex--;
					currentPlayerIndex += playerData.size();
					currentPlayerIndex %= playerData.size();
				}
			};
		}

		@Override
		public TurnCommand getResolvingReactionsValue() {
			return new TurnCommand() {
				@Override
				public void doWork() throws InvalidMoveException {
					if (hasReactions()) {
						throw new IllegalGameStateException(
								"You must resolve the conflicts first"
						);
					}

					turnState = TurnState.MOVING_OR_ROTATING_PIECE;
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					turnState = TurnState.RESOLVING_REACTIONS;
				}
			};
		}
	}

}
