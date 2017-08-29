package main.gui.game;

import main.gamemodel.*;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gui.game.celldrawers.CellColorProcessor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GameGUIModel extends Observable implements Observer, CellColorProcessor {

	private final GameModel gameModel;

	private GUIState guiState;

	private PieceCell creationSelectedCell;
	private List<PieceCell> creationSelectedCellRotatedCopies;
	private PieceCell movementOrRotationSelectedCell;

	public GameGUIModel(GameModel gameModel) {
		this.gameModel = gameModel;
		gameModel.addObserver(GameGUIModel.this);
		resetGuiState(false);
	}

	public GameModel getGameModel() {
		return gameModel;
	}

	public PieceCell getCreationSelectedCell() {
		return creationSelectedCell;
	}

	public void setCreationSelectedCell(PieceCell creationSelectedCell)
			throws InvalidMoveException {
		requireState(GUIState.CREATE_PIECE_CREATION);

		this.creationSelectedCell = creationSelectedCell;
		setGuiState(GUIState.CREATE_PIECE_ROTATION);
	}

	public void setMovementOrRotationSelectedCell(PieceCell newVal)
			throws InvalidMoveException {
		requireState(GUIState.MOVING_OR_ROTATING_PIECE_SELECTION);
		if (getGameModel().getPiecesPlayedThisTurn().contains(newVal)) {
			throw new InvalidMoveException("You have already moved this cell");
		}

		this.movementOrRotationSelectedCell = newVal;
		setGuiState(GUIState.MOVING_OR_ROTATING_PIECE_APPLYING);
	}

	public List<PieceCell> calculateRotatedCopiesOfSelectedCell()
			throws InvalidMoveException {
		requireState(GUIState.CREATE_PIECE_ROTATION);

		if (creationSelectedCellRotatedCopies == null) {
			creationSelectedCellRotatedCopies =
					creationSelectedCell.getRotatedCopies();
		}

		return creationSelectedCellRotatedCopies;
	}

	public Player getCurrentPlayer() {
		return getGameModel().getCurrentPlayerData();
	}

	public Player getPlayerOfCellOrRotatedCopy(Cell cell) {
		if (creationSelectedCellRotatedCopies != null &&
				creationSelectedCellRotatedCopies
						.stream()
						.anyMatch(copy -> cell == copy)
				) {
			return getCurrentPlayer();
		}

		return getGameModel().getPlayerOfCell(cell);
	}

	@Override
	public void update(Observable o, Object arg) {
		SwingUtilities.invokeLater(() -> {
			resetGuiState(arg == GameModel.UNDO_UPDATE_KEY);
			setChanged();
			notifyObservers(arg);
		});
	}

	public GUIState getGuiState() {
		return guiState;
	}

	public void performGameAction(GameAction action) {
		try {
			action.perform();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(e);
		}
	}

	public void createPiece(Cell rotatedCellCopy) throws InvalidMoveException {
		requireState(GUIState.CREATE_PIECE_ROTATION);

		PieceCell rotatedPieceCopy = (PieceCell) rotatedCellCopy;
		PieceCell baseCell = creationSelectedCell;

		if (rotatedPieceCopy.getSideCombination() != baseCell.getSideCombination()) {
			throw new IllegalArgumentException("Somehow wrong cell was selected");
		}

		Direction direction = rotatedPieceCopy.getDirection();

		getGameModel().create(baseCell.getId(), direction.ordinal());
		creationSelectedCell = null;
	}

	public void move(Direction moveDirection) throws InvalidMoveException {
		requireState(GUIState.MOVING_OR_ROTATING_PIECE_APPLYING);

		getGameModel().move(movementOrRotationSelectedCell.getId(), moveDirection);
		movementOrRotationSelectedCell = null;
		setGuiState(GUIState.MOVING_OR_ROTATING_PIECE_SELECTION);
	}

	public void enterRotationMode() throws InvalidMoveException {
		requireState(GUIState.MOVING_OR_ROTATING_PIECE_APPLYING);
		setGuiState(GUIState.MOVING_OR_ROTATING_PIECE_ROTATING);
	}

	@Override
	public Color process(Color color, Cell cell) {
		if (cell == creationSelectedCell || cell == movementOrRotationSelectedCell) {
			return color.brighter();
		}

		if (getGameModel().getTurnState() == TurnState.MOVING_OR_ROTATING_PIECE) {
			Collection<PieceCell> played = getGameModel().getPiecesPlayedThisTurn();
			if (cell instanceof PieceCell && played.contains(cell)) {
				return color.darker();
			}
		}

		return color;
	}

	public PieceCell getCopyOfMovementOrRotationSelectedCell() {
		return movementOrRotationSelectedCell.createCopy();
	}

	public void cancelRotation() throws InvalidMoveException {
		requireState(GUIState.MOVING_OR_ROTATING_PIECE_ROTATING);
		movementOrRotationSelectedCell = null;
		setGuiState(GUIState.MOVING_OR_ROTATING_PIECE_SELECTION);
	}

	public void rotate(PieceCell rotatedCellCopy) throws InvalidMoveException {
		requireState(GUIState.MOVING_OR_ROTATING_PIECE_ROTATING);

		PieceCell baseCell = movementOrRotationSelectedCell;
		if (rotatedCellCopy.getSideCombination() != baseCell.getSideCombination()) {
			throw new IllegalArgumentException("Somehow wrong cell was selected");
		}

		int numDirs = Direction.values().length;
		int clockwiseRotations = rotatedCellCopy.getDirection().ordinal()
				- baseCell.getDirection().ordinal();
		clockwiseRotations += numDirs; // Ensure positive
		clockwiseRotations %= numDirs;

		if (clockwiseRotations == 0) {
			// Doesn't count as a move
			cancelRotation();
			return;
		}

		getGameModel().rotate(baseCell.getId(), clockwiseRotations);
	}

	private void requireState(GUIState... states) throws InvalidMoveException {
		String msg = String.format(
				"Not allowed to be called in state %s",
				guiState.name()
		);
		if (!Arrays.asList(states).contains(guiState)) {
			throw new InvalidMoveException(msg);
		}
	}

	private void resetGuiState(boolean forceReset) {
		List<GUIState> validStates = getGameModel()
				.getTurnState()
				.getFromMap(new TurnStateToGUIState());

		// Deselect on undo
		creationSelectedCell = null;
		movementOrRotationSelectedCell = null;

		if (!forceReset && validStates.contains(guiState)) {
			// No need to change
			return;
		}

		setGuiState(validStates.get(0));
		setChanged();
		notifyObservers();
	}

	private void setGuiState(GUIState newGuiState) {
		if (this.guiState == newGuiState) {
			return;
		}

		if (this.guiState == GUIState.CREATE_PIECE_ROTATION) {
			// Leaving this state
			creationSelectedCell = null;
			creationSelectedCellRotatedCopies = null;
		}
		if (isLeavingMovingOrRotatingPieceState(newGuiState)) {
			movementOrRotationSelectedCell = null;
		}

		this.guiState = newGuiState;
		setChanged();
		notifyObservers();
	}

	private boolean isLeavingMovingOrRotatingPieceState(GUIState newState) {
		List<GUIState> movingOrRotatingStates = TurnState
				.MOVING_OR_ROTATING_PIECE
				.getFromMap(new TurnStateToGUIState());

		if (!movingOrRotatingStates.contains(guiState)) {
			// Not in a moving/rotating state
			return false;
		}

		if (movingOrRotatingStates.contains(newState)) {
			// Staying in selection state
			return false;
		}

		return true;
	}


	/**
	 * Finds the possible GUIStates for a given {@link GameModel}'s
	 * {@link TurnState}. The first item in the list that this mapper returns
	 * has the state to enter when the gameModel's getTurnState returns
	 * the state corresponding to the method.
	 */
	private static class TurnStateToGUIState implements TurnState.Mapper<List<GUIState>> {
		static {
			List<GUIState> usedGuiStates = new ArrayList<>();
			for (TurnState turnState : TurnState.values()) {
				List<GUIState> guiStates = turnState.getFromMap(
						new TurnStateToGUIState()
				);
				usedGuiStates.addAll(guiStates);
			}

			usedGuiStates.sort(Comparator.naturalOrder());
			List<GUIState> allGuiStates = Arrays.asList(GUIState.values());

			if (!allGuiStates.equals(usedGuiStates)) {
				throw new AssertionError("Not all gui states have been used");
			}
		}

		@Override
		public List<GUIState> getCreatingPiecesValue() {
			return Arrays.asList(
					GUIState.CREATE_PIECE_CREATION,
					GUIState.CREATE_PIECE_ROTATION
			);
		}

		@Override
		public List<GUIState> getMovingOrRotatingPieceValue() {
			return Arrays.asList(
					GUIState.MOVING_OR_ROTATING_PIECE_SELECTION,
					GUIState.MOVING_OR_ROTATING_PIECE_APPLYING,
					GUIState.MOVING_OR_ROTATING_PIECE_ROTATING
			);
		}

		@Override
		public List<GUIState> getResolvingReactionsValue() {
			return Arrays.asList(
					GUIState.RESOLVING_REACTIONS
			);
		}

		@Override
		public List<GUIState> getGameFinishedValue() {
			return Arrays.asList(
					GUIState.GAME_FINISHED
			);
		}
	}
}
