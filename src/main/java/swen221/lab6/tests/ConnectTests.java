package swen221.lab6.tests;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.MethodSorters;

import swen221.lab6.connect.Game;
import swen221.lab6.connect.core.Board;
import swen221.lab6.connect.util.Position;

import org.junit.FixMethodOrder;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectTests {

	@Test public void test_01() {
		String output = "|_|_|_|_|\n" +
						"|_|_|_|_|\n" +
						"|_|_|_|_|\n" +
						"|_|_|_|_|\n";

		Board board = new Board();

		assertEquals(output,board.toString());
	}

}
