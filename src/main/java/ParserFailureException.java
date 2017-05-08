@SuppressWarnings("serial")
public class ParserFailureException extends RuntimeException {
    private final ParserFailureType type;

    public ParserFailureException(String msg, ParserFailureType type) {
        super(msg);
        this.type = type;
    }
    public ParserFailureException(String msg) {
        this(msg, ParserFailureType.GENERAL);
    }

    public ParserFailureType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " (" + type.toString() + "): " +
                getMessage();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
