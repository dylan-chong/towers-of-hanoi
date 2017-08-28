package main.gui.game;

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
	MOVING_OR_ROTATING_PIECE_SELECTION {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getMovingOrRotatingPieceSelectionValue();
		}
	},
	MOVING_OR_ROTATING_PIECE_APPLYING {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getMovingOrRotatingPieceApplyingValue();
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
		ValueT getMovingOrRotatingPieceSelectionValue();
		ValueT getMovingOrRotatingPieceApplyingValue();
		ValueT getResolvingReactionsValue();
		ValueT getGameFinishedValue();
	}
}
