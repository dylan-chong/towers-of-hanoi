package swen221.lab6.connect.rules;

import swen221.lab6.connect.Game;
import swen221.lab6.connect.Game.Status;
import swen221.lab6.connect.core.Board;
import swen221.lab6.connect.core.Rule;
import swen221.lab6.connect.util.Position;

/**
 * Checks whether or not a stale mate has been reached.
 *
 * @author David J. Pearce
 *
 */
public class StaleMateRule implements Rule {

	@Override
	public Status apply(Game g) {
		if (g.isWhitesTurn()) {
			if (g.tokensLeft(Board.Token.WHITE) == 0) return Status.STALEMATE;
		} else {
			if (g.tokensLeft(Board.Token.BLACK) == 0) return Status.STALEMATE;
		}
		// Here, we need to check how many tokens have been played so far. Since
		// each player starts with exactly eight tokens, there can be at most
		// eight tokens played by each player. After that point, we have reached
		// a stalemate. When this happens, we need to return the appropriate
		// status signal. And, yes, it is possible to reach stalemate.
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (g.getBoard().getSquare(new Position(x, y)) == null) {
					return g.getStatus();
				}
			}
		}
		return Status.STALEMATE;
	}
}
