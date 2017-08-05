package main.gamemodel;

public class IllegalGameStateException extends RuntimeException {
	public IllegalGameStateException(String message) {
		super(message);
	}
}
