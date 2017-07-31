package test;

import main.GameModel;
import main.GameTextController;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Scanner;

public class GameTextControllerTest {

	@Test
	public void runUntilGameEnd_typeCreateCommand_gameCreateMethodCalled() {
		String input = "create a 0";
		GameModel gameSpy = Mockito.mock(GameModel.class);
		// Stub toTextualRep to prevent converting actual data to string
		Mockito.when(gameSpy.toTextualRep()).thenReturn(new char[1][1]);
		GameTextController textBoardController = new GameTextController(
				new Scanner(new StringReader(input)),
				new PrintStream(new ByteArrayOutputStream()),
				gameSpy
		);

		textBoardController.runUntilGameEnd();

		Mockito.verify(gameSpy).create('a', 0);
	}

	// TODO other commands

}
