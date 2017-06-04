package assignment5;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Created by Dylan on 30/05/17.
 */
public class BoyerMooreStringSearcher implements StringSearcher {
    @Override
    public SearchResult search(String pattern, String text) {
        if (pattern.isEmpty() || pattern.length() > text.length())
            return new SearchResult(NO_MATCH_FOUND, 0);

        AtomicLong numChecks = new AtomicLong(0);
        Map<Character, NavigableSet<Integer>> patternCharPos =
                getCharsToIndexes(pattern, numChecks);
        int textIndex = 0;

        while (textIndex <= text.length() - pattern.length()) {
            int patternIndex;
            char patternChar = '\0';
            char textChar = '\0';
            numChecks.addAndGet(2);

            for (patternIndex = pattern.length() - 1; patternIndex >= 0; patternIndex--) {
                patternChar = pattern.charAt(patternIndex);
                textChar = text.charAt(textIndex + patternIndex);
                numChecks.addAndGet(2);

                if (patternChar != textChar)
                    break; // no match
                else if (patternIndex == 0)
                    // match success
                    return new SearchResult(textIndex, numChecks.get());
            }

            // Decide which textIndex to check next:

            assert patternChar != textChar;

            NavigableSet<Integer> patternIndexes = patternCharPos.get(textChar);
            numChecks.incrementAndGet();
            if (patternIndexes == null || patternIndexes.isEmpty()) {
                // No textChar isn't in pattern at all
                textIndex += patternIndex + 1;
                continue;
            } else {
                Integer jumpToIndex = patternIndexes.floor(patternIndex);
                numChecks.incrementAndGet();
                if (jumpToIndex != null) {
                    textIndex += patternIndex - jumpToIndex;
                    continue;
                }
            }
            textIndex++;
        }

        return new SearchResult(NO_MATCH_FOUND, numChecks.get());
    }

    public static Map<Character, NavigableSet<Integer>> getCharsToIndexes(
            String string,
            AtomicLong numChecks) {
        Map<Character, NavigableSet<Integer>> charsToIndexes = new HashMap<>();
        Function<Character, NavigableSet<Integer>> newSet = (c) -> new TreeSet<>();

        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            NavigableSet<Integer> indexes =
                    charsToIndexes.computeIfAbsent(character, newSet);
            indexes.add(i);
            numChecks.incrementAndGet();
        }

        return charsToIndexes;
    }
}
