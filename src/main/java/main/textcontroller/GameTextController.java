package main.textcontroller;

import main.ExceptionHandler;
import main.Main;
import main.gamemodel.*;

import java.io.PrintStream;
import java.util.Scanner;

public class GameTextController {

	private final Scanner textIn;
	private final PrintStream textOut;
	private final ExceptionHandler exceptionHandler;
	private final TextCommandStateMapper commandProvider;
	private final GameModel game;

	public GameTextController(Scanner textIn,
							  PrintStream textOut,
							  ExceptionHandler exceptionHandler,
							  TextCommandStateMapper commandProvider,
							  GameModel game) {
		this.textIn = textIn;
		this.textOut = textOut;
		this.exceptionHandler = exceptionHandler;
		this.commandProvider = commandProvider;
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
			TextCommandState command = turnState.getFromMap(commandProvider);

			try {
				command.parseAndExecute(line);
			} catch (ParseFormatException |
					InvalidMoveException |
					IllegalGameStateException e) {
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
				.getFromMap(commandProvider)
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

}

