package swen221.lab2.rules;

import swen221.lab2.model.*;

/**
 * This should implements Conway's rule for overproduction:
 * 
 * "Any live cell with more than three live neighbours dies, as if by over-population"
 * 
 * @author David J. Pearce
 *
 */
public class ConwaysOverpopulationRule implements Rule {

	@Override
	public int apply(int x, int y, BoardView board) {
		// This rule was not applied in this case
		return Rule.NOT_APPLICABLE;		
	}
}
