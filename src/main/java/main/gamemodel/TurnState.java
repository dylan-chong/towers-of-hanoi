package main.gamemodel;

/**
 * The state of the current players turn. It is the job of the controllers
 * to call the correct methods of the model for the current state
 *
 * This uses a visitor-like pattern to ensure that all controllers
 * implement a command for enum value.
 */
public enum TurnState {
	CREATING_PIECE {
		@Override
		public <CommandT> CommandT getCommand(CommandProvider<CommandT> provider) {
			return provider.getCreatingPiecesCommand();
		}

		@Override
		public boolean canMoveToNextState(GameModel gameModel) {
			return true;
		}
	},
	MOVING_OR_ROTATING_PIECE {
		@Override
		public <CommandT> CommandT getCommand(CommandProvider<CommandT> provider) {
			return provider.getMovingOrRotatingPieceCommand();
		}

		@Override
		public boolean canMoveToNextState(GameModel gameModel) {
			return true;
		}
	},
	;

	public abstract <CommandT> CommandT getCommand(CommandProvider<CommandT> provider);

	public abstract boolean canMoveToNextState(GameModel gameModel);
	
	public interface CommandProvider<CommandT> {
		CommandT getCreatingPiecesCommand();
		CommandT getMovingOrRotatingPieceCommand();
	}
}
