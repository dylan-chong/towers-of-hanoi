package test;

import main.Board;
import main.TextBoardController;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Scanner;

import static junit.framework.TestCase.assertTrue;

public class TextBoardControllerTest {

	@Test
	public void listenUntilGameEnd_enterNothing_boardIsStillEmpty() {
		String input = "";
		Board board = new Board();
		assertTrue(board.isEmpty()); // make sure test is valid

		TextBoardController textBoardController = new TextBoardController(
				new Scanner(new StringReader(input)),
				new PrintStream(new ByteArrayOutputStream()),
				board
		);
		textBoardController.listenUntilGameEnd();

		assertTrue(board.isEmpty());
	}

	@Test
	public void listenUntilGameEnd_create_createMethodIsCalled() {
		// TODO
		// String input = "create a 0";
		// Board board = new Board();
        //
		// TextBoardController textBoardController = new TextBoardController(
		// 		new Scanner(new StringReader(input)),
		// 		new PrintStream(new ByteArrayOutputStream()),
		// 		board
		// );
		// textBoardController.listenUntilGameEnd();
        //
		// assertEquals(board._getCellAt());
	}


}
