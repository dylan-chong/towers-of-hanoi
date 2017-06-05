package swen221.lab10;

import swen221.lab10.model.Board;
import swen221.lab10.model.Rule;
import swen221.lab10.model.Simulation;
import swen221.lab10.util.Pair;
import swen221.lab10.util.Point;
import swen221.lab10.view.BoardFrame;

import static swen221.lab10.GameOfLife.DEAD;
import static swen221.lab10.GameOfLife.neighbours;

public class CellDecayGameOfLife {
	/**
	 * The standard rule set for Conway's "Game of Life".
	 */
	public static final Rule[] CellDecayRules = {
			(Pair<Point, Board> p) -> { // under pop
				int cellState = p.second().getCellState(p.first().getX(), p.first().getY());

				if (cellState == DEAD || neighbours(p) >= 2) return null;
				return cellState + 1;
			},
			(Pair<Point, Board> p) -> { // next gen
				int cellState = p.second().getCellState(p.first().getX(), p.first().getY());
				if (neighbours(p) != 2) return null;
				return cellState;
			},
			(Pair<Point, Board> p) -> { // over pop
				int cellState = p.second().getCellState(p.first().getX(), p.first().getY());
				if (cellState == DEAD || neighbours(p) <= 3) return null;
				return cellState + 1;
			},
			(Pair<Point, Board> p) -> { // happiness
				int cellState = p.second().getCellState(p.first().getX(), p.first().getY());
				if (neighbours(p) != 3) return null;
				if (cellState == 0) return 0;
				return cellState - 1;
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
