package swen222.battleships;

import java.util.*;

/**
 * This class implements a text interface for the battle ships game
 * @author djp
 *
 */
public class TextInterface {
	private static Random random = new Random();
	
	/**
	 * Generate a random integer between 0 and n
	 */
	public static int randomInteger(int n) {
		return random.nextInt(n);
	}
	
	/**
	 * Print a simple welcome message for when the game starts.
	 */
	private static void printWelcomeMessage() {		
		System.out.println("Welcome to the game of BattleShips!!");
		System.out.println("written by David J. Pearce, 2008.\n\n");
	}
	
	/**
	 * Input an integer from the scanner. If an invalid input is given, then
	 * keep trying until a valid input is given.
	 * 
	 * @param msg The message to print before reading the input
	 * @param in The scanner from which to read the input
	 * @return
	 */
	private static int inputInteger(String msg, Scanner scanner) {
		while(true) {
			System.out.println(msg);
			try {
				int i = Integer.parseInt(scanner.nextLine());
				return i;
			} catch(NumberFormatException e) {
				System.out.println("Invalid integer!");
			}
		}
	}
	
	/**
     * Print a grid square to the standard output. If the visible flag is set,
     * then ShipSquares are visible.
     * 
     * @param square
     * @param visible
     */
	public static void printSquare(GridSquare square, boolean visible) {
		if(square instanceof EmptySquare) {
			System.out.print(" ");
		} else if(square instanceof MissSquare) {
			System.out.print("O");
		} else if(square instanceof HitSquare) {
			System.out.print("X");
		} else if(square instanceof ShipSquare) {			
			if(visible) {
				ShipSquare ss = (ShipSquare) square;
				if(ss.getType() == ShipSquare.Type.HORIZONTAL_LEFT_END) {
					System.out.print("<");
				} else if(ss.getType() == ShipSquare.Type.HORIZONTAL_RIGHT_END) {
					System.out.print(">");
				} else if(ss.getType() == ShipSquare.Type.HORIZONTAL_MIDDLE) {
					System.out.print("#");	
				} else if(ss.getType() == ShipSquare.Type.VERTICAL_TOP_END) {
					System.out.print("^");
				} else if(ss.getType() == ShipSquare.Type.VERTICAL_BOTTOM_END) {
					System.out.print("v");
				} else if(ss.getType() == ShipSquare.Type.VERTICAL_MIDDLE) {
					System.out.print("#");
				} else {
					System.out.print("?");
				}
			} else {
				System.out.print(" ");
			}
		}
	}
	
	/**
     * Print the current state of the game board to the standard output.
     * 
     * @param game
     */
	public static void printBoard(BattleShipsGame game) {
		for(int y = 0; y < game.getHeight(); y++) {
			if(y < 10) { System.out.print("0"); }
			System.out.print(y);
			for(int x = 0; x < game.getWidth(); x++) {
				printSquare(game.getLeftSquare(x,y),true); 
			}
			
			System.out.print("||");
			
			for(int x = 0; x < game.getWidth(); x++) {
				printSquare(game.getRightSquare(x,y),false);
			}			
			if(y < 10) { System.out.print("0"); }
			System.out.println(y);
		}
		System.out.print("                    ");
		for(int x = 0; x < game.getWidth();++x) {
			System.out.print(x % 10);
		}
		System.out.println("\n");	
	}
	
	/**
	 * Play the game!
	 * 
	 * @param game
	 */
	public static void playGame(BattleShipsGame game) {
		Scanner scanner = new Scanner(System.in);
		game.createRandomBoard(random);
		while(!game.isOver()) {		
			printBoard(game);
			int xpos = 0;
			int ypos = 0;
			while(true) {
				xpos = inputInteger("Enter x coordinate for target:",scanner);
				ypos = inputInteger("Enter y coordinate for target:",scanner);
				if(xpos < 0 || ypos < 0 || xpos >= game.getWidth() || ypos >= game.getHeight()) {
					System.out.println("\n *** INVALID TARGET --- TRY AGAIN ***\n");
					continue;
				}		
				break;
			}
			
			game.bombSquare(xpos,ypos,false);
			int c_xpos = randomInteger(game.getWidth());
			int c_ypos = randomInteger(game.getHeight());
			game.bombSquare(c_xpos,c_ypos,true);
		}
		System.out.println("*** GAME OVER ***\n");
		
		if(game.didPlayerWin()) {
			System.out.println("*** WELL DONE! ***");
		} else {
			System.out.println("*** BAD LUCK! ***");
		}
	}
	
	public static void main(String[] args) {
		printWelcomeMessage();
		playGame(new BattleShipsGame(16,16));
	}
}
