package swen221.lab2.rules;

import swen221.lab2.model.BoardView;
import swen221.lab2.model.Rule;
import swen221.lab2.util.ConwayAbstractRule;

/**
 * Rule for making a live cell that has less than 2 live neighbours die
 */
public class ConwaysUnderpopulationRule extends ConwayAbstractRule {

    @Override
	public int apply(int x, int y, int neighbours, BoardView board) {
		if (neighbours < 2) {
			// This rule was applied in this case
			return ConwayAbstractRule.DEAD;
		} else {
			// This rule was not applied in this case
			return Rule.NOT_APPLICABLE;
		}
	}
}
