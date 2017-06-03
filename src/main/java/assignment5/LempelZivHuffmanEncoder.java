package assignment5;

/**
 * Created by Dylan on 3/06/17.
 */
public class LempelZivHuffmanEncoder implements Encoder {

    private final String text;

    private final LempelZivEncoder lempelZivEncoder;
    private HuffmanEncoder huffmanEncoder;

    public LempelZivHuffmanEncoder(String text, int lookbackWindowSize) {
        this.text = text;
        this.lempelZivEncoder = new LempelZivEncoder(text, lookbackWindowSize);
    }

    public LempelZivHuffmanEncoder(String text) {
        this.text = text;
        this.lempelZivEncoder = new LempelZivEncoder(text);
    }

    @Override
    public String encode() {
        String lempelEncode = lempelZivEncoder.encode();
        huffmanEncoder = new HuffmanEncoder(lempelEncode);
        return huffmanEncoder.encode();
    }

    @Override
    public String decode(String encoded) {
        String lempelCode = huffmanEncoder.decode(encoded);
        return lempelZivEncoder.decode(lempelCode);
    }
}
