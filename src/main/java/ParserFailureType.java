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
    WRONG_MIDDLE_OR_END_OF_NODE,
    NUMBER_FORMAT,
    /**
     * When there is 0 or more than 1 possible match when trying to pick
     * what sort of node (or action) to use
     */
    NON_ONE_MATCHES,
    WRONG_NUMBER_OF_STATEMENTS
}
