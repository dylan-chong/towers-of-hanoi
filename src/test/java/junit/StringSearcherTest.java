package junit;

import assignment5.BruteForceStringSearcher;
import assignment5.KMPStringSearcher;
import assignment5.StringSearcher;
import org.junit.Test;

import static assignment5.StringSearcher.NO_MATCH_FOUND;
import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 30/05/17.
 */
public abstract class StringSearcherTest {
    @Test
    public void search_emptyPatternEmptyText_returns0() {
        assertEquals(0, newSearcher().search("", ""));
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
    public void search_textIsTwoRepeatsOfPattern_returns0() {
        assertEquals(0, newSearcher().search("ab", "abab"));
    }

    @Test
    public void search_textIsTwoRepeatsOfPatternNotAtStart_returnsFirstIndex() {
        assertEquals(0, newSearcher().search("ab", "xxabab"));
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
    }
}
