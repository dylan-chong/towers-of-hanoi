package main.textcontroller;

import main.gamemodel.Direction;
import main.gamemodel.GameModel;
import main.gamemodel.InvalidMoveException;
import main.gamemodel.TurnState;

import java.util.Collection;

public class TextCommandStateMapper
		implements TurnState.Mapper<TextCommandState> {

	public static final String PASS_COMMAND = "pass";
	public static final String MOVE_COMMAND = "move";
	public static final String CREATE_COMMAND = "create";
	public static final String ROTATE_COMMAND = "rotate";

	private final GameModel game;

	public TextCommandStateMapper(GameModel game) {
		this.game = game;
	}

	@Override
	public TextCommandState getCreatingPiecesCommand() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(line, 1, 3);
				String command = tokens[0];

				if (command.equals(PASS_COMMAND)) {
					game.passTurnState();
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
	public TextCommandState getMovingOrRotatingPieceCommand() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(line, 1, 3);
				String command = tokens[0];

				if (mustPass()) {
					if (command.equals(PASS_COMMAND)) {
						game.passTurnState();
						return;
					}

					throw new ParseFormatException("Invalid command name");
				}

				switch (command) {
					case MOVE_COMMAND: {
						char pieceID = tokens[1].charAt(0);
						Direction direction = Direction.valueOfAlternateName(tokens[2]);
						game.move(pieceID, direction);
						break;
					}
					case ROTATE_COMMAND: {
						char pieceID = tokens[1].charAt(0);
						game.rotate(pieceID, rotationsFromDegrees(tokens[2]));
						break;
					}
					case PASS_COMMAND: {
						game.passTurnState();
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

				Collection<Character> pieceIds = game.getPlayablePieceIds();
				String moveInstructions = commandInstructions(
						MOVE_COMMAND,
						pieceIds,
						direction -> direction.getAlternateName().toLowerCase()
				);
				String rotateInstructions = commandInstructions(
						ROTATE_COMMAND,
						pieceIds,
						direction -> direction.degrees() + ""
				);
				return "You can:" +
						"\n- " + moveInstructions +
						"\n- " + rotateInstructions +
						"\n- " + PASS_COMMAND;

				// TODO allow pass
			}

			public boolean mustPass() {
				return game.getPlayablePieceIds().isEmpty();
			}
		};
	}
}

