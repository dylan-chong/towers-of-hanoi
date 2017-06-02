package assignment5;

import java.util.*;

/**
 * A new instance of LempelZiv is created for every run.
 */
public class LempelZivEncoder implements Encoder {

    private static final int LOOKBACK_MAX = 1023;

    private final String text;

    public LempelZivEncoder(String text) {
        this.text = text;
    }

    @Override
    public String encode() {
        List<CharRef> charRefs = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            BackReferenceResult backRef = findBackReference(text, i);

            if (backRef == null) { // No back reference found
                charRefs.add(new CharRef(0, 0, currentChar));
            } else {
                i += backRef.length;

                char nextChar;
                if (i >= text.length())
                    nextChar = CharRef.NULL_CHAR;
                else
                    nextChar = text.charAt(i);

                charRefs.add(new CharRef(backRef.offset, backRef.length, nextChar));
            }
        }

        // Make sure there is always a NULL_CHAR at the end
        if (charRefs.get(charRefs.size() - 1).nextChar != CharRef.NULL_CHAR)
            charRefs.add(new CharRef(0, 0, CharRef.NULL_CHAR));

        return CharRef.toString(charRefs);
    }

    private static BackReferenceResult findBackReference(String text,
                                                         int endPos) {
        if (endPos == 0)
            return null;

        int searchPosStart = Math.max(0, endPos - LOOKBACK_MAX);
        Collection<BackReferenceResult> backRefs = new ArrayList<>();

        for (int searchPos = searchPosStart; searchPos < endPos; searchPos++) {
            int matchLength = 0;

            while (true) {
                int textPos = endPos + matchLength;
                char backChar = text.charAt(searchPos + matchLength);
                char textChar = text.charAt(textPos);

                if (backChar == textChar)
                    matchLength++;
                else
                    break;

                if (textPos + 1 >= text.length()) // new textPos
                    break;
            }

            if (matchLength == 0) // no match
                continue;

            backRefs.add(new BackReferenceResult(endPos - searchPos, matchLength));
        }

        if (backRefs.isEmpty()) // No match
            return null;

        return Collections.max(
                backRefs,
                Comparator.comparingInt(backRef -> backRef.length) // longest
        );
    }

    @Override
    public String decode(String encoded) {
        return null;
    }

    /**
     * Represents a char or a lookback for a next char
     */
    public static class CharRef {
        /**
         * Ending character of all encodings for {@link LempelZivEncoder}
         */
        public static final char NULL_CHAR = '\0';

        public final int offset;
        public final int length;
        public final char nextChar;

        public CharRef(int offset, int length, char nextChar) {
            this.offset = offset;
            this.length = length;
            this.nextChar = nextChar;
        }

        @Override
        public String toString() {
            return String.format("[%d|%d|%c]", offset, length, nextChar);
        }

        public static String toString(List<CharRef> charRefs) {
            if (charRefs.get(charRefs.size() - 1).nextChar != NULL_CHAR)
                throw new IllegalArgumentException("charRefs must end with a null char tuple");

            StringBuilder stringBuilder = new StringBuilder();
            charRefs.forEach(stringBuilder::append);
            return stringBuilder.toString();
        }
    }

    private static class BackReferenceResult {
        public final int offset;
        public final int length;

        private BackReferenceResult(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }
}
