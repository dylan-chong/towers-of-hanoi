import java.util.Scanner;

@SuppressWarnings("serial")
public class ParserFailureException extends RuntimeException {

    private static final int ERROR_MAX_EXTRA_CHARS = 15;
    private static final String ONE_CHAR_PATTERN = "(?=.)|(?<=.)";

    private final ParserFailureType type;
    private final Integer positionInInput;

    public ParserFailureException(String msg,
                                  Integer positionInInput,
                                  ParserFailureType type) {
        super(msg);
        this.type = type;
        this.positionInInput = positionInInput;
    }

    public ParserFailureException(String msg) {
        this(msg, null, ParserFailureType.GENERAL);
    }

    public static ParserFailureException newFrom(String startOfMessage,
                                                 Scanner scanner,
                                                 ParserFailureType type) {
        StringBuilder msg = new StringBuilder(startOfMessage);
        Integer errorPosition = null;

        msg.append("\nBut Got: ");

        if (scanner.hasNext()) {
            // Using original delimiter
            msg.append("'");
            msg.append(scanner.next());
            msg.append("'");

            // Print error position
            errorPosition = scanner.match().start();
            msg.append("\nAt position: ")
                    .append(errorPosition);
        }

        // Print next chars
        scanner.useDelimiter(ONE_CHAR_PATTERN); // one char at a time
        if (scanner.hasNext()) {
            msg.append("\nMore next input: '");
            for (int i = 0; i < ERROR_MAX_EXTRA_CHARS && scanner.hasNext(); i++) {
                msg.append(scanner.next());
            }
            msg.append("'");
        }
        if (!scanner.hasNext()) msg.append(" {END OF INPUT}");

        return new ParserFailureException(msg.toString(), errorPosition, type);
    }

    public ParserFailureType getType() {
        return type;
    }

    public Integer getPositionInInput() {
        return positionInInput;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " (" + type.toString() + "): " +
                getMessage();
    }
}
