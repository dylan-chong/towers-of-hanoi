import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * This class generates a random key and a plaintext/ciphertext pair.
 * <p>
 * A little buggy still but can generate some reasonable key/ciphertext pairs.
 */

public class KeyGen {

  // Create a key and plaintext/ciphertext pair
  public static void main(String[] args) throws Exception {

    // Check the arguments
    int numBytes = 4; // default is a 32 bit key
    if (args.length > 0) {
      try {
        numBytes = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("Argument" + args[0] + " must be an integer.");
        System.exit(1);
      }
    } else {
      System.out.println("Using default key length of 32 bits.");
    }

    // Get the KeyGenerator
    KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
    kgen.init(numBytes * 8); // 32 bit key

    // Generate a random key
    SecretKey skey = kgen.generateKey();
    byte[] raw = skey.getEncoded();

    // Lets make it an easy key
    //raw[0] = 0;
    //raw[1] = 0;
    //raw[2] = 0;

    // Convert the key to a BigInteger and other formats (for sharing)
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
    BigInteger bi = Blowfish.asBigInteger(raw);
    String bis = bi.toString();
    System.out.println("key is (formatted integer) " + nf.format(bi));
    System.out.println("key is (integer) " + bis);
    System.out.println("key (raw) is (hex) " + Blowfish.toHex(raw));

    // Initialise the key
    Blowfish.setKey(raw);

    // We work with bytes so do a conversion using ISO rules
    String plaintextStr = "May good flourish; Kia hua ko te pai";
    System.out.println("plaintext string :" + plaintextStr);
    byte[] plaintext = plaintextStr.getBytes("ISO-8859-1");

    // Apply the encryption
    byte[] ciphertext = Blowfish.encrypt(plaintext);

    // This has to be shared so convert into Base64 representation
    String ciphertextBase64 = Blowfish.toBase64(ciphertext);
    System.out.println("base64 ciphertext:" + ciphertextBase64);

    // Now lets check that we can use the key and ciphertext to go in reverse

    // first, lets recover the key from the BigInteger
    bi = new BigInteger(bis);
    raw = Blowfish.asByteArray(bi, raw.length);
    Blowfish.setKey(raw);

    // second, let recover the ciphertext bytes from base64
    ciphertext = Blowfish.fromBase64(ciphertextBase64);
    plaintextStr = Blowfish.decryptToString(ciphertext);

    // print out the details
    System.out.println("decrypted ciphertext:" + plaintextStr);
  }
}
