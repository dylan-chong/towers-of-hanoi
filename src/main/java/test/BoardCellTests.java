package test;

import main.Piece;
import main.Player;
import org.junit.Test;

import static test.TestUtils.assertRepresentationEquals;

public class BoardCellTests {

	@Test
	public void Piece_toTextualRep_noSwordOrShield_returnsGridWithIdInMiddle() {
		// By empty, I mean no swords or shields
		Piece piece = new Piece(
				'a', Piece.SideCombination.EMPTY_EMPTY_EMPTY_EMPTY, 1, 1
		);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				"...".toCharArray(),
				".a.".toCharArray(),
				"...".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void Piece_toTextualRep_withThreeSwords_returnsGridWithIdInMiddle() {
		Piece piece = new Piece(
				'a', Piece.SideCombination.SWORD_SWORD_SWORD_EMPTY, 1, 1
		);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				".|.".toCharArray(),
				"-a.".toCharArray(),
				".|.".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void Piece_toTextualRep_withTwoSwordsTwoShields_returnsGridWithIdInMiddle() {
		Piece piece = new Piece(
				'a', Piece.SideCombination.SWORD_SWORD_SHIELD_SHIELD, 1, 1
		);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				".|.".toCharArray(),
				"-a#".toCharArray(),
				".#.".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void Player_toTextualRep_happyPlayer_returnsGridWithTokenInMiddle() {
		// By empty, I mean no swords or shields
		Player.Token token = Player.Token.HAPPY;
		Player piece = new Player(token, 1, 1);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				"...".toCharArray(),
				String.format(".%s.", Player.Token.HAPPY.representation).toCharArray(),
				"...".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

}
