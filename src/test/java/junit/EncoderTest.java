package junit;

import assignment5.Encoder;
import assignment5.HuffmanEncoder;
import assignment5.LempelZivEncoder;
import assignment5.LempelZivEncoder.CharRef;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 2/06/17.
 */
public abstract class EncoderTest {
    @Test
    public void encodeThenDecode_twoDifferentChars_getsOriginalText() {
        testEncodeThenDecode_getsOriginalTest("abb");
    }

    @Test
    public void encodeThenDecode_threeDifferentChars_getsOriginalText() {
        testEncodeThenDecode_getsOriginalTest("abbccc");
    }

    @Test
    public void encodeThenDecode_fourDifferentChars_getsOriginalText() {
        testEncodeThenDecode_getsOriginalTest("abbcccdddd");
    }

    private void testEncodeThenDecode_getsOriginalTest(String text) {
        Encoder encoder = newEncoder(text);
        String encoded = encoder.encode();
        String decoded = encoder.decode(encoded);
        assertEquals(text, decoded);
    }

    protected abstract Encoder newEncoder(String text);

    public static class HuffmanEncoderTest extends EncoderTest {
        @Override
        protected Encoder newEncoder(String text) {
            return new HuffmanEncoder(text);
        }

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

    public static class LempelZivEncoderTest extends EncoderTest {
        @Override
        protected Encoder newEncoder(String text) {
            return new LempelZivEncoder(text);
        }

        @Test
        public void encode_oneCharacter_getOneTuple() {
            testEncode_getsCorrectTuples("a", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(0, 0, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_twoCharacters_getTwoTuples() {
            testEncode_getsCorrectTuples("ab", Arrays.asList(
                    new CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(0, 0, 'b'),
                    new CharRef(0, 0, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_oneSingleCharLookback_secondTuplePointsBack() {
            testEncode_getsCorrectTuples("aab", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(1, 1, 'b'),
                    new LempelZivEncoder.CharRef(0, 0, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_oneSingleCharLookbackNotAtStart_secondTuplePointsBack() {
            testEncode_getsCorrectTuples("zaab", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'z'),
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(1, 1, 'b'),
                    new LempelZivEncoder.CharRef(0, 0, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_twoSingleCharLookbacks_twoCharRefsLookBack() {
            testEncode_getsCorrectTuples("aaba", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(1, 1, 'b'),
                    new LempelZivEncoder.CharRef(3, 1, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_twoDoubleCharLookbacks_twoCharRefsLookBack() {
            testEncode_getsCorrectTuples("aabaa", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(1, 1, 'b'),
                    new LempelZivEncoder.CharRef(3, 2, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_twoOfTwoDifferentCharLookbacks_twoCharRefsLookBack() {
            testEncode_getsCorrectTuples("acbac", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(0, 0, 'c'),
                    new LempelZivEncoder.CharRef(0, 0, 'b'),
                    new LempelZivEncoder.CharRef(3, 2, CharRef.NULL_CHAR)
            ));
        }

        @Test
        public void encode_oneShortAndOneLongLookback_picksTheShortest() {
            // 2nd "acc" should reference first "acc"
            testEncode_getsCorrectTuples("acbaccacc", Arrays.asList(
                    new LempelZivEncoder.CharRef(0, 0, 'a'),
                    new LempelZivEncoder.CharRef(0, 0, 'c'),
                    new LempelZivEncoder.CharRef(0, 0, 'b'),
                    new LempelZivEncoder.CharRef(3, 2, 'c'),
                    new LempelZivEncoder.CharRef(3, 3, CharRef.NULL_CHAR)
            ));
        }

        private void testEncode_getsCorrectTuples(String text,
                                                  List<LempelZivEncoder.CharRef> charRefs) {
            assertEquals(
                    LempelZivEncoder.CharRef.toString(charRefs),
                    newEncoder(text).encode()
            );
        }
    }
}
