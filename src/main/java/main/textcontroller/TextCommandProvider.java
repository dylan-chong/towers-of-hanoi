package main.textcontroller;

import main.gamemodel.Direction;
import main.gamemodel.GameModel;
import main.gamemodel.InvalidMoveException;
import main.gamemodel.TurnState;

public class TextCommandProvider
		implements TurnState.CommandProvider<TextCommand> {

	public static final String PASS_COMMAND = "pass";
	public static final String MOVE_COMMAND = "move";
	public static final String CREATE_COMMAND = "create";
	public static final String ROTATE_COMMAND = "rotate";

	private final GameModel game;

	public TextCommandProvider(GameModel game) {
		this.game = game;
	}

	@Override
	public TextCommand getCreatingPiecesCommand() {
		return new TextCommand() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(1, 3, line);
				String command = tokens[0];

				if (command.equals(PASS_COMMAND)) {
					game.passTurn();
					return;
				}

				if (!command.equals(CREATE_COMMAND)) {
					throw new ParseFormatException("Invalid command name");
				}

				char pieceID = tokens[1].charAt(0);

				game.create(pieceID, rotationsFromDegrees(tokens[2]));
			}

			@Override
			public String getInstructions() {
				String commandInstructions = commandInstructions(
						CREATE_COMMAND,
						game.getCurrentPlayerData().getUnusedPieceIds(),
						direction -> direction.degrees() + ""
				);
				return String.format(
						"You can:\n- %s\n" +
								"- %s",
						commandInstructions,
						PASS_COMMAND
				);
			}
		};
	}

	@Override
	public TextCommand getMovingOrRotatingPieceCommand() {
		return new TextCommand() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(1, 3, line);
				String command = tokens[0];

				if (mustPass()) {
					if (command.equals(PASS_COMMAND)) {
						game.passTurn();
						return;
					}

					throw new ParseFormatException("Invalid command name");
				}

				char pieceID = tokens[1].charAt(0);

				switch (command) {
					case MOVE_COMMAND: {
						Direction direction = Direction.valueOfAlternateName(tokens[2]);
						game.move(pieceID, direction);
						break;
					}
					case ROTATE_COMMAND: {
						game.rotate(pieceID, rotationsFromDegrees(tokens[2]));
						break;
					}
					default:
						throw new ParseFormatException("Invalid command name");
				}
			}

			@Override
			public String getInstructions() {
				if (mustPass()) {
					return "You can:\n- " + PASS_COMMAND;
				}

				String moveInstructions = commandInstructions(
						MOVE_COMMAND,
						game.getCurrentPlayerData().getUsedPieceIds(),
						direction -> direction.getAlternateName().toLowerCase()
				);
				String rotateInstructions = commandInstructions(
						ROTATE_COMMAND,
						game.getCurrentPlayerData().getUsedPieceIds(),
						direction -> direction.degrees() + ""
				);
				return "You can:" +
						"\n- " + moveInstructions +
						"\n- " + rotateInstructions;
			}

			public boolean mustPass() {
				return game.getCurrentPlayerData()
						.getUsedPieceIds()
						.isEmpty();
			}
		};
	}
}

