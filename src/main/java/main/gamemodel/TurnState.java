package main.gamemodel;

/**
 * The state of the current players turn. It is the job of the controllers
 * to call the correct methods of the model for the current state
 *
 * This uses a visitor-like pattern to ensure that all users of the enum
 * implement a mapping for every enum value, and creates compile time errors
 * when a new value is added (which are resolved by handling the new value).
 *
 * @see Mapper
 */
public enum TurnState {
	CREATING_PIECE {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getCreatingPiecesValue();
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
	},
	;

	public abstract <T> T getFromMap(Mapper<T> mapper);

	public interface Mapper<ValueT> {
		ValueT getCreatingPiecesValue();
		ValueT getMovingOrRotatingPieceValue();
		ValueT getResolvingReactionsValue();
		ValueT getGameFinishedValue();
	}
}
