package swen221.lab6.connect.rules;

import swen221.lab6.connect.Game;
import swen221.lab6.connect.Game.Status;
import swen221.lab6.connect.core.Board;
import swen221.lab6.connect.core.Rule;
import swen221.lab6.connect.util.Position;

/**
 * Checks whether or not the game has been won by getting a horizontal row of
 * the same tokens.
 * 
 * @author David J. Pearce
 *
 */
public class RowWinRule implements Rule {

	@Override
	public Game.Status apply(Game g) {
		// We want to check whether there is a row on the board which is full
		// of tokens with the same colour. To do this, we go through each row
		// in turn.
		Status r = null;
		for(int y=0;y<4;++y) {
			r = checkRow(g.getBoard(),y);
			if(r != null) {
				return r;
			}
		}
		return Status.ONGOING;
	}
	
	/**
	 * Check a single row on the board has all the same token. If so, we have
	 * a winner.
	 * 
	 * @param b
	 * @param x
	 * @return
	 */
	private Game.Status checkRow(Board b, int y) {
		Board.Token first = b.getSquare(new Position(0, y));
		if (first != null) {
			// There is a token in the first column. Now, all remaining tokens
			// must match this one to have a winner.
			for (int x = 1; x < 4; ++x) {
				Board.Token t = b.getSquare(new Position(x, y));
				if (t != first) {
					return null;
				}
			}
			// We found a winner!
			if (first == Board.Token.WHITE) {
				return Status.WHITEWON;
			} else {
				return Status.BLACKWON;
			}
		}
		return null;
	}
}
