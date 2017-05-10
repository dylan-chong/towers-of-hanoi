import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Dylan on 8/05/17.
 *
 * @param <EvalT> The type that this node returns (or {@link Void})
 */
public abstract class ParsableNode<EvalT> implements RobotProgramNode {

    /**
     * Chars to be put into a string regex character set
     */
    private static final String SPECIAL_CHARS = "(){},;";
    private static final Pattern DEFAULT_DELIMITER = Pattern.compile(
            "\\s+|(?=[" + SPECIAL_CHARS + "])|(?<=[" + SPECIAL_CHARS + "])"
    );
    private static final int ERROR_MAX_EXTRA_CHARS = 15;

    private boolean hasParsed = false;

    public final void parse(Scanner scanner) {
        if (hasParsed) throw new IllegalStateException("Already parsed");
        hasParsed = true;

        if (scanner.delimiter() != getScannerDelimiter()) { // Compare pointers for speed
            scanner.useDelimiter(getScannerDelimiter());
        }
        privateDoParse(scanner);
    }

    /**
     * Used to set the scanner delimiter to whatever the node needs
     */
    protected Pattern getScannerDelimiter() {
        return DEFAULT_DELIMITER;
    }

    /**
     * 'Private' method that actually does the parsing. Don't call this method
     * from the outside
     * <p>
     * Note: the delimiter causes spaces to be removed
     */
    abstract protected void privateDoParse(Scanner scanner);

    /**
     * @return The code for the node (and its insides) without spaces (where
     * possible). Don't just return the code that was parsed, parse the code,
     * and reconstruct it from the parsed code.
     */
    abstract public String toCode();

    abstract public EvalT evaluate();

    @Override
    public String toString() {
        return toCode();
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    protected String require(String pattern,
                             Scanner scanner,
                             ParserFailureType type) {
        if (scanner.hasNext(pattern)) return scanner.next();
        throwParseError("\nExpected: " + pattern, scanner, type);
        return null;
    }

    protected void throwParseError(String startOfMessage,
                                   Scanner scanner,
                                   ParserFailureType type) {
        StringBuilder msg = new StringBuilder(startOfMessage);
        msg.append("\nBut Got: ");

        if (scanner.hasNext()) {
            // Using original delimiter
            msg.append("'");
            msg.append(scanner.next());
            msg.append("'");
        }

        // Print next chars
        scanner.useDelimiter("(?=.)|(?<=.)"); // one char at a time
        if (scanner.hasNext()) {
            msg.append("\nMore next input: '");
            for (int i = 0; i < ERROR_MAX_EXTRA_CHARS && scanner.hasNext(); i++) {
                msg.append(scanner.next());
            }
            msg.append("'");
        }
        if (!scanner.hasNext()) msg.append(" {END OF INPUT}");

        throw new ParserFailureException(msg.toString(), type);
    }

    /**
     * Every {@link ParsableNode} should have a static factory class
     *
     * @param <NodeT> The type of node to produce
     */
    public interface Factory<NodeT extends ParsableNode<?>> {
        /**
         * Create a new {@link ParsableNode}
         */
        NodeT create();

        /**
         * @param scanner Scanner to call hasNext(pattern) on. Do not modify
         *                the scanner
         */
        boolean canStartWith(Scanner scanner);
    }
}
