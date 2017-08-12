package test;

import main.gamemodel.Board;
import main.gamemodel.Direction;
import main.gamemodel.GameModel;
import main.gamemodel.TurnState;
import main.gamemodel.cells.PlayerCell;
import main.textcontroller.GameTextController;
import main.textcontroller.TextCommandStateMapper;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class GameTextControllerTest {

	@Test
	public void runUntilGameEnd_typeCreateCommand_gameCreateMethodCalled()
			throws Exception {
		List<String> input = Collections.singletonList("create a 0");

		new TestRunUntilGameEnd(input) {
			@Override
			public void runVerifications(GameModel gameSpy) throws Exception {
				verify(gameSpy).create('a', 0);
			}
		}.run();
	}

	@Test
	public void runUntilGameEnd_typeMoveCommand_gameMoveMethodCalled()
			throws Exception {
		List<String> input = Arrays.asList("create a 0", "move a up");

		new TestRunUntilGameEnd(input) {
			@Override
			public void runVerifications(GameModel gameSpy) throws Exception {
				verify(gameSpy).move('a', Direction.NORTH);
			}
		}.run();
	}

	@Test
	public void runUntilGameEnd_typePassCommand_gamePassMethodCalled()
			throws Exception {
		List<String> input = Arrays.asList(
				TextCommandStateMapper.PASS_COMMAND,
				TextCommandStateMapper.PASS_COMMAND
		);

		new TestRunUntilGameEnd(input) {
			@Override
			public void runVerifications(GameModel gameSpy) throws Exception {
				verify(gameSpy, times(2)).passTurnState();
				verify(gameSpy, never()).create(anyChar(), anyInt());
			}
		}.run();
	}

	@Test
	public void runUntilGameEnd_typeRotateCommand_gameRotateMethodCalled()
			throws Exception {
		List<String> input = Arrays.asList(
				"create a 0",
				"rotate a 90"
		);

		new TestRunUntilGameEnd(input) {
			@Override
			public void runVerifications(GameModel gameSpy) throws Exception {
				verify(gameSpy).rotate('a', 1);
			}
		}.run();
	}

	@Test
	public void runUntilGameEnd_player1Dies_player0Wins()
			throws Exception {
		List<String> input = Arrays.asList(
				"create A 0",
				"move A up",
				"react 1 A",
				"undo",
				"react 1 A",
				"undo",
				"react 1 A"
		);

		new TestRunUntilGameEnd(input) {
			@Override
			public void runVerifications(GameModel gameSpy) throws Exception {
				assertEquals(TurnState.GAME_FINISHED, gameSpy.getTurnState());
				assertEquals(
						PlayerCell.Token.HAPPY,
						gameSpy.getWinner()
								.getPlayerCell()
								.getToken()
				);
			}
		}.run();
	}

	@Test
	public void runUntilGameEnd_shieldTouchingShield_noReaction()
			throws Exception {
		List<String> input = Arrays.asList(
				"create C 0",
				"move C down",
				"pass",

				"create a 0",
				"move a up",
				"pass",

				"create D 0" // D and C's shields touch
		);

		new TestRunUntilGameEnd(input) {
			@Override
			public void runVerifications(GameModel gameSpy) throws Exception {
				assertNotEquals(TurnState.RESOLVING_REACTIONS, gameSpy.getTurnState());
			}
		}.run();
	}

	/**
	 * Yea i know this is really lazy, but i was running out of time!
	 */
	@Test
	public void runUntilGameEnd_gameInputsWithReactions_doesntCrash() {
		// noinspection RedundantArrayCreation because trailing commas!
		runUntilGameEnd_forInputLines_doesntCrash(new String[]{
				"see A",
				"create A 0",
				"move A down",
				"undo",
				"move A down",
				"pass",

				"create a 0",
				"move a up",
				"pass",

				"create B 0",
				"react B A",
				"undo",
				"react B A",
				"undo",
				"react B A",
				"pass",
				"pass",

				"create c 0",
				"react a c",
				"undo",
				"react a c",
				"undo",
				"react a c",
				"pass",
        });
	}

	private static void runUntilGameEnd_forInputLines_doesntCrash(String... inputLines) {
		runUntilGameEnd_forInputLines_doesntCrash(false, inputLines);
	}

	private static void runUntilGameEnd_forInputLines_doesntCrash(
			boolean printToStandardOut,
			String... inputLines
	) {
		GameModel game = new GameModel(new Board());
		GameTextController textBoardController = new GameTextController(
				new Scanner(new StringReader(
						String.join("\n", inputLines)
				)),
				printToStandardOut ?
						System.out :
						new PrintStream(new ByteArrayOutputStream()),
				throwable -> {
					throw new Error(throwable);
				},
				game
		);
		textBoardController.runUntilGameEnd();
	}

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
					getTextOutput(),
					throwable -> {
						throw new Error(throwable);
					},
					gameSpy
			);

			textBoardController.runUntilGameEnd();

			runVerifications(gameSpy);
		}

		protected PrintStream getTextOutput() {
			return new PrintStream(new ByteArrayOutputStream());
		}

		/**
		 * Run the Mockito.runVerifications (assertions)
		 */
		protected abstract void runVerifications(GameModel gameSpy) throws Exception;
	}
}
