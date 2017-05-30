package assignment5;

/**
 * Created by Dylan on 30/05/17.
 */
public class BruteForceStringSearcher implements StringSearcher {
    @Override
    public int search(String pattern, String text) {
        if (pattern.isEmpty())
            return NO_MATCH_FOUND;

        if (pattern.length() > text.length())
            return NO_MATCH_FOUND;

        // 'xI' is short for 'x index'

        for (int textI = 0; textI <= text.length() - pattern.length(); textI++) {
            if (isMatchAt(textI, pattern, text)) {
                return textI;
            }
        }

        return -1;
    }

    private boolean isMatchAt(int textI, String pattern, String text) {
        for (int patternI = 0; patternI < pattern.length(); patternI++) {
            char patternChar = pattern.charAt(patternI);
            char textChar = text.charAt(textI + patternI);

            if (patternChar != textChar) // no match
                break;
            else if (patternI == pattern.length() - 1) // match on last pattern char
                return true;
        }

        return false;
    }
}
