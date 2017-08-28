package main.gui.game;

import main.gamemodel.*;
import main.gamemodel.cells.Cell;
import main.gamemodel.cells.PieceCell;
import main.gui.game.celldrawers.CellColorProcessor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class GameGUIModel extends Observable implements Observer, CellColorProcessor {

	private final Supplier<GameModel> gameModelFactory;

	private final Supplier<GameModel> gameModel = new Supplier<GameModel>() {
		public GameModel model;

		@Override
		public GameModel get() {
			if (model == null) {
				model = gameModelFactory.get();
				model.addObserver(GameGUIModel.this);
				resetGuiState(false);
			}
			return model;
		}
	};

	private GUIState guiState;

	private PieceCell creationSelectedCell;
	private List<PieceCell> creationSelectedCellRotatedCopies;
	private PieceCell movementSelectedCell;

	public GameGUIModel(Supplier<GameModel> gameModelFactory) {
		this.gameModelFactory = gameModelFactory;
	}

	public GameModel getGameModel() {
		return gameModel.get();
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

	public void setMovementSelectedCell(PieceCell movementSelectedCell)
			throws InvalidMoveException {
		requireState(GUIState.MOVING_OR_ROTATING_PIECE_SELECTION);
		if (getGameModel().getPiecesPlayedThisTurn().contains(movementSelectedCell)) {
			throw new InvalidMoveException("You have already moved this cell");
		}

		this.movementSelectedCell = movementSelectedCell;
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

	public void setGuiState(GUIState guiState) {
		if (this.guiState == guiState) {
			return;
		}

		if (this.guiState == GUIState.CREATE_PIECE_ROTATION) {
			// Leaving this state
			creationSelectedCell = null;
			creationSelectedCellRotatedCopies = null;
		}
		if (this.guiState == GUIState.MOVING_OR_ROTATING_PIECE_APPLYING) {
			// Leaving this state
			movementSelectedCell = null;
		}

		this.guiState = guiState;
		setChanged();
		notifyObservers();
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

		getGameModel().move(movementSelectedCell.getId(), moveDirection);
		movementSelectedCell = null;
		setGuiState(GUIState.MOVING_OR_ROTATING_PIECE_SELECTION);
	}

	@Override
	public Color process(Color color, Cell cell) {
		if (cell == creationSelectedCell || cell == movementSelectedCell) {
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
		movementSelectedCell = null;

		if (!forceReset && validStates.contains(guiState)) {
			// No need to change
			return;
		}

		setGuiState(validStates.get(0));
		setChanged();
		notifyObservers();
	}

	/**
	 * Finds the possible GUIStates for a given {@link GameModel}'s
	 * {@link TurnState}. The first item in the list that this mapper returns
	 * has the state to enter when the gameModel's getTurnState returns
	 * the state corresponding to the method.
	 */
	private static class TurnStateToGUIState implements TurnState.Mapper<List<GUIState>> {
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
					GUIState.MOVING_OR_ROTATING_PIECE_APPLYING
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
