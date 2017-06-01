package assignment5;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HuffmanEncoder {

    private final String text;
    private final Map<Character, String> codes;

    public HuffmanEncoder(String text) {
        this.text = text;
        this.codes = getCharacterCodes(text);
    }

    /**
     * Take an input string, text, and encode it with the stored tree. Should
     * return the encoded text as a binary string, that is, a string containing
     * only 1 and 0.
     */
    public String encode() {
        StringBuilder encoded = new StringBuilder();
        text.chars().forEach(character ->
                encoded.append(codes.get((char) character))
        );
        return encoded.toString();
    }

    /**
     * Take encoded input as a binary string, decode it using the stored tree,
     * and return the decoded text as a text string.
     */
    public String decode(String encoded) {
        Map<String, Character> codeToChar = codes.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue, Map.Entry::getKey
                ));

        StringBuilder text = new StringBuilder();
        StringBuilder token = null;
        for (int i = 0; i < encoded.length(); i++) {
            char thisChar = encoded.charAt(i);

            if (token == null)
                token = new StringBuilder();
            token.append(thisChar);

            Character character = codeToChar.get(token.toString());
            if (character == null) {
                continue; // keep appending to token
            }

            text.append(character);
            token = null;
        }

        return text.toString();
    }

    public static Map<Character, Integer> getCharacterCounts(String text) {
        Map<Character, Integer> counts = new HashMap<>();
        text.chars().forEach(c -> {
            char character = (char) c;
            Integer count = counts.getOrDefault(character, 0);
            counts.put(character, count + 1);
        });
        return counts;
    }

    public static Map<Character, String> getCharacterCodes(
            Map<Character, Integer> charCounts) {

        if (charCounts.isEmpty()) {
            return Collections.emptyMap();
        } else if (charCounts.size() == 1) {
            Map.Entry<Character, Integer> onlyEntry = charCounts.entrySet()
                    .stream()
                    .findAny()
                    .orElseThrow(AssertionError::new);
            Map<Character, String> codes = new HashMap<>();
            codes.put(onlyEntry.getKey(), "0");
            return codes;
        }

        CharNode rootCharNode = convertCharacterCountsIntoNodes(charCounts);
        Map<Character, String> codes = rootCharNode.getCodes();
        return codes;
    }

    private static CharNode convertCharacterCountsIntoNodes(
            Map<Character, Integer> charCounts) {
        // Queue has more frequent character entries first
        PriorityQueue<CharNode> queue = new PriorityQueue<>(
                Comparator.comparingInt(CharNode::getCharCount)
        );
        queue.addAll(charCounts.entrySet()
                .stream()
                .map(entry -> new CharNode(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        while (queue.size() >= 2) {
            CharNode newNode = new CharNode(queue.remove(), queue.remove());
            queue.add(newNode);
        }

        assert queue.size() == 1;
        return queue.remove();
    }

    public static Map<Character, String> getCharacterCodes(String text) {
        return getCharacterCodes(getCharacterCounts(text));
    }

    private static class CharNode {
        private final Character character;
        private final Integer charCount;
        private final CharNode leftNode, rightNode;

        private CharNode(Character character,
                         Integer charCount) {
            this.character = character;
            this.charCount = charCount;
            this.leftNode = null;
            this.rightNode = null;
        }

        private CharNode(CharNode leftNode,
                         CharNode rightNode) {
            this.character = null;
            this.charCount = null;
            this.leftNode = leftNode;
            this.rightNode = rightNode;
        }

        public Integer getCharCount() {
            if (character != null) return charCount;
            int left = leftNode == null ? 0 : leftNode.getCharCount();
            int right = rightNode == null ? 0 : rightNode.getCharCount();
            return left + right;
        }

        public Map<Character, String> getCodes() {
            return getCodes("")
                    .collect(Collectors.toMap(
                            CharAndCode::getCharacter, CharAndCode::getCode
                    ));
        }

        private Stream<CharAndCode> getCodes(String prefix) {
            if (character != null)
                return Stream.of(new CharAndCode(character, prefix));

            Stream<CharAndCode> leftPrefixes = leftNode == null ?
                    Stream.empty() : leftNode.getCodes(prefix + "0");
            Stream<CharAndCode> rightPrefixes = rightNode == null ?
                    Stream.empty() : rightNode.getCodes(prefix + "1");

            return Stream.of(leftPrefixes, rightPrefixes)
                    .flatMap(entryStream -> entryStream);
        }

        private static class CharAndCode {
            private final char character;
            private final String code;

            private CharAndCode(char character, String code) {
                this.character = character;
                this.code = code;
            }

            public char getCharacter() {
                return character;
            }

            public String getCode() {
                return code;
            }
        }
    }
}
