package main.gamemodel;

/**
 * The state of the current players turn. It is the job of the controllers
 * to call the correct methods of the model for the current state
 *
 * This uses a visitor-like pattern to ensure that all controllers
 * implement a command for enum value. (See {@link Mapper}).
 */
public enum TurnState {
	CREATING_PIECE {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getCreatingPiecesCommand();
		}
	},
	MOVING_OR_ROTATING_PIECE {
		@Override
		public <T> T getFromMap(Mapper<T> mapper) {
			return mapper.getMovingOrRotatingPieceCommand();
		}
	},
	;

	public abstract <T> T getFromMap(Mapper<T> mapper);

	public interface Mapper<T> {
		T getCreatingPiecesCommand();
		T getMovingOrRotatingPieceCommand();
	}
}
