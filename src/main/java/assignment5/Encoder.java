package assignment5;

/**
 * Created by Dylan on 2/06/17.
 */
public interface Encoder {
    /**
     * Take an input string, text, and encode it with the stored tree. Should
     * return the encoded text as a binary string, that is, a string containing
     * only 1 and 0.
     */
    String encode();

    /**
     * Take encoded input as a binary string, decode it using the stored tree,
     * and return the decoded text as a text string.
     */
    String decode(String encoded);
}
