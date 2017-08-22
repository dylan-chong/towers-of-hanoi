package main.gamemodel.cells;

import main.gamemodel.Direction;
import main.gamemodel.cells.PieceCell.SideType;

/**
 * The player cell
 */
public class PlayerCell extends BoardCell {

	/**
	 * Char to go around {@link Token#representation} icon
	 */
	private static final char FILLER_CHAR = '+';

	private final Token token;

	public PlayerCell(Token token) {
		this.token = token;
	}

	/**
	 * @return a 3x3 grid of characters representing this piece -
	 * a textual representation
	 */
	@Override
	public char[][] toTextualRep() {
		char[][] representation = blankCellTextualRep();
		for (int r = 0; r < representation.length; r++) {
			for (int c = 0; c < representation[r].length; c++) {
				representation[r][c] = FILLER_CHAR;
			}
		}

		representation[1][1] = token.representation;
		return representation;
	}

	@Override
	public char getId() {
		return token.representation;
	}

	@Override
	protected Reaction getReactionToPieceCell(PieceCell cell, Direction fromThisToCell) {
		SideType side = cell.getSide(fromThisToCell.reversed());
		return side.getFromMap(new SideType.Mapper<Reaction>() {
			@Override
			public Reaction getEmptyValue() {
				return Reaction.DO_NOTHING;
			}

			@Override
			public Reaction getSwordValue() {
				return Reaction.LOSE_THE_GAME;
			}

			@Override
			public Reaction getShieldValue() {
				return Reaction.DO_NOTHING;
			}
		});
	}

	@Override
	protected Reaction getReactionToPlayerCell(PlayerCell cell, Direction fromThisToCell) {
		return Reaction.DO_NOTHING;
	}

	@Override
	Reaction getReactionToByVisiting(BoardCell cell, Direction fromCellToThis) {
		return cell.getReactionToPlayerCell(this, fromCellToThis);
	}

	@Override
	public <ReturnT> ReturnT accept(BoardCellGenericGetter<ReturnT> getter) {
		return getter.visitPlayerCell(this);
	}

	public String getName() {
		return String.format("%s (%s)", token.name(), token.representation);
	}

	public Token getToken() {
		return token;
	}

	public enum Token {
		HAPPY('0'),
		ANGRY('1');

		Token(char representation) {
			this.representation = representation;
		}

		public final char representation;
	}
}
