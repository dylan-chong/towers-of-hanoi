package junit;

import assignment5.HuffmanEncoder;
import org.junit.Test;

import java.util.*;

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
    }

    @Test
    public void getCharacterCodes_fourDifferentChars_returnsMapOf0() {
        String text = "abbcccdddd";

        Map<Character, String> expectedCodes = new HashMap<>();
        expectedCodes.put('d', "0");
        expectedCodes.put('c', "10");
        expectedCodes.put('b', "111");
        expectedCodes.put('a', "110");
        assertEquals(expectedCodes, HuffmanEncoder.getCharacterCodes(text));
    }

    @Test
    public void encode_twoDifferentChars_encodes() {
        String text = "abb";
        assertEquals("011", new HuffmanEncoder(text).encode());
    }

    @Test
    public void encode_threeDifferentChars_encodes() {
        String text = "abbccc";
        assertEquals("101111000", new HuffmanEncoder(text).encode());
    }

    @Test
    public void encode_fourDifferentChars_encodes() {
        String text = "abbcccdddd";
        assertEquals("1101111111010100000", new HuffmanEncoder(text).encode());
    }

    @Test
    public void decode_twoDifferentChars_getsOriginalText() {
        String text = "abb";
        String encoded = "011";
        assertEquals(text, new HuffmanEncoder(text).decode(encoded));
    }

    @Test
    public void decode_threeDifferentChars_getsOriginalText() {
        String text = "abbccc";
        String encoded = "101111000";
        assertEquals(text, new HuffmanEncoder(text).decode(encoded));
    }

    @Test
    public void decode_fourDifferentChars_getsOriginalText() {
        String text = "abbcccdddd";
        String encoded = "1101111111010100000";
        assertEquals(text, new HuffmanEncoder(text).decode(encoded));
    }
}

