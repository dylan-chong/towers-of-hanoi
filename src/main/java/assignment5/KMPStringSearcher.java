package assignment5;

/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMPStringSearcher implements StringSearcher {

    // Table constants
    public static final int BEFORE_START = -1;
    /**
     * it's the first char (the first char doesn't count as the prefix)
     */
    public static final int FIRST_CHAR = 0;
    public static final int NOT_IN_PREFIX = 0;

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

    public static int[] createSearchTable(String pattern) {
        if (pattern.isEmpty())
            throw new IllegalArgumentException("Pattern can't be empty");

        int[] table = new int[pattern.length()];
        table[0] = BEFORE_START;

        int currentPrefixLength = 0;

        if (pattern.length() >= 2)
            table[1] = FIRST_CHAR;

        for (int i = 2; i < pattern.length(); i++) {
            char charI = pattern.charAt(i - 1);
            char patternChar = pattern.charAt(currentPrefixLength);

            if (charI == patternChar) {
                currentPrefixLength++;
                table[i] = jumpBack(currentPrefixLength);
            } else if (currentPrefixLength > 0) {
                currentPrefixLength = table[currentPrefixLength];
                i--; // cancel i++
            } else { // currentPrefixLength == 0
                // not in the middle of a 'streak'
                table[i] = NOT_IN_PREFIX;
                currentPrefixLength = 0;
            }
        }

        return table;
    }

    /**
     * Perform KMP substring search on the given text with the given pattern.
     * <p>
     * This should return the starting index of the first substring match if it
     * exists, or -1 if it doesn't.
     */
    @Override
    public int search(String pattern, String text) {
        // TODO fill this in.
        return -1;
    }
}
