package test;

import main.gamemodel.cells.PieceCell;
import main.gamemodel.cells.PlayerCell;
import org.junit.Test;

import static test.TestUtils.assertRepresentationEquals;

public class BoardCellTest {

	@Test
	public void Piece_toTextualRep_noSwordOrShield_returnsGridWithIdInMiddle() {
		// By empty, I mean no swords or shields
		PieceCell piece = new PieceCell(
				'a', PieceCell.SideCombination.EMPTY_EMPTY_EMPTY_EMPTY
		);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				"   ".toCharArray(),
				" a ".toCharArray(),
				"   ".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void Piece_toTextualRep_withThreeSwords_returnsGridWithIdInMiddle() {
		PieceCell piece = new PieceCell(
				'a', PieceCell.SideCombination.SWORD_SWORD_SWORD_EMPTY
		);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				" | ".toCharArray(),
				"-a ".toCharArray(),
				" | ".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void Piece_toTextualRep_withTwoSwordsTwoShields_returnsGridWithIdInMiddle() {
		PieceCell piece = new PieceCell(
				'a', PieceCell.SideCombination.SWORD_SWORD_SHIELD_SHIELD
		);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				" | ".toCharArray(),
				"-a#".toCharArray(),
				" # ".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

	@Test
	public void Player_toTextualRep_happyPlayer_returnsGridWithTokenInMiddle() {
		// By empty, I mean no swords or shields
		PlayerCell.Token token = PlayerCell.Token.HAPPY;
		PlayerCell piece = new PlayerCell(token);
		char[][] representation = piece.toTextualRep();
		char[][] expected = {
				"+++".toCharArray(),
				String.format("+%s+", PlayerCell.Token.HAPPY.representation).toCharArray(),
				"+++".toCharArray()
		};
		assertRepresentationEquals(expected, representation);
	}

}
