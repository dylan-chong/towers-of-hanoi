package test;

import main.AbsDirection;
import main.Board;
import main.GameModel;
import main.GameTextController;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class GameTextControllerTest {

	@Test
	public void runUntilGameEnd_typeCreateCommand_gameCreateMethodCalled()
			throws Exception {
		List<String> input = Collections.singletonList("create a up");

		new TestRunUntilGameEnd(input) {
			@Override
			public void verify(GameModel gameSpy) throws Exception {
				Mockito.verify(gameSpy).create('a', AbsDirection.NORTH);
			}
		}.run();
	}

	@Test
	public void runUntilGameEnd_typeMoveCommand_gameMoveMethodCalled()
			throws Exception {
		List<String> input = Arrays.asList("create a up", "move a up");

		new TestRunUntilGameEnd(input) {
			@Override
			public void verify(GameModel gameSpy) throws Exception {
				Mockito.verify(gameSpy).move('a', AbsDirection.NORTH);
			}
		}.run();
	}

	// TODO other commands

	private abstract static class TestRunUntilGameEnd {
		private final List<String> inputLines;

		public TestRunUntilGameEnd(List<String> inputLines) {
			this.inputLines = inputLines;
		}

		public void run() throws Exception {
			GameModel gameSpy = Mockito.spy(new GameModel(new Board()));

			GameTextController textBoardController = new GameTextController(
					new Scanner(new StringReader(
							String.join("\n", inputLines)
					)),
					new PrintStream(new ByteArrayOutputStream()),
					throwable -> {
						throw new Error(throwable);
					},
					gameSpy
			);

			textBoardController.runUntilGameEnd();

			verify(gameSpy);
		}

		/**
		 * Run the Mockito.verify (assertions)
		 */
		protected abstract void verify(GameModel gameSpy) throws Exception;
	}
}
