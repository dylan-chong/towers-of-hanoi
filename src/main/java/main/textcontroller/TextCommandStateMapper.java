package main.textcontroller;

import main.gamemodel.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextCommandStateMapper
		implements TurnState.Mapper<TextCommandState> {

	public static final String PASS_COMMAND = "pass";
	public static final String MOVE_COMMAND = "move";
	public static final String CREATE_COMMAND = "create";
	public static final String ROTATE_COMMAND = "rotate";
	public static final String UNDO_COMMAND = "undo";
	public static final String REACT_COMMAND = "react";

	private final GameModel game;

	public TextCommandStateMapper(GameModel game) {
		this.game = game;
	}

	@Override
	public TextCommandState getCreatingPiecesValue() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(line, 1, 3);
				String command = tokens[0];

				// TODO add see piece

				switch (command) {
					case PASS_COMMAND:
						game.passTurnState();
						break;
					case CREATE_COMMAND:
						char pieceID = tokens[1].charAt(0);
						game.create(pieceID, rotationsFromDegrees(tokens[2]));
						break;
					case UNDO_COMMAND:
						game.undo();
						break;
					default:
						throw new ParseFormatException("Invalid command name");
				}
			}

			@Override
			public String getInstructions() {
				String commandInstructions = commandInstructions(
						CREATE_COMMAND,
						game.getCurrentPlayerData().getUnusedPieceIds(),
						direction -> direction.degrees() + ""
				);
				String instructions = String.format(
						"You can:\n" +
								"- %s\n" +
								"- %s",
						commandInstructions,
						PASS_COMMAND
				);
				if (game.canUndo()) {
					instructions += "\n- " + UNDO_COMMAND;
				}
				return instructions;
			}
		};
	}

	@Override
	public TextCommandState getMovingOrRotatingPieceValue() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(line, 1, 3);
				String command = tokens[0];

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
					case UNDO_COMMAND: {
						game.undo();
						break;
					}
					default:
						throw new ParseFormatException("Invalid command name");
				}
			}

			@Override
			public String getInstructions() {
				String instructions = "You can:" +
						"\n- " + PASS_COMMAND;

				Collection<Character> pieceIds = game.getPlayablePieceIds();
				if (!pieceIds.isEmpty()) {
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
					instructions += "\n- " + moveInstructions +
							"\n- " + rotateInstructions;
				}

				if (game.canUndo()) {
					instructions += "\n- " + UNDO_COMMAND;
				}

				return instructions;
			}

		};
	}

	@Override
	public TextCommandState getResolvingReactionsValue() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(line, 1, 3);
				String command = tokens[0];

				if (command.equals(UNDO_COMMAND)) {
					game.undo();
					return;
				}

				List<Board.CellPair> matches = game.getReactions()
						.stream()
						.filter(cellPair ->
								cellPair.getCellAId() == tokens[1].charAt(0) &&
								cellPair.getCellBId() == tokens[2].charAt(0)
						)
						.collect(Collectors.toList());
				if (matches.isEmpty()) {
					throw new ParseFormatException("No match for ids");
				} else if (matches.size() > 1) {
					throw new IllegalStateException(
							"There are multiple matches for the given ids " +
									"for some reason"
					);
				}

				game.react(matches.get(0));
			}

			@Override
			public String getInstructions() {
				String instructions = "You can:\n";

				Set<Board.CellPair> reactions = game.getReactions();
				Set<String> idPairs = new HashSet<>();
				for (Board.CellPair reaction : reactions) {
					idPairs.add(reaction.getCellAId() + " " + reaction.getCellBId());
				}
				instructions += String.format(
						"- %s <%s> // NOTE: Case sensitive",
						REACT_COMMAND,
						String.join(",", idPairs)
				);

				instructions += "\n- " + UNDO_COMMAND;
				return instructions;
			}
		};
	}
}

