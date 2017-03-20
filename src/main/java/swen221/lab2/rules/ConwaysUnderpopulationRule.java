package swen221.lab2.rules;

import swen221.lab2.model.Rule;
import swen221.lab2.util.ConwayAbstractRule;


public class ConwaysUnderpopulationRule extends ConwayAbstractRule {
	
	public int apply(int x, int y, int neighbours) {
		if (neighbours < 2) {
			// This rule was applied in this case
			return ConwayAbstractRule.DEAD;
		} else {
			// This rule was not applied in this case
			return Rule.NOT_APPLICABLE;
		}
	}
}
