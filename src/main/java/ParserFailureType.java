/**
 * Error type for {@link ParserFailureException}
 */
public enum ParserFailureType {
    /**
     * Avoid using this (pick something more specific)
     */
    GENERAL,
    /**
     * Used when a node of an incorrect is told to parse something
     */
    WRONG_NODE_START,
    /**
     * Failure in the middle or end of a node parse
     */
    SYNTAX_ERROR,
    NUMBER_FORMAT
}
