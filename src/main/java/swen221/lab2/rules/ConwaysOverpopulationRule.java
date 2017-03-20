package swen221.lab2.rules;

import swen221.lab2.model.BoardView;
import swen221.lab2.util.ConwayAbstractRule;

/**
 * This should implements Conway's rule for overproduction:
 * 
 * "Any live cell with more than three live neighbours dies, as if by over-population"
 * 
 * @author David J. Pearce
 *
 */
public class ConwaysOverpopulationRule extends ConwayAbstractRule {
    @Override
    public int apply(int x, int y, int neighbours, BoardView board) {
        if (neighbours > 3) {
            return DEAD;
        }
        return NOT_APPLICABLE;
    }
}
