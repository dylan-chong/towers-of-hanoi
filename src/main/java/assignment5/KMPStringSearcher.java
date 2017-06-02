package assignment5;

/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMPStringSearcher implements StringSearcher {

    // Table constants
    public static final int BEFORE_PATTERN_START = -1;
    /**
     * it's the first char (the first char doesn't count as the prefix)
     */
    public static final int FIRST_CHAR = 0;
    public static final int NOT_A_PREFIX = 0;

    /**
     * Use this rather than raw ints for readability purposes.
     * <p>
     * Represents the number of characters to jump back to continue the search
     * if the search fails
     */
    public static int jumpBack(int jump) {
        if (jump < 0)
            throw new IllegalArgumentException();

        return jump;
    }

    public static TableResult createPatternTable(String pattern) {
        if (pattern.isEmpty())
            throw new IllegalArgumentException("Pattern can't be empty");

        int[] table = new int[pattern.length()];
        table[0] = BEFORE_PATTERN_START;
        long numChecks = 0;
        int currentPrefixLength = 0;

        if (pattern.length() >= 2)
            table[1] = FIRST_CHAR;

        for (int i = 2; i < pattern.length(); i++) {
            char charI = pattern.charAt(i - 1);
            char patternChar = pattern.charAt(currentPrefixLength);
            numChecks += 2;

            if (charI == patternChar) {
                // Continue or start streak
                currentPrefixLength++;
                table[i] = jumpBack(currentPrefixLength);
                numChecks++;
            }
            else if (currentPrefixLength > 0) {
                // Streak ended. Jump back to last prefix
                currentPrefixLength = table[currentPrefixLength];
                i--; // cancel i++
                numChecks++;
            }
            else { // currentPrefixLength == 0
                // Not in the middle of a 'streak'
                table[i] = NOT_A_PREFIX;
                currentPrefixLength = 0;
                numChecks++;
            }
        }

        return new TableResult(table, numChecks);
    }

    /**
     * Perform KMP substring search on the given text with the given pattern.
     * <p>
     * This should return the starting index of the first substring match if it
     * exists, or -1 if it doesn't.
     */
    @Override
    public SearchResult search(String pattern, String text) {
        if (pattern.isEmpty() || pattern.length() > text.length())
            return new SearchResult(NO_MATCH_FOUND, 0);

        TableResult tableResult = createPatternTable(pattern);
        int[] patternTable = tableResult.patternTable;
        int patternI = 0, textI = 0;
        long numChecks = tableResult.numChecks;

        while (textI <= text.length() - pattern.length()) {
            numChecks += 2;
            if (pattern.charAt(patternI) == text.charAt(textI + patternI)) { // match
                patternI++; // check next pattern char next
                if (patternI == pattern.length()) // match found
                    return new SearchResult(textI, numChecks);
                continue;
            }
            // No match
            numChecks++;
            if (patternTable[patternI] == BEFORE_PATTERN_START) { // No match without streak
                textI++; // skip ahead because no prefix of pattern
                patternI = 0; // reset to start of pattern
            }
            else { // No match just after streak
                // The length of the prefix we are on
                int lastPrefixLength = patternTable[patternI];
                textI += patternI; // jump to current pattern char in text
                textI -= lastPrefixLength; // jump back to start of last prefix
                patternI = lastPrefixLength; // set s to current position in last prefix
                numChecks++;
            }
        }

        return new SearchResult(NO_MATCH_FOUND, numChecks);
    }

    public static class TableResult {
        public final int[] patternTable;
        public final long numChecks;

        public TableResult(int[] patternTable, long numChecks) {
            this.patternTable = patternTable;
            this.numChecks = numChecks;
        }
    }
}