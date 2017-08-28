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

	public static List<PieceCell> getRotatedCopies(PieceCell baseCell) {
		List<PieceCell> rotatedCopies = new ArrayList<>();

		for (int rotations = 0; rotations < Direction.values().length; rotations++) {
			PieceCell cell = new PieceCell(
					(char) ('a' + rotations), // Assume we don't show these
					baseCell.getSideCombination(),
					Direction.values()[rotations]
			);
			rotatedCopies.add(cell);
		}

		return rotatedCopies;
	}

	private final Supplier<GameModel> gameModelFactory;

	private GameModel gameModel;
	private GUIState guiState;

	private PieceCell creationSelectedCell;
	private List<PieceCell> creationSelectedCellRotatedCopies;

	public GameGUIModel(Supplier<GameModel> gameModelFactory) {
		this.gameModelFactory = gameModelFactory;
	}

	public GameModel getGameModel() {
		if (gameModel == null) {
			gameModel = gameModelFactory.get();
			gameModel.addObserver(this);
			resetGuiState();
		}
		return gameModel;
	}

	public PieceCell getCreationSelectedCell() {
		return creationSelectedCell;
	}

	public void setCreationSelectedCell(PieceCell creationSelectedCell) {
		if (guiState != GUIState.CREATE_PIECE_CREATION) {
			throw new IllegalGameStateException("Not allowed to be called in this state");
		}

		this.creationSelectedCell = creationSelectedCell;
		setGuiState(GUIState.CREATE_PIECE_ROTATION);
	}

	public List<PieceCell> calculateRotatedCopiesOfSelectedCell() {
		if (guiState != GUIState.CREATE_PIECE_ROTATION) {
			throw new IllegalGameStateException("Not allowed to be called in this state");
		}

		if (creationSelectedCellRotatedCopies == null) {
			creationSelectedCellRotatedCopies = getRotatedCopies(
					Objects.requireNonNull(creationSelectedCell)
			);
		}

		return creationSelectedCellRotatedCopies;
	}

	public Player getCurrentPlayer() {
		return gameModel.getCurrentPlayerData();
	}

	public Player getPlayerOfCellOrRotatedCopy(Cell cell) {
		if (creationSelectedCellRotatedCopies != null &&
				creationSelectedCellRotatedCopies.stream()
						.anyMatch(copy -> cell == copy)
				) {
			return getCurrentPlayer();
		}

		return gameModel.getPlayerOfCell(cell);
	}

	@Override
	public void update(Observable o, Object arg) {
		SwingUtilities.invokeLater(() -> {
			resetGuiState();
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
			creationSelectedCell = null;
			creationSelectedCellRotatedCopies = null;
		}

		this.guiState = guiState;
		setChanged();
		notifyObservers();
	}

	public void performGameAction(GameAction action) {
		try {
			action.perform();
		} catch (Exception e) {
			setChanged();
			notifyObservers(e);
		}
	}

	public void createPiece(Cell rotatedCellCopy) {
		if (guiState != GUIState.CREATE_PIECE_ROTATION) {
			return;
		}

		PieceCell rotatedPieceCopy = (PieceCell) rotatedCellCopy;
		PieceCell baseCell = creationSelectedCell;

		if (rotatedPieceCopy.getSideCombination() != baseCell.getSideCombination()) {
			throw new IllegalArgumentException("Somehow wrong cell was selected");
		}

		Direction direction = rotatedPieceCopy.getDirection();

		performGameAction(() -> gameModel.create(
				baseCell.getId(),
				direction.ordinal()
		));
	}

	@Override
	public Color process(Color color, Cell cell) {
		if (cell == creationSelectedCell) {
			return color.brighter();
		}

		return color;
	}

	private void resetGuiState() {
		List<GUIState> validStates = gameModel.getTurnState()
				.getFromMap(new TurnStateToGUIState());

		if (validStates.contains(guiState)) {
			// No need to change
			return;
		}

		setGuiState(validStates.get(0));
		setChanged();
		notifyObservers();
	}

	// TODO map from TurnState
	public enum GUIState {
		CREATE_PIECE_CREATION {
			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getCreatePieceCreationValue();
			}
		},
		CREATE_PIECE_ROTATION {
			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getCreatePieceRotationValue();
			}
		},
		MOVING_OR_ROTATING_PIECE {
			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getMovingOrRotatingPieceValue();
			}
		},
		RESOLVING_REACTIONS {
			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getResolvingReactionsValue();
			}
		},
		GAME_FINISHED {
			@Override
			public <T> T getFromMap(Mapper<T> mapper) {
				return mapper.getGameFinishedValue();
			}
		}
		;

		public abstract <T> T getFromMap(Mapper<T> mapper);

		public interface Mapper<ValueT> {
			ValueT getCreatePieceCreationValue();
			ValueT getCreatePieceRotationValue();
			ValueT getMovingOrRotatingPieceValue();
			ValueT getResolvingReactionsValue();
			ValueT getGameFinishedValue();
		}
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
					GUIState.MOVING_OR_ROTATING_PIECE
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
