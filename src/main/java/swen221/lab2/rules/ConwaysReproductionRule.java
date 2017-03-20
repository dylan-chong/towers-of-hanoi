package swen221.lab2.rules;

import swen221.lab2.model.*;

/**
 * This should implement Conway's rule for reproduction:
 * 
 * "Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction"
 * 
 * @author David J. Pearce
 *
 */
public class ConwaysReproductionRule implements Rule {
	
	@Override
	public int apply(int x, int y, BoardView board) {
		// default implementation which does nothing.
		return Rule.NOT_APPLICABLE;		
	}
}
