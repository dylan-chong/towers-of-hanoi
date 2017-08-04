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
	},
	MOVING_OR_ROTATING_PIECE {
		@Override
		public <CommandT> CommandT getCommand(CommandProvider<CommandT> provider) {
			return provider.getMovingOrRotatingPieceCommand();
		}
	},
	;

	public abstract <CommandT> CommandT getCommand(CommandProvider<CommandT> provider);

	public interface CommandProvider<CommandT> {
		CommandT getCreatingPiecesCommand();
		CommandT getMovingOrRotatingPieceCommand();
	}
}
