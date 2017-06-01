package junit;

import assignment5.HuffmanEncoder;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 1/06/17.
 */
public class HuffmanEncoderTest {
    @Test
    public void getCharacterCounts_emptyString_returnsEmptyMap() {
        assertEquals(Collections.emptyMap(), HuffmanEncoder.getCharacterCounts(""));
    }

    @Test
    public void getCharacterCounts_oneDifferentCharString_returnsCorrectMap() {
        Map<Character, Integer> expectedCounts = new HashMap<>();
        expectedCounts.put('a', 1);
        assertEquals(expectedCounts, HuffmanEncoder.getCharacterCounts("a"));
    }

    @Test
    public void getCharacterCounts_severalDifferentChars_returnsCorrectMap() {
        Map<Character, Integer> expectedCounts = new HashMap<>();
        expectedCounts.put('a', 2);
        expectedCounts.put('b', 2);
        expectedCounts.put('c', 1);
        expectedCounts.put(' ', 1);
        expectedCounts.put('A', 1);
        assertEquals(expectedCounts, HuffmanEncoder.getCharacterCounts("abc abA"));
    }

    @Test
    public void getCharacterCodes_emptyString_returnsEmptyMap() {
        assertEquals(Collections.emptyMap(), HuffmanEncoder.getCharacterCodes(""));
    }

    @Test
    public void getCharacterCodes_oneChar_returnsMapTo0() {
        Map<Character, String> expectedCodes = new HashMap<>();
        expectedCodes.put('a', "0");
        assertEquals(expectedCodes, HuffmanEncoder.getCharacterCodes("a"));
    }

    @Test
    public void getCharacterCodes_twoDifferentChars_returnsMapOf0() {
        String text = "abb";

        Map<Character, String> expectedCodes = new HashMap<>();
        expectedCodes.put('b', "1");
        expectedCodes.put('a', "0");
        assertEquals(expectedCodes, HuffmanEncoder.getCharacterCodes(text));
    }

    @Test
    public void getCharacterCodes_threeDifferentChars_returnsMapOf0() {
        String text = "abbccc";

        Map<Character, String> expectedCodes = new HashMap<>();
        expectedCodes.put('c', "0");
        expectedCodes.put('b', "11");
        expectedCodes.put('a', "10");
        assertEquals(expectedCodes, HuffmanEncoder.getCharacterCodes(text));

        // ---
        // Map<Character, Integer> counts = HuffmanEncoder.getCharacterCounts(text);
        // Map<Character, String> codes = HuffmanEncoder.getCharacterCodes(counts);
        //
        // List<Character> charactersSortedByCodeLength = codes.entrySet().stream()
        //         .sorted(Comparator.comparingInt(entry -> entry.getValue().length()))
        //         .map(Map.Entry::getKey)
        //         .collect(Collectors.toList());
        //
        // List<Integer> hopefullySortedCounts = charactersSortedByCodeLength.stream()
        //         .map(counts::get)
        //         .collect(Collectors.toList());
        // assertTrue(isSorted(hopefullySortedCounts));

        // Check that small code lengths are used by frequent chars
        // by making sure that when you sort the codes by length, they are
        // also sorted by frequency
    }

    private <T extends Comparable<T>> boolean isSorted(List<T> hopefullySortedCounts) {
        if (hopefullySortedCounts.size() <= 1) return true;

        T previousItem = null;
        for (T item : hopefullySortedCounts) {
            if (previousItem == null) {
                previousItem = item;
                continue;
            }

            if (previousItem.compareTo(item) > 0)
                return false;
        }

        return true;
    }
}
