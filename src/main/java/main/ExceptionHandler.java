package main;

/**
 * In production, should handle the error without crashing the program.
 * In testing, should throw the exception.
 */
public interface ExceptionHandler {
	void handle(Throwable throwable);
}
