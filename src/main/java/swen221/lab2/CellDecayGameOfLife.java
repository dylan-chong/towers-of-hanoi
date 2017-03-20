package swen221.lab2;

import swen221.lab2.model.Board;
import swen221.lab2.model.BoardView;
import swen221.lab2.model.Rule;
import swen221.lab2.model.Simulation;
import swen221.lab2.util.ConwayAbstractRule;
import swen221.lab2.view.BoardFrame;

public class CellDecayGameOfLife {
	/**
	 * The standard rule set for Conway's "Game of Life".
	 */
	public static final Rule[] CellDecayRules = {
            new ConwayAbstractRule() { // Under population
                @Override
                public int apply(int x, int y, int neighbours, BoardView board) {
                    int cellState = board.getCellState(x, y);

                    if (cellState == DEAD || neighbours >= 2) return NOT_APPLICABLE;
                    return cellState + 1;
                }
            },
            new ConwayAbstractRule() { // Next generation
                @Override
                public int apply(int x, int y, int neighbours, BoardView board) {
                    int cellState = board.getCellState(x, y);

                    if (neighbours != 2) return NOT_APPLICABLE;
                    return cellState;
                }
            },
            new ConwayAbstractRule() { // Over-population
                @Override
                public int apply(int x, int y, int neighbours, BoardView board) {
                    int cellState = board.getCellState(x, y);

                    if (cellState == DEAD || neighbours <= 3) return NOT_APPLICABLE;
                    return cellState + 1;
                }
            },
            new ConwayAbstractRule() { // Happiness
                @Override
                public int apply(int x, int y, int neighbours, BoardView board) {
                    int cellState = board.getCellState(x, y);

                    if (neighbours != 3) return NOT_APPLICABLE;
                    if (cellState == 0) return 0;
                    return cellState - 1;
                }
            }


    };
	
	/**
	 * The entry point for the GameOfLife application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Board board = new Board(50,50);
		Simulation sim = new Simulation(board,CellDecayRules);
		new BoardFrame(sim);
	}
}
