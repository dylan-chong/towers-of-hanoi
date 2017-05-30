package junit;

import assignment5.BruteForceStringSearcher;
import assignment5.KMPStringSearcher;
import assignment5.StringSearcher;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.LongStream;

import static assignment5.KMPStringSearcher.*;
import static assignment5.StringSearcher.NO_MATCH_FOUND;
import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 30/05/17.
 */
public abstract class StringSearcherTest {

    private static final int BENCHMARK_REPEATS = 8;
    private static final String BENCHMARK_FILES_DIR = "data/";
    private static final Map<String, Collection<String>> BENCHMARK_FILES_TO_PATTERNS =
            new HashMap<String, Collection<String>>() {{
                put("data/apollo.txt",
                        Arrays.asList("123", "50"));
                put("data/pi.txt",
                        Arrays.asList("123", "4567",
                                "0315614033321272849194418437150696552087542450598956787961303311646283996346460422090106105779458151"
                        ));
                put("data/war_and_peace.txt",
                        Arrays.asList("independence", "--", "my the"));
            }};

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

    @Test
    public void RUN_BENCHMARKS() throws Exception {
        BENCHMARK_FILES_TO_PATTERNS.forEach((file, patterns) -> {
            String text = getTextFromFile(new File(file));
            System.out.printf("File : %s\n", file);
            for (String pattern : patterns) {
                BenchmarkResult benchmarkResult = getBenchmarkResult(pattern, text);
                System.out.printf("" +
                                "  - pattern           : '%s'\n" +
                                "  - match index       : %d\n" +
                                "    - median duration : %d ms\n" +
                                "    - durations       : %s\n",
                        pattern,
                        benchmarkResult.index,
                        benchmarkResult.medianDuration,
                        Arrays.toString(benchmarkResult.durations)
                );
                System.out.println();
            }
            System.out.println();
        });
    }

    private BenchmarkResult getBenchmarkResult(String pattern, String text) {
        long[] durations = LongStream.generate(() -> getDuration(pattern, text))
                .limit(BENCHMARK_REPEATS)
                .sorted()
                .toArray();
        long median = durations[durations.length / 2];
        int index = newSearcher().search(pattern, text);
        return new BenchmarkResult(durations, median, index);
    }

    private long getDuration(String pattern, String text) {
        System.gc();
        long start = System.currentTimeMillis();
        newSearcher().search(pattern, text);
        long end = System.currentTimeMillis();
        return end - start;
    }

    private String getTextFromFile(File path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path.getPath()));
            StringBuilder text = new StringBuilder();
            for (String line : lines)
                text.append(line);
            return text.toString();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    protected abstract StringSearcher newSearcher();

    private static class BenchmarkResult {
        private final long medianDuration;
        private final long[] durations;
        public final int index;

        private BenchmarkResult(long[] durations, long medianDuration, int index) {
            this.medianDuration = medianDuration;
            this.durations = durations;
            this.index = index;
        }
    }

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
            createPatternTable("");
        }

        @Test
        public void createSearchTable_oneCharPattern_equalsArrayOfMinus1() {
            assertTableMatches("a",
                    new int[]{BEFORE_PATTERN_START}
            );
        }

        @Test
        public void createSearchTable_twoDifferentChars_tableIsCorrect() {
            assertTableMatches("ab",
                    new int[]{BEFORE_PATTERN_START, FIRST_CHAR}
            );
        }

        @Test
        public void createSearchTable_threeDifferentChars_tableIsCorrect() {
            assertTableMatches("abc",
                    new int[]{BEFORE_PATTERN_START, FIRST_CHAR, NOT_A_PREFIX}
            );
        }

        @Test
        public void createSearchTable_two1CharRepeats_tableIsCorrect() {
            assertTableMatches("aa",
                    new int[]{BEFORE_PATTERN_START, FIRST_CHAR}
            );
        }

        @Test
        public void createSearchTable_three1CharRepeats_tableShowsJumpBacks() {
            assertTableMatches("aaa",
                    new int[]{BEFORE_PATTERN_START, FIRST_CHAR, jumpBack(1)}
            );
        }

        @Test
        public void createSearchTable_two2CharRepeats_tableShowsJumpBacks() {
            assertTableMatches("abab",
                    new int[]{BEFORE_PATTERN_START, FIRST_CHAR, NOT_A_PREFIX, jumpBack(1)}
            );
        }

        @Test
        public void createSearchTable_two3CharRepeats_tableShowsJumpBacks() {
            assertTableMatches("abcabc",
                    new int[]{
                            BEFORE_PATTERN_START,
                            // first 'abc'
                            FIRST_CHAR, NOT_A_PREFIX, NOT_A_PREFIX,
                            // second 'ab' (last char isn't checked)
                            jumpBack(1), jumpBack(2)
                    }
            );
        }

        /**
         * Example 1 from slides
         */
        @Test
        public void createSearchTable_twoPartialPrefixes_tableShowsJumpBacks() {
            assertTableMatches("abcdabd",
                    new int[]{
                            BEFORE_PATTERN_START,
                            // 'abcd'
                            FIRST_CHAR, NOT_A_PREFIX, NOT_A_PREFIX, NOT_A_PREFIX,
                            // 'ab'
                            jumpBack(1), jumpBack(2)
                    }
            );
        }

        @Test
        public void createSearchTable_threePartialPrefixes_tableShowsJumpBacks() {
            assertTableMatches("abcdabcabc",
                    new int[]{
                            BEFORE_PATTERN_START,
                            // 'abcd'
                            FIRST_CHAR, NOT_A_PREFIX, NOT_A_PREFIX, NOT_A_PREFIX,
                            // 'abc'
                            jumpBack(1), jumpBack(2), jumpBack(3),
                            // 'ab'
                            jumpBack(1), jumpBack(2)
                    }
            );
        }

        /**
         * Example 2 from slides
         */
        @Test
        public void createSearchTable_threePartialPrefixes2_tableShowsJumpBacks() {
            assertTableMatches("ananaba",
                    new int[]{
                            BEFORE_PATTERN_START,
                            // 'an'
                            FIRST_CHAR, NOT_A_PREFIX,
                            // 'anab'
                            jumpBack(1), jumpBack(2), jumpBack(3), NOT_A_PREFIX
                    }
            );
        }

        private void assertTableMatches(String pattern, int[] expectedTable) {
            int[] searchTable = createPatternTable(pattern);
            assertEquals(Arrays.toString(expectedTable), Arrays.toString(searchTable));
        }
    }
}
