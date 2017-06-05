package swen221.lab10;

import static swen221.lab10.GameOfLife.*;

import swen221.lab10.model.*;
import swen221.lab10.util.Pair;
import swen221.lab10.util.Point;
import swen221.lab10.view.BoardFrame;

public class CellDecayGameOfLife {
	/**
	 * The standard rule set for Conway's "Game of Life".
	 */
	public static final Rule[] CellDecayRules = {
			// TODO: The underproduction rule

			// TODO: The reproduction rule

			// TODO: The overpopulation rule
	
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
