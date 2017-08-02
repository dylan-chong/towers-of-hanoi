package main;

public class InvalidMoveException extends Exception {
	/**
	 * @param message The message provided will be shown to the user
	 */
	public InvalidMoveException(String message) {
		super(message);
	}
}
