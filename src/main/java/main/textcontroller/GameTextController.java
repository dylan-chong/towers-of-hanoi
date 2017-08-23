package main.textcontroller;

import main.ExceptionHandler;
import main.GameUtils;
import main.Main;
import main.gamemodel.*;
import main.gamemodel.cells.BoardCell;

import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameTextController {

	private static final boolean WITH_GAP = true;

	private final Scanner textIn;
	private final PrintStream textOut;
	private final ExceptionHandler exceptionHandler;
	private final TextCommandStateMapper commandProvider;
	private final GameModel game;

	public GameTextController(Scanner textIn,
							  PrintStream textOut,
							  ExceptionHandler exceptionHandler,
							  GameModel game) {
		this.textIn = textIn;
		this.textOut = textOut;
		this.exceptionHandler = exceptionHandler;
		this.commandProvider = new TextCommandStateMapper(game, textOut);
		this.game = game;
	}

	/**
	 * Reads the user input from the input stream until the game ends
	 */
	public void runUntilGameEnd() {
		textOut.println(getGameString());

		while (textIn.hasNext()) {
			String line = textIn.nextLine();
			textOut.println(line);

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
				if (!(e instanceof ArrayIndexOutOfBoundsException) &&
                        e.getMessage() != null) {
					message += ": " + e.getMessage();
				}
				exceptionHandler.handle(new Exception(message, e));
			}

			textOut.println(getGameString());
		}
	}

	private String getGameString() {
		String playerName = game.getCurrentPlayerData().getName();
		String gameStateName = game.getTurnState()
				.name()
				.replaceAll("_", " ")
				.trim();
		String instructions = game.getTurnState()
				.getFromMap(commandProvider)
				.getInstructions();

		return getGameRepresentation() +
				"\nThe current player is: " + playerName +
				"\nThe current state is: " + gameStateName +
				"\n" + instructions;
	}

	private String getGameRepresentation() {
		PlayerData player = game.getCurrentPlayerData();

		StringBuilder representation = new StringBuilder(
				Textable.convertToString(game.toTextualRep(), WITH_GAP)
		);

		if (game.getTurnState() == TurnState.CREATING_PIECE) {
			List<List<BoardCell>> unusedCells = GameUtils.packCells(
					player.getUnusedPieceIds()
							.stream()
							.map(player::findUnusedPiece)
							.sorted()
							.collect(Collectors.toList()),
					Board.DEFAULT_NUM_COLS
			);
			representation.append("\nYour unused cells:\n");
			for (List<BoardCell> pieceList : unusedCells) {
				char[][] rep = Textable.copyRowIntoRep(pieceList);
				representation.append(Textable.convertToString(rep, WITH_GAP));
			}
		}

		List<List<BoardCell>> deadPieces = GameUtils.packCells(
				game.getPlayers()
						.stream()
						.flatMap(playerData -> playerData.getDeadPieceIds()
								.stream()
								.map(playerData::findDeadPiece)
								.sorted()
						)
						.collect(Collectors.toList()),
				Board.DEFAULT_NUM_COLS
		);
		if (deadPieces.stream().mapToLong(Collection::size).sum() > 0) {
			representation.append("\nCemetery\n");
			for (List<BoardCell> pieceList : deadPieces) {
				char[][] rep = Textable.copyRowIntoRep(pieceList);
				representation.append(Textable.convertToString(rep, WITH_GAP));
			}
		}

		return representation.toString();
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
