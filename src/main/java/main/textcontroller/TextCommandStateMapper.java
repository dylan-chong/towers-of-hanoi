package main.textcontroller;

import main.gamemodel.*;
import main.gamemodel.cells.PieceCell;

import java.io.PrintStream;
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
	public static final String SEE_COMMAND = "see";

	private final GameModel game;
	private final PrintStream textOut;

	public TextCommandStateMapper(GameModel game, PrintStream textOut) {
		this.game = game;
		this.textOut = textOut;
	}

	@Override
	public TextCommandState getCreatingPiecesValue() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line)
					throws ParseFormatException, InvalidMoveException {
				String[] tokens = requireTokens(line, 1, 2, 3);
				String command = tokens[0];

				switch (command) {
					case SEE_COMMAND:
						PieceCell piece = game.getCurrentPlayerData()
								.findUnusedPiece(tokens[1].charAt(0));
						String pieceStr = Textable.convertToString(
								piece.toTextualRep(), true
						);
						textOut.println(pieceStr);
						break;
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
				String seeInstructions = String.format(
						"%s <%s>",
						SEE_COMMAND,
						String.join(
								"/",
								game.getCurrentPlayerData()
										.getUnusedPieceIds()
										.stream()
										.map(Object::toString)
										.sorted()
										.collect(Collectors.toList())
						)
				);
				String instructions = String.format(
						"You can:\n" +
								"- %s\n" +
								"- %s\n" +
								"- %s",
						commandInstructions,
						seeInstructions,
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

				if (mustPass()) {
					if (command.equals(PASS_COMMAND)) {
						game.passTurnState();
						return;
					} else {
						throw new ParseFormatException("Invalid command");
					}
				}

				requireTokens(line, 3);

				List<ReactionData.Pair> matches = game.getReactions()
						.stream()
						.filter(cellPair ->
								cellPair.dataA.cell.getId() == tokens[1].charAt(0) &&
								cellPair.dataB.cell.getId() == tokens[2].charAt(0)
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

				if (mustPass()) {
					instructions += "- " + PASS_COMMAND;
				} else {
					Collection<ReactionData.Pair> reactions = game.getReactions();
					Set<String> idPairs = new HashSet<>();
					for (ReactionData.Pair reactionPair : reactions) {
						idPairs.add(String.format(
								"%s %s",
								reactionPair.dataA.cell.getId(),
								reactionPair.dataB.cell.getId()
						));
					}
					instructions += String.format(
							"- %s <%s> // NOTE: Case sensitive",
							REACT_COMMAND,
							String.join(",", idPairs)
					);
				}

				instructions += "\n- " + UNDO_COMMAND;
				return instructions;
			}

			private boolean mustPass() {
				return game.getReactions().isEmpty();
			}
		};
	}

	@Override
	public TextCommandState getGameFinishedValue() {
		return new TextCommandState() {
			@Override
			public void parseAndExecute(String line) throws ParseFormatException, InvalidMoveException {
				String command = requireTokens(line, 1)[0];
				if (!command.equals(UNDO_COMMAND)) {
					throw new ParseFormatException("Invalid command");
				}

				game.undo();
			}

			@Override
			public String getInstructions() {
				char winnerId = game.getWinner()
						.getPlayerCell()
						.getId();
				return String.format("Player %c won!\n", winnerId) +
						"You can:\n- " + UNDO_COMMAND;
			}
		};
	}
}

