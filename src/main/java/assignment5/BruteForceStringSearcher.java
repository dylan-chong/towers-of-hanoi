package assignment5;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Dylan on 30/05/17.
 */
public class BruteForceStringSearcher implements StringSearcher {
    @Override
    public SearchResult search(String pattern, String text) {
        AtomicLong numChecks = new AtomicLong(0);

        if (pattern.isEmpty() || pattern.length() > text.length())
            return new SearchResult(NO_MATCH_FOUND, 0);

        // 'xI' is short for 'x index'

        for (int textI = 0; textI <= text.length() - pattern.length(); textI++) {
            if (isMatchAt(textI, pattern, text, numChecks)) {
                return new SearchResult(textI, numChecks.get());
            }
        }

        return new SearchResult(NO_MATCH_FOUND, numChecks.get());
    }

    private boolean isMatchAt(int textI, String pattern, String text, AtomicLong numChecks) {
        for (int patternI = 0; patternI < pattern.length(); patternI++) {
            char patternChar = pattern.charAt(patternI);
            char textChar = text.charAt(textI + patternI);
            numChecks.addAndGet(2);

            if (patternChar != textChar) // no match
                break;
            else if (patternI == pattern.length() - 1) // match on last pattern char
                return true;
        }

        return false;
    }
}
