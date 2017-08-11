package main.gamemodel;

import main.gamemodel.cells.BoardCell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PieceCell.SideType;
import main.gamemodel.cells.PlayerCell;
import main.gamemodel.cells.Reaction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO: Split this class up using the state pattern
 */
public class GameModel implements Textable {

	/**
	 * The offsets from the corners of the board
	 */
	private static final int PLAYER_CELL_OFFSET = 1;
	private static final int CREATION_CELL_OFFSET = 2;

	private final Board board;
	private final List<PlayerData> players;

	/**
	 * Index in players
	 */
	private int currentPlayerIndex;
	private TurnState turnState = TurnState.CREATING_PIECE;
	/**
	 * The user can move or rotate each piece they have on the board when the
	 * state is MOVING_OR_ROTATING_PIECE. This variable is for keeping track of
	 * which pieces have been played.
	 */
	private Collection<PieceCell> piecesPlayedThisTurn;

	private Deque<Command> undoStack = new ArrayDeque<>();
	private PlayerData winner;

	public GameModel(Board emptyBoard) {
		assert emptyBoard.isEmpty();

		this.board = emptyBoard;
		this.players = Arrays.asList(
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
		return players.get(currentPlayerIndex);
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
		Command nextTurnState = turnState.getFromMap(
				new NextTurnStateCommandMapper()
		);

		doCommandWork(new Command() {
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
				player.unuseUsedPiece(newPiece);
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

		doCommandWork(new Command() {
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

		doCommandWork(new Command() {
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

		BoardCell cellA = cellPair.getCellA();
		BoardCell cellB = cellPair.getCellB();
		int[] cellARowCol = board.rowColOf(cellA);
		int[] cellBRowCol = board.rowColOf(cellB);
		ReactionCellData dataA = new ReactionCellData(
				cellA, cellARowCol, cellB, cellBRowCol,
				getPlayerOfCell(cellA)
		);
		ReactionCellData dataB = new ReactionCellData(
				cellB, cellBRowCol, cellA, cellARowCol,
				getPlayerOfCell(cellB)
		);

		ReactionApplier reactionApplier = new ReactionApplier();

		Command commandA = dataA.reaction
				.getFromMap(reactionApplier)
				.apply(dataA);
		Command commandB = dataB.reaction
				.getFromMap(reactionApplier)
				.apply(dataB);
		doCommandWork(new Command.Composite(commandA, commandB));
	}

	private PlayerData getPlayerOfCell(BoardCell cell) {
		return players.stream()
				.filter(data -> data.ownsPiece(cell))
				.findAny()
				.orElseThrow(() -> new IllegalGameStateException(
						"A player does not have the cell"
				));
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

	private void doCommandWork(Command command) throws InvalidMoveException {
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
					players.get(0).getPlayerCell(),
					PLAYER_CELL_OFFSET,
					PLAYER_CELL_OFFSET
			);
			board.addCell(
					players.get(1).getPlayerCell(),
					board.getNumRows() - 1 - PLAYER_CELL_OFFSET,
					board.getNumCols() - 1 - PLAYER_CELL_OFFSET
			);
		} catch (InvalidMoveException e) {
			throw new Error(e);
		}
	}

	public PlayerData getWinner() {
		if (turnState != TurnState.GAME_FINISHED) {
			throw new IllegalGameStateException("The game is not finished yet");
		}
		return winner;
	}

	private interface Command {
		void doWork() throws InvalidMoveException;
		void undoWork() throws InvalidMoveException;

		class Composite implements Command {
			private final Command[] commands;

			public Composite(Command... commands) {
				this.commands = commands;
			}

			@Override
			public void doWork() throws InvalidMoveException {
				for (Command command : commands) {
					command.doWork();
				}
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				for (int i = commands.length - 1; i >= 0; i--) {
					Command command = commands[i];
					command.undoWork();
				}
			}
		}
	}

	private class NextTurnStateCommandMapper implements TurnState.Mapper<Command> {
		/**
		 * Assumes that there may be reactions
		 */
		@Override
		public Command getCreatingPiecesValue() {
			return new Command() {
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
		public Command getMovingOrRotatingPieceValue() {
			return new Command() {
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
					currentPlayerIndex %= players.size();
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
					currentPlayerIndex += players.size();
					currentPlayerIndex %= players.size();
				}
			};
		}

		@Override
		public Command getResolvingReactionsValue() {
			return new Command() {
				@Override
				public void doWork() throws InvalidMoveException {
					if (hasReactions()) {
						throw new IllegalGameStateException(
								"You must resolve the conflicts before passing"
						);
					}

					turnState = TurnState.MOVING_OR_ROTATING_PIECE;
					if (piecesPlayedThisTurn == null)
						piecesPlayedThisTurn = new ArrayList<>();
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					turnState = TurnState.RESOLVING_REACTIONS;
				}
			};
		}

		@Override
		public Command getGameFinishedValue() {
			throw new UnsupportedOperationException("Game has finished");
		}
	}

	private static class ReactionCellData {
		public final BoardCell cell;
		public final int[] cellRowCol;
		public final BoardCell cellReactedTo;
		public final int[] cellReactedToRowCol;
		public final Reaction reaction;
		public final PlayerData cellPlayerData;

		public ReactionCellData(BoardCell cell,
								int[] cellRowCol,
								BoardCell cellReactedTo,
								int[] cellReactedToRowCol,
								PlayerData cellPlayerData) {
			this.cell = cell;
			this.cellRowCol = cellRowCol;
			this.cellReactedTo = cellReactedTo;
			this.cellReactedToRowCol = cellReactedToRowCol;
			this.cellPlayerData = cellPlayerData;
			this.reaction = cell.getReactionTo(
					cellReactedTo,
					Direction.fromAToB(cellRowCol, cellReactedToRowCol)
			);
		}
	}

	private class ReactionApplier
			implements Reaction.Mapper<Function<ReactionCellData, Command>> {

		@Override
		public Function<ReactionCellData, Command> getDoNothingValue() {
			return reactionCellData -> new Command() {
				@Override
				public void doWork() throws InvalidMoveException {
				}

				@Override
				public void undoWork() throws InvalidMoveException {
				}
			};
		}

		@Override
		public Function<ReactionCellData, Command> getDieValue() {
			return reactionCellData -> new Command() {
				@Override
				public void doWork() throws InvalidMoveException {
					int[] rowCol = reactionCellData.cellRowCol;
					board.removeCell(rowCol[0], rowCol[1]);

					PlayerData player = reactionCellData.cellPlayerData;
					player.killPiece((PieceCell) reactionCellData.cell);
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					PieceCell cell = (PieceCell) reactionCellData.cell;
					PlayerData player = reactionCellData.cellPlayerData;
					player.revivePiece(cell);

					int[] rowCol = reactionCellData.cellRowCol;
					board.addCell(cell, rowCol[0], rowCol[1]);
				}
			};
		}

		@Override
		public Function<ReactionCellData, Command> getGetBumpedBackValue() {
			throw new RuntimeException("TODO");
		}

		@Override
		public Function<ReactionCellData, Command> getLoseTheGameValue() {
			return reactionCellData -> new Command() {
				private TurnState previousTurnState;

				@Override
				public void doWork() throws InvalidMoveException {
					List<PlayerData> winners = players.stream()
							.filter(p -> p != reactionCellData.cellPlayerData)
							.collect(Collectors.toList());
					if (winners.size() != 1) {
						throw new IllegalGameStateException(String.format(
								"Somehow there were %d winners",
								winners.size()
						));
					}

					winner = winners.get(0);
					previousTurnState = turnState;
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					winner = null;
					turnState = previousTurnState;
					previousTurnState = null;
				}
			};
		}
	}
}
