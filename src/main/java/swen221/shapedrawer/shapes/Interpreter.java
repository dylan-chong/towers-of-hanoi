package swen221.shapedrawer.shapes;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Responsible for interpreting a shape program. The program is represented as a
 * string, through which the interpreter moves. For example, consider this shape
 * program:
 *
 * <pre>
 * x =[0,0,10,10]
 * fill x #000000
 * </pre>
 *
 * This program will be represented in the input string as follows:
 *
 * <pre>
 * --------------------------------------------------------------
 * | x |   | = | [ | 0 | , | 0 | , | 1 | 0 | , | 1 | 0 | ] | \n |
 * --------------------------------------------------------------
 *   0   1   2   3   4   5   6   7   8   9   10  11  12  13  14
 *
 * (continued)
 * --------------------------------------------------------------
 * | f | i | l | l |   | x |   | # | 0 | 0 | 0 | 0 | 0 | 0 | \n |
 * --------------------------------------------------------------
 *   14  15  16  17  18  19  20  21  22  23  24  25  26  27  38
 * </pre>
 *
 * The interpreter starts at index 0 and attempts to decide what kind of command
 * we have. If the first four characters are "fill" then it's a fill command. If
 * the first four characters are "draw" then it's a draw command. Otherwise, it
 * must be an assignment command.
 *
 * @author David J. Pearce
 *
 */
public class Interpreter {

	private static final Map<Character, CompositeShape.Factory> COMPOSITE_FACTORIES;

	static {
		COMPOSITE_FACTORIES = new HashMap<>();
		COMPOSITE_FACTORIES.put('+', UnionShape::new);
		COMPOSITE_FACTORIES.put('&', IntersectionShape::new);
		COMPOSITE_FACTORIES.put('-', DifferenceShape::new);
	}
	/**
	 * The input program being interpreted by this class
	 */
	private String input;

	/**
	 * The current position within the input program that this interpreter has
	 * reached.
	 */
	private int index; // current position within the input string.

	/**
	 * A mapping from variables to their shape value. When a variable is
	 * assigned a new shape value, then this map is updated accordingly.
	 */
	private HashMap<String, Shape> environment = new HashMap<String, Shape>();

	/**
	 * Construct an interpreter from a given input string representing a simple
	 * shape program.
	 *
	 * @param input
	 */
	public Interpreter(String input) {
		this.input = input;
		this.index = 0;
	}

	/**
	 * This method creates an empty canvas onto which it evaluates each command
	 * of the program in turn. The canvas is then returned.
	 *
	 * @return a canvas that shows the result of the input.
	 */
	public Canvas run() {
		Canvas canvas = new Canvas();
		while (index < input.length()) {
			evaluateNextCommand(canvas);
		}
		return canvas;
	}

	/**
	 * Evaluate the next command in the program. To do this, the interpreter
	 * must first decide what kind of command it is. This is done by looking at
	 * the first word of the input string at the current position.
	 *
	 * @param canvas
	 */
	private void evaluateNextCommand(Canvas canvas) {
		skipWhiteSpace();
		String cmd = readWord();
		skipWhiteSpace();

		if (index >= input.length()) return;

		if (cmd.equals("fill")) {
			Shape shape = evaluateShapeExpression();
			Color color = readColor();
			fillShape(color, shape, canvas);
		} else if (cmd.equals("draw")) {
			Shape shape = evaluateShapeExpression();
			Color color = readColor();
			drawShape(color, shape, canvas);
		} else if (cmd.equals("")) {
			if (!Character.isAlphabetic(input.charAt(index))) {
				error("Can't start a line with a non-alphabetic char");
			}
			// do nothing
		} else {
			// variable assignment
			if (!Character.isAlphabetic(cmd.charAt(0))) {
				error("Can't start variable with non-alphabetic char");
			}
			String variableName = cmd + readVariable();
			skipWhiteSpace();
			match("=");
			Shape rhs = evaluateShapeExpression();
			environment.put(variableName, rhs);
		}
	}

	/**
	 * Read a "word" from the input string. This is defined as a sequence of one
	 * or more consequtive letters. Digits and other characters (e.g. '_' or
	 * '+') are not permitted as part of a word.
	 *
	 * @return
	 */
	private String readWord() {
		int start = index;
		// Advance through the input string from the current position whilst the
		// character at that position is a letter.
		while (index < input.length() && Character.isLetter(input.charAt(index))) {
			index++;
		}
		return input.substring(start, index);
	}

	/**
	 * This should fill a given shape in a given colour onto the canvas.
	 *
	 * @param color
	 * @param shape
	 * @param canvas
	 */
	public void fillShape(Color color, Shape shape, Canvas canvas) {
		Rectangle box = shape.boundingBox();
		for (int y = box.y; y < box.y + box.height; y++) {
			for (int x = box.x; x < box.x + box.width; x++) {
				if (!shape.contains(x, y)) continue;
				canvas.draw(x, y, color);
			}
		}
	}

	/**
	 * This should draw a given shape in a given colour onto the canvas.
	 *
	 * @param color
	 * @param shape
	 * @param canvas
	 */
	public void drawShape(Color color, Shape shape, Canvas canvas) {
		Rectangle box = shape.boundingBox();
		for (int y = box.y; y < box.y + box.height; y++) {
			for (int x = box.x; x < box.x + box.width; x++) {

				if (!shape.contains(x, y)) continue;
				long nonDiagonalNeighbours = Stream.of(
						new Point(x, y + 1),
						new Point(x, y - 1),
						new Point(x + 1, y),
						new Point(x - 1, y))
						.filter(point -> shape.contains(point.x, point.y))
						.count();
				if (nonDiagonalNeighbours == 4) continue;
				canvas.draw(x, y, color);

			}
		}
	}

	/**
	 * Evaluate a shape expression which is expected at the current position
	 * within the input string. This is done by first looking at the current
	 * character in the input string. If this is a '(', for example, then it
	 * signals the start of a bracketed expression.
	 *
	 * @return
	 */
	public Shape evaluateShapeExpression() {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		Shape value = null;

		if (lookahead == '(') {
			// in this case, we have a bracketed sub-expression
			value = evaluateBracketedExpression();
		} else if (lookahead == '[') {
			// in this case, we have a bracketed sub-expression
			value = evaluateRectangleExpression();
		} else if (Character.isLetter(lookahead)) {
			// in this case, we have a number
			value = evaluateVariableExpression();
		} else {
			error("unknown operator");
		}

		Character lookahead2 = lookaheadToNextChar();
		if (lookahead2 != null && COMPOSITE_FACTORIES.keySet()
				.contains(lookahead2)) {
			CompositeShape compositeShape = evaluateCompositeShape(value);
			if (compositeShape != null) return compositeShape;
		}

		return value;
	}

	private Character lookaheadToNextChar() {
		for (int lookahead = index; lookahead < input.length(); lookahead++) {
			char compositeOperator = input.charAt(lookahead);
			if (compositeOperator == ' ') continue;
			if (compositeOperator == '\n') return null;
			return compositeOperator;
		}

		return null; // no next character
}

	private CompositeShape evaluateCompositeShape(Shape shape1) {
		skipWhiteSpace();
		char operator = input.charAt(index);
		CompositeShape.Factory factory = COMPOSITE_FACTORIES.get(operator);
		if (factory == null) return null;

		index++;
		skipWhiteSpace();
		Shape shape2 = evaluateShapeExpression();

		return factory.create(shape1, shape2);
	}

	/**
	 * Evaluate a bracketed shape expression. That is a shape expression which
	 * is surrounded by braces.
	 * 
	 * @return
	 */
	private Shape evaluateBracketedExpression() {
		match("(");
		Shape value = evaluateShapeExpression();
		match(")");
		return value;
	}

	/**
	 * Evaluate a rectangle expression. That is, four numbers separated by
	 * comma's and '[', ']'.
	 * 
	 * @return
	 */
	private Shape evaluateRectangleExpression() {
		match("[");
		int x = readNumber();
		match(",");
		int y = readNumber();
		match(",");
		int w = readNumber();
		match(",");
		int h = readNumber();
		match("]");
		return new Rectangle(x, y, w, h);
		//todo whitepsace
	}

	/**
	 * Evaluate a variable expression which is expected at the current input
	 * position. A variable is a sequence of one or more digits or letters, of
	 * which the first character must be a letter. Having determined the
	 * variable name, its current value is then looked up in the environment.
	 * 
	 * @return
	 */
	private Shape evaluateVariableExpression() {
		int start = index;
		String var = readVariable();
		Shape s = environment.get(var);
		if (s == null) {
			index = start; // to get proper output
			error("undefined variable");
		}
		return s;
	}

	/**
	 * Read a number which is expected at the current input position. A number
	 * is defined as a sequence of one or more digits.
	 * 
	 * @return
	 */
	private int readNumber() {
		skipWhiteSpace();
		int start = index;
		while (index < input.length() && Character.isDigit(input.charAt(index))) {
			index = index + 1;
		}
		return Integer.parseInt(input.substring(start, index));
	}

	/**
	 * Read a color which is expected at the current input position. A color is
	 * a string of 7 characters, of which the first is a '#' and the remainder
	 * are digits or letters.
	 * 
	 * @return
	 */
	public Color readColor() {
		skipWhiteSpace();
		if ((index + 7) > input.length()) {
			error("expecting color");
		}
		String str = input.substring(index, index + 7);
		index += 7;
		return new Color(str);
	}

	/**
	 * Read a variable name which is expected at the current input position. A
	 * variable is a sequence of one or more digits or letters, of which the
	 * first character must be a letter.
	 * 
	 * @return
	 */
	private String readVariable() {
		int start = index;
		while (index < input.length()
				&& (Character.isLetter(input.charAt(index)) || Character.isDigit(input.charAt(index)))) {
			index++;
		}
		return input.substring(start, index);
	}

	/**
	 * Match a string of text which is expected at the current input position.
	 * If the match fails, then an error is produced.
	 * 
	 * @param text
	 */
	private void match(String text) {
		skipWhiteSpace();
		if (input.startsWith(text, index)) {
			index += text.length();
		} else {
			error("expecting: " + text);
		}
	}

	/**
	 * Skip over any "whitespace" at the current input position. That is, any
	 * sequence of zero or more space or newline characters.
	 */
	private void skipWhiteSpace() {
		while (index < input.length() && (input.charAt(index) == ' ' || input.charAt(index) == '\n')) {
			index = index + 1;
		}
	}

	/**
	 * Report an error
	 * 
	 * @param error
	 */
	private void error(String error) {
		String msg = error + "\n" + input + "\n";
		for (int i = 0; i < index; ++i) {
			msg += " ";
		}
		msg += "^";
		throw new IllegalArgumentException(msg);
	}
}
