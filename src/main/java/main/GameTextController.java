package main;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class GameTextController {

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
			try {
				parseAndRunLine(line);
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
		return Textable.convertToString(
				this.game.toTextualRep(), true
		);
	}

	private void parseAndRunLine(String line)
			throws ParseFormatException, InvalidMoveException {
		String[] tokens = Arrays
				.stream(line.split(" "))
				.map(String::trim)
				.filter(token -> token.length() > 0)
				.toArray(String[]::new);

		String commandName = tokens[0];

		switch (commandName) {
			case "create": {
				if (tokens.length != 3) {
					throw new ParseFormatException("Invalid number of tokens");
				}
				char pieceID = tokens[1].charAt(0);
				int orientation = Integer.parseInt(tokens[2]);
				game.create(pieceID, orientation);
				break;
			}
			case "move": {
				if (tokens.length != 3) {
					throw new ParseFormatException("Invalid number of tokens");
				}
				char pieceID = tokens[1].charAt(0);
				AbsDirection orientation = AbsDirection.valueOfAlternateName(tokens[2]);
				game.move(pieceID, orientation);
				break;
			}
			default:
				throw new ParseFormatException("Invalid command name");
		}
	}

	public static class ProductionExceptionHandler implements ExceptionHandler {
		private final PrintStream out;

		public ProductionExceptionHandler(PrintStream out) {
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

}

