package main;

import java.io.PrintStream;
import java.util.Scanner;

public class GameTextController {

	private final Scanner textIn;
	private final PrintStream textOut;
	private final GameModel game;

	public GameTextController(Scanner textIn,
							  PrintStream textOut,
							  GameModel game) {
		this.textIn = textIn;
		this.textOut = textOut;
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
			} catch (ParseFormatException e) {
				textOut.println(e.getMessage());
			}

			textOut.println(getGameString());
		}
	}

	private String getGameString() {
		return TextualRepresentable.convertToString(
				this.game.toTextualRep(), true
		);
	}

	private void parseAndRunLine(String line) throws ParseFormatException {
		String[] tokens = line.split(" ");
		String commandName = tokens[0];

		switch (commandName) {
			case "create":
				char pieceID = tokens[1].charAt(0);
				int orientation = Integer.parseInt(tokens[2]);
				game.create(pieceID, orientation);
				break;
			default:
				throw new ParseFormatException("Invalid command name");
		}
	}

	private static class ParseFormatException extends Exception {
		public ParseFormatException(String message) {
			super(message);
		}
	}
}

