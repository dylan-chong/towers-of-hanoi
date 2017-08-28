package main.gamemodel;

import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;
import main.gamemodel.cells.Reaction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TODO: Split this class up using the state pattern
 */
public class GameModel extends Observable implements Textable {

	/**
	 * The offsets from the corners of the board
	 */
	public static final int PLAYER_CELL_OFFSET = 1;
	public static final int CREATION_CELL_OFFSET = 2;

	private final Board board;
	private final List<Player> players;

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
	private Player winner;

	public GameModel(Board emptyBoard) {
		assert emptyBoard.isEmpty();

		this.board = emptyBoard;
		this.players = Arrays.asList(
				new Player(
						new PlayerCell(PlayerCell.Token.ANGRY),
						true,
						CREATION_CELL_OFFSET,
						CREATION_CELL_OFFSET
				),
				new Player(
						new PlayerCell(PlayerCell.Token.HAPPY),
						false,
						emptyBoard.getNumRows() - 1 - CREATION_CELL_OFFSET,
						emptyBoard.getNumCols() - 1 - CREATION_CELL_OFFSET
				)
		);
		this.currentPlayerIndex = 0;

		setupPlayers();
	}

	public Board getBoard() {
		return board;
	}

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public void undo() throws InvalidMoveException {
		if (!canUndo()) {
			throw new IllegalGameStateException("Nothing to undo");
		}

		undoStack.pop().undoWork();
		setChanged();
		notifyObservers();
	}

	public Player getCurrentPlayerData() {
		return players.get(currentPlayerIndex);
	}

	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	public Collection<PieceCell> getPiecesPlayedThisTurn() {
		return piecesPlayedThisTurn;
	}

	@Override
	public char[][] toTextualRep() {
		return board.toTextualRep();
	}

	public void requireCanCreatePiece() throws InvalidMoveException {
		requireState(TurnState.CREATING_PIECE, null);

		Player player = getCurrentPlayerData();
		final int creationRow = player.getCreationRow();
		final int creationCol = player.getCreationCol();

		Cell existingCell = board.getCellAt(creationRow, creationCol);
		if (existingCell != null) {
			throw new InvalidMoveException(
					"There is a cell in your creation square"
			);
		}
	}

	/**
	 * Create a piece on the current player's creation square
	 *
	 * @param pieceId     a,b,c,... the piece to get from. Case insensitive.
	 *                    {@link Player#unusedPieces}
	 * @param numberOfClockwiseRotations Used to sit the original orientation
	 *                                   of the piece
	 */
	public void create(char pieceId, int numberOfClockwiseRotations)
			throws InvalidMoveException {
		requireState(TurnState.CREATING_PIECE, null);
		requireCanCreatePiece();

		Player player = getCurrentPlayerData();
		final int creationRow = player.getCreationRow();
		final int creationCol = player.getCreationCol();

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
		requireState(TurnState.MOVING_OR_ROTATING_PIECE, null);

		Player player = getCurrentPlayerData();
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

				if (hasReactions()) {
					turnState = TurnState.RESOLVING_REACTIONS;
				}
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				if (hasReactions()) {
					turnState = TurnState.MOVING_OR_ROTATING_PIECE;
				}
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

		requireState(TurnState.MOVING_OR_ROTATING_PIECE, null);

		Player player = getCurrentPlayerData();
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

				if (hasReactions()) {
					turnState = TurnState.RESOLVING_REACTIONS;
				}
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				for (int i = 0; i < 4 - clockwiseRotations; i++) {
					piece.rotateClockwise();
				}
				piecesPlayedThisTurn.remove(piece);

				if (hasReactions()) {
					turnState = TurnState.MOVING_OR_ROTATING_PIECE;
				}
			}
		});
	}

	public Collection<Character> getPlayablePieceIds() {
		requireState(TurnState.MOVING_OR_ROTATING_PIECE, null);

		Player player = getCurrentPlayerData();
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

	public Collection<ReactionData.Pair> getReactions() {
		return board.findTouchingCellPairs()
				.stream()
				.map(cellPair -> new ReactionData.Pair(
						cellPair,
						board::rowColOf,
						this::getPlayerOfCell
				))
				.filter(pair -> !pair.isBlankReaction())
				.collect(Collectors.toList());
	}

	public void react(ReactionData.Pair reactionDataPair) throws InvalidMoveException {
		if (!getReactions().contains(reactionDataPair)) {
			throw new InvalidMoveException("These cells aren't able to react");
		}

		ReactionApplier reactionApplier = new ReactionApplier();

		ReactionData dataA = reactionDataPair.dataA;
		ReactionData dataB = reactionDataPair.dataB;

		Command commandA = dataA.reaction
				.getFromMap(reactionApplier)
				.apply(dataA);
		Command commandB = dataB.reaction
				.getFromMap(reactionApplier)
				.apply(dataB);

		doCommandWork(new Command() {
			boolean didRunCommandB = false;

			@Override
			public void doWork() throws InvalidMoveException {
				commandA.doWork();
				didRunCommandB = false;
				if (turnState != TurnState.GAME_FINISHED) {
					commandB.doWork();
					didRunCommandB = true;
				}
			}

			@Override
			public void undoWork() throws InvalidMoveException {
				if (didRunCommandB) {
					commandB.undoWork();
				}
				commandA.undoWork();
			}
		});
	}

	public Player getWinner() {
		if (turnState != TurnState.GAME_FINISHED) {
			throw new IllegalGameStateException("The game is not finished yet");
		}

		return winner;
	}

	public Player getPlayerOfCell(Cell cell) {
		return players.stream()
				.filter(data -> data.ownsPiece(cell))
				.findAny()
				.orElseThrow(() -> new IllegalGameStateException(
						"A player does not have the cell"
				));
	}

	public void surrender() throws InvalidMoveException {
		if (turnState == TurnState.GAME_FINISHED) {
			throw new InvalidMoveException("Game is already finished");
		}

		Player newLoser = getCurrentPlayerData();
		doCommandWork(new LoseGame(newLoser));
	}

	private void doCommandWork(Command command) throws InvalidMoveException {
		command.doWork();
		undoStack.push(command);
		setChanged();
		notifyObservers();
	}

	private void requireState(TurnState turnState, String messageOrNull) {
		if (messageOrNull == null) {
			messageOrNull = "You can't performed this operation in this state";
		}
		if (turnState != this.turnState) {
			throw new IllegalGameStateException(messageOrNull);
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

	private interface Command {
		void doWork() throws InvalidMoveException;
		void undoWork() throws InvalidMoveException;
	}

	public class LoseGame implements Command {
		private final Player loser;

		private TurnState previousTurnState;
		private int[] loserCellRowCol;

		public LoseGame(Player loser) {
			this.loser = loser;
		}

		@Override
		public void doWork() throws InvalidMoveException {
			List<Player> winners = players.stream()
					.filter(p -> p != loser)
					.collect(Collectors.toList());
			if (winners.size() != 1) {
				throw new IllegalGameStateException(String.format(
						"Somehow there were %d winners",
						winners.size()
				));
			}
			winner = winners.get(0);
			previousTurnState = turnState;
			turnState = TurnState.GAME_FINISHED;

			loserCellRowCol = board.rowColOf(loser.getPlayerCell());
			board.removeCell(loserCellRowCol[0], loserCellRowCol[1]);
		}

		@Override
		public void undoWork() throws InvalidMoveException {
			board.addCell(
					loser.getPlayerCell(),
					loserCellRowCol[0], loserCellRowCol[1]
			);

			winner = null;
			turnState = previousTurnState;

			previousTurnState = null;
			loserCellRowCol = null;
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

	private class ReactionApplier
			implements Reaction.Mapper<Function<ReactionData, Command>> {

		@Override
		public Function<ReactionData, Command> getDoNothingValue() {
			return reactionData -> new Command() {
				@Override
				public void doWork() throws InvalidMoveException {
				}

				@Override
				public void undoWork() throws InvalidMoveException {
				}
			};
		}

		@Override
		public Function<ReactionData, Command> getDieValue() {
			return reactionData -> new Command() {
				@Override
				public void doWork() throws InvalidMoveException {
					int[] rowCol = reactionData.cellRowCol;
					board.removeCell(rowCol[0], rowCol[1]);

					Player player = reactionData.cellPlayer;
					player.killPiece((PieceCell) reactionData.cell);
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					PieceCell cell = (PieceCell) reactionData.cell;
					Player player = reactionData.cellPlayer;
					player.revivePiece(cell);

					int[] rowCol = reactionData.cellRowCol;
					board.addCell(cell, rowCol[0], rowCol[1]);
				}
			};
		}

		@Override
		public Function<ReactionData, Command> getGetBumpedBackValue() {
			return reactionData -> new Command() {
				int[] originalRowCol;
				int[] nextRowCol;

				@Override
				public void doWork() throws InvalidMoveException {
					originalRowCol = reactionData.cellRowCol;
					Direction moveDir = Direction.fromAToB(
							reactionData.cellReactedToRowCol,
							originalRowCol
					);
					nextRowCol = moveDir.shift(originalRowCol);

					if (!board.isInside(nextRowCol[0], nextRowCol[1])) {
						throw new InvalidMoveException(
								"Can't push a cell outside the board (TODO)"
						);
					}

					if (board.getCellAt(nextRowCol[0], nextRowCol[1]) != null) {
						throw new InvalidMoveException(
								"Can't push a cell into another cell (TODO)"
						);
					}

					board.removeCell(originalRowCol[0], originalRowCol[1]);
					board.addCell(reactionData.cell, nextRowCol[0], nextRowCol[1]);
				}

				@Override
				public void undoWork() throws InvalidMoveException {
					board.removeCell(nextRowCol[0], nextRowCol[1]);
					board.addCell(
							reactionData.cell,
							originalRowCol[0], originalRowCol[1]
					);
				}
			};
		}

		@Override
		public Function<ReactionData, Command> getLoseTheGameValue() {
			return reactionData -> new LoseGame(reactionData.cellPlayer);
		}
	}
}
