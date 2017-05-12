import java.util.Collection;
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
    abstract protected String privateToCode();

    /**
     * Similar to {@link ParsableNode#execute(Robot)} but this doesn't perform
     * {@link Robot} operations, it only calculates a return value. This is
     * useful for maths and boolean operations.
     */
    abstract public EvalT evaluate();

    @Override
    public String toString() {
        if (!hasParsed) throw new IllegalStateException("Not parsed yet");
        return privateToCode();
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    protected static String require(String pattern,
                             Scanner scanner,
                             ParserFailureType type) {
        if (scanner.hasNext(pattern)) return scanner.next();
        throwParseError("\nExpected: " + pattern, scanner, type);
        throw new AssertionError("Should not reach here");
    }

    protected static void throwParseError(String startOfMessage,
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

    protected static void requireOnlyOne(Collection<?> matches,
                                         Scanner scanner) {
        if (matches.size() != 1) throwParseError(
                String.format("Invalid number of valid matches (%d): %s",
                        matches.size(),
                        matches
                ),
                scanner,
                ParserFailureType.NON_ONE_MATCHES
        );
    }

    /**
     * Every {@link ParsableNode} should have a static factory class (that
     * overrides this). Don't implement this directly; use the {@link Base}
     * class.
     *
     * @param <NodeT> The type of node to produce
     */
    public interface Factory<NodeT extends ParsableNode<?>> {

        /**
         * Create a new {@link ParsableNode}. Pick a node by choosing what
         * can be created using
         */
        NodeT create(Scanner scannerNotToBeModified);

        /**
         * @param scanner Scanner to call hasNext(pattern) on. Do not modify
         *                the scanner. This is to be used for factories that use
         *                other factories to create nodes to decide which to use
         */
        boolean canStartWith(Scanner scannerNotToBeModified);
    }
}
