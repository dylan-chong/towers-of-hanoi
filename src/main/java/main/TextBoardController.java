package main;

import java.io.PrintStream;
import java.util.Scanner;

public class TextBoardController {

	private final Scanner textIn;
	private final PrintStream textOut;

	public TextBoardController(Scanner textIn,
							   PrintStream textOut,
							   Board board) {
		this.textIn = textIn;
		this.textOut = textOut;
	}

	/**
	 * Reads the user input from the input stream until the game ends
	 */
	public void listenUntilGameEnd() {
		while (textIn.hasNext()) {
			String line = textIn.nextLine();
			//TODO
		}
	}
}

