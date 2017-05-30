package junit;

import assignment5.BruteForceStringSearcher;
import assignment5.KMPStringSearcher;
import assignment5.StringSearcher;
import org.junit.Test;

import java.util.Arrays;

import static assignment5.KMPStringSearcher.*;
import static assignment5.StringSearcher.NO_MATCH_FOUND;
import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 30/05/17.
 */
public abstract class StringSearcherTest {
    @Test
    public void search_emptyPatternEmptyText_returnsNoMatch() {
        assertEquals(NO_MATCH_FOUND, newSearcher().search("", ""));
    }

    @Test
    public void search_emptyPatternNonEmptyText_returnsNoMatch() {
        assertEquals(NO_MATCH_FOUND, newSearcher().search("text", ""));
    }

    @Test
    public void search_oneCharPatternExactlyMatchesText_returns0() {
        assertEquals(0, newSearcher().search("c", "c"));
    }

    @Test
    public void search_twoCharPatternExactlyMatchesText_returns0() {
        assertEquals(0, newSearcher().search("ab", "ab"));
    }

    @Test
    public void search_patternLongerThanText_returnsNoMatch() {
        assertEquals(NO_MATCH_FOUND, newSearcher().search("long pattern", "text"));
    }

    @Test
    public void search_textIsTwoRepeatsOfPattern_returns0() {
        assertEquals(0, newSearcher().search("ab", "abab"));
    }

    @Test
    public void search_textIsTwoRepeatsOfPatternNotAtStart_returnsFirstIndex() {
        assertEquals(2, newSearcher().search("ab", "xxabab"));
    }

    @Test
    public void search_noMatchForOneCharPattern_returnsNoMatch() {
        assertEquals(NO_MATCH_FOUND, newSearcher().search("p", "abcde"));
    }

    @Test
    public void search_oneMatchForOneCharPatternAfter1Char_returns1() {
        assertEquals(1, newSearcher().search("p", "apq"));
    }

    @Test
    public void search_oneMatchForTwoCharPatternAfter1Char_returns1() {
        assertEquals(1, newSearcher().search("pq", "apqb"));
    }

    @Test
    public void search_textHasOverlappingIncompleteMatches_returnsMatch() {
        assertEquals(9, newSearcher().search("amalama", "amamalammamalamaa"));
        // Match here: ------------------------------------------ amalama ---
    }

    protected abstract StringSearcher newSearcher();

    public static class BruteForceStringSearcherTest extends StringSearcherTest {
        protected StringSearcher newSearcher() {
            return new BruteForceStringSearcher();
        }
    }

    public static class KMPStringSearcherTest extends StringSearcherTest {
        @Override
        protected StringSearcher newSearcher() {
            return new KMPStringSearcher();
        }

        @Test(expected = IllegalArgumentException.class)
        public void createSearchTable_emptyPattern_exception() {
            createSearchTable("");
        }

        @Test
        public void createSearchTable_oneCharPattern_equalsArrayOfMinus1() {
            assertTableMatches("a",
                    new int[]{BEFORE_START}
            );
        }

        @Test
        public void createSearchTable_twoDifferentChars_tableIsCorrect() {
            assertTableMatches("ab",
                    new int[]{BEFORE_START, FIRST_CHAR}
            );
        }

        @Test
        public void createSearchTable_threeDifferentChars_tableIsCorrect() {
            assertTableMatches("abc",
                    new int[]{BEFORE_START, FIRST_CHAR, NOT_A_PREFIX}
            );
        }

        @Test
        public void createSearchTable_two1CharRepeats_tableIsCorrect() {
            assertTableMatches("aa",
                    new int[]{BEFORE_START, FIRST_CHAR}
            );
        }

        @Test
        public void createSearchTable_three1CharRepeats_tableShowsJumpBacks() {
            assertTableMatches("aaa",
                    new int[]{BEFORE_START, FIRST_CHAR, jumpBack(1)}
            );
        }

        @Test
        public void createSearchTable_two2CharRepeats_tableShowsJumpBacks() {
            assertTableMatches("abab",
                    new int[]{BEFORE_START, FIRST_CHAR, NOT_A_PREFIX, jumpBack(1)}
            );
        }

        @Test
        public void createSearchTable_two3CharRepeats_tableShowsJumpBacks() {
            assertTableMatches("abcabc",
                    new int[]{
                            BEFORE_START,
                            // first 'abc'
                            FIRST_CHAR, NOT_A_PREFIX, NOT_A_PREFIX,
                            // second 'abc'
                            jumpBack(1), jumpBack(2)
                    }
            );
        }

        /**
         * Example from slides
         */
        @Test
        public void createSearchTable_twoPartialPrefixes_tableShowsJumpBacks() {
            assertTableMatches("abcdabd",
                    new int[]{
                            BEFORE_START,
                            // 'abcd'
                            FIRST_CHAR, NOT_A_PREFIX, NOT_A_PREFIX, NOT_A_PREFIX,
                            // 'abd'
                            jumpBack(1), jumpBack(2)
                    }
            );
        }

        private void assertTableMatches(String pattern, int[] expectedTable) {
            int[] searchTable = createSearchTable(pattern);
            assertEquals(Arrays.toString(expectedTable), Arrays.toString(searchTable));
        }
    }
}
