package main;

import main.gamemodel.*;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class GameTextController {

	private static final String PASS_COMMAND = "pass";
	private static final String MOVE_COMMAND = "move";
	private static final String CREATE_COMMAND = "create";

	private final CommandProvider commandProvider = new CommandProvider();

	private final Scanner textIn;
	private final PrintStream textOut;
	private final ExceptionHandler exceptionHandler;
	private final GameModel game;

	public GameTextController(Scanner textIn,
							  PrintStream textOut,
							  ExceptionHandler exceptionHandler,
							  GameModel game) {
		this.textIn = textIn;
		this.textOut = textOut;
		this.exceptionHandler = exceptionHandler;
		this.game = game;
	}

	/**
	 * Reads the user input from the input stream until the game ends
	 */
	public void runUntilGameEnd() {
		textOut.println(getGameString());

		while (textIn.hasNext()) {
			String line = textIn.nextLine();

			TurnState turnState = game.getTurnState();
			TurnStateCommand command = turnState.getCommand(commandProvider);

			try {
				command.parseAndExecute(line);
			} catch (ParseFormatException | InvalidMoveException e) {
				exceptionHandler.handle(e);
			} catch (RuntimeException | Error e) {
				String message = "There was an error making your move";
				if (e.getMessage() != null) {
					message += ": " + e.getMessage();
				}
				exceptionHandler.handle(new Exception(message, e));
			}

			textOut.println(getGameString());
		}
	}

	private String getGameString() {
		String gameRep = Textable.convertToString(game.toTextualRep(), true);

		String playerName = game.getCurrentPlayerData().getName();

		String instructions = game.getTurnState()
				.getCommand(commandProvider)
				.getInstructions();

		return gameRep +
				"\nThe current player is: " + playerName +
				"\n" + instructions;
	}

	public static class AppExceptionHandler implements ExceptionHandler {
		private final PrintStream out;

		public AppExceptionHandler(PrintStream out) {
			this.out = out;
		}

		@Override
		public void handle(Throwable throwable) {
			if (Main.areAssertionsEnabled()) {
				throwable.printStackTrace(out);
			} else {
				out.println(throwable.getMessage());
			}
		}
	}

	private static class ParseFormatException extends Exception {
		public ParseFormatException(String message) {
			super(message);
		}
	}

	private abstract class TurnStateCommand {

		/**
		 * @param line The text the user entered
		 */
		public abstract void parseAndExecute(String line)
				throws ParseFormatException, InvalidMoveException;

		/**
		 * Instructions to display to the user
		 */
		public abstract String getInstructions();

		protected String[] requireTokens(int minTokens,
										 int maxTokens,
										 String line)
				throws ParseFormatException {

			String[] tokens = Arrays
					.stream(line.split(" "))
					.map(String::trim)
					.filter(token -> token.length() > 0)
					.toArray(String[]::new);

			if (tokens.length < minTokens || tokens.length > maxTokens) {
				throw new ParseFormatException("Invalid number of tokens");
			}

			return tokens;
		}

		protected String commandInstructions(String cmdName,
											 Collection<Character> pieceIds) {
			List<String> sortedIds = pieceIds.stream()
					.map(Object::toString)
					.sorted()
					.collect(Collectors.toList());
			List<String> directions = Arrays.stream(AbsDirection.values())
					.map(AbsDirection::getAlternateName)
					.map(String::toLowerCase)
					.collect(Collectors.toList());

			return String.format(
					"%s <%s> <%s>",
					cmdName,
					String.join("/", sortedIds),
					String.join("/", directions)
			);
		}
	}

	private class CommandProvider
			implements TurnState.CommandProvider<TurnStateCommand> {
		@Override
		public TurnStateCommand getCreatingPiecesCommand() {
			return new TurnStateCommand() {
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

					AbsDirection orientation = AbsDirection.valueOfAlternateName(tokens[2]);
					game.create(pieceID, orientation);
				}

				@Override
				public String getInstructions() {
					return "You can:\n- " + commandInstructions(
							"create",
							game.getCurrentPlayerData().getUnusedPieceIds()
					);
				}
			};
		}

		@Override
		public TurnStateCommand getMovingOrRotatingPieceCommand() {
			return new TurnStateCommand() {
				@Override
				public void parseAndExecute(String line)
						throws ParseFormatException, InvalidMoveException {
					String[] tokens = requireTokens(1, 3, line);
					String command = tokens[0];

					if (command.equals(PASS_COMMAND)) {
						game.passTurn();
						return;
					}

                    if (!command.equals(MOVE_COMMAND)) {
						throw new ParseFormatException("Invalid command name");
					}

					// TODO: 3/08/17 rotate

					char pieceID = tokens[1].charAt(0);

					AbsDirection orientation = AbsDirection.valueOfAlternateName(tokens[2]);
					game.move(pieceID, orientation);
				}

				@Override
				public String getInstructions() {
					return "You can:\n- " + commandInstructions(
							"move",
							game.getCurrentPlayerData().getUsedPieceIds()
					);
				}
			};
		}
	}
}

