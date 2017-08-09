package main.textcontroller;

import main.gamemodel.Direction;
import main.gamemodel.InvalidMoveException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class TextCommandState {

	protected static String[] requireTokens(String line,
											Integer... validNumTokens)
			throws ParseFormatException {

		String[] tokens = Arrays
				.stream(line.split(" "))
				.map(String::trim)
				.filter(token -> token.length() > 0)
				.toArray(String[]::new);

		if (!Arrays.asList(validNumTokens).contains(tokens.length)) {
			throw new ParseFormatException("Invalid number of tokens");
		}

		return tokens;
	}

	protected static String commandInstructions(
			String cmdName,
			Collection<Character> pieceIds,
			Function<Direction, String> directionToString
	) {
		List<String> sortedIds = pieceIds.stream()
				.map(Object::toString)
				.sorted()
				.collect(Collectors.toList());
		List<String> directions = Arrays.stream(Direction.values())
				.map(directionToString)
				.collect(Collectors.toList());

		return String.format(
				"%s <%s> <%s>",
				cmdName,
				String.join("/", sortedIds),
				String.join("/", directions)
		);
	}

	protected static int rotationsFromDegrees(String degreesStr)
			throws InvalidMoveException {
		int degrees = Integer.parseInt(degreesStr);
		if (!Direction.isValidDegrees(degrees)) {
			throw new InvalidMoveException("Invalid angle: " + degrees);
		}
		return Direction.clockwiseRotationsFromDegrees(degrees);
	}

	/**
	 * @param line The text the user entered
	 */
	public abstract void parseAndExecute(String line)
			throws ParseFormatException, InvalidMoveException;

	/**
	 * Instructions to display to the user
	 */
	public abstract String getInstructions();
}
