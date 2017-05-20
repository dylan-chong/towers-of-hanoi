import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Dylan on 8/05/17.
 *
 * @param <EvalT> The type that this node returns (or {@link Void})
 */
public abstract class AbstractParsableNode<EvalT> implements ParsableNode<EvalT> {

    static {
        String specialChars = "(){},;=";
        DEFAULT_DELIMITER = Pattern.compile(
                "\\s+|(?=[" + specialChars + "])|(?<=[" + specialChars + "])"
        );
    }

    /**
     * Chars to be put into a string regex character set
     */
    public static final Pattern DEFAULT_DELIMITER;

    private final ParsableNode<?> parentNode;

    private boolean hasParsed = false;

    public AbstractParsableNode(ParsableNode<?> parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public ParsableNode<?> getParentNode() {
        return parentNode;
    }

    @Override
    public final void parse(Scanner scanner, Logger logger) {
        if (hasParsed) throw new IllegalStateException("Already parsed");

        if (scanner.delimiter() != getScannerDelimiter()) { // Compare pointers for speed
            scanner.useDelimiter(getScannerDelimiter());
        }

        logger.logStartParseNode(this);

        privateDoParse(scanner, logger);
        hasParsed = true;

        logger.logEndParseNode(this);
    }

    /**
     * Used to set the scanner delimiter to whatever the node needs
     */
    protected Pattern getScannerDelimiter() {
        return DEFAULT_DELIMITER;
    }

    @Override
    public String toString() {
        if (!hasParsed) return super.toString();
        return privateToCode();
    }

    /**
     * 'Private' method that actually does the parsing. Don't call this method
     * from the outside
     * <p>
     * Note: the delimiter causes spaces to be removed
     */
    abstract protected void privateDoParse(Scanner scanner, Logger logger);

    /**
     * @return The code for the node (and its insides) without spaces (where
     * possible). Don't just return the code that was parsed, parse the code,
     * and reconstruct it from the parsed code.
     */
    abstract protected String privateToCode();

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

    protected static void throwParseError(String startOfMessage,
                                          Scanner scanner,
                                          ParserFailureType nonOneMatches) {
        throw ParserFailureException.newFrom(
                startOfMessage, scanner, nonOneMatches
        );
    }


    /**
     * A factory that uses other factories to create NodeT objects
     * @param <NodeT>
     */
    public static abstract class DelegatorFactory<NodeT extends ParsableNode<?>>
            implements Factory<NodeT> {

        @Override
        public NodeT create(Scanner scannerNotToBeModified,
                            ParsableNode<?> parentNode) {
            Collection<Factory<? extends NodeT>> matches = getMatches(scannerNotToBeModified);
            requireOnlyOne(matches, scannerNotToBeModified);
            return matches.stream()
                    .findAny()
                    .orElseThrow(AssertionError::new)
                    .create(scannerNotToBeModified, parentNode);
        }

        @Override
        public boolean canStartWith(Scanner scannerNotToBeModified) {
            return getMatches(scannerNotToBeModified).size() > 0;
        }

        public Collection<Factory<? extends NodeT>> getMatches(
                Scanner scannerNotToBeModified) {
            return getPossibilities()
                    .stream()
                    .filter(factory -> factory.canStartWith(scannerNotToBeModified))
                    .collect(Collectors.toList());
        }

        protected abstract Collection<Factory<? extends NodeT>> getPossibilities();
    }
}
