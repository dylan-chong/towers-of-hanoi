import java.math.BigInteger;


/**
 * This class decrypts a Base64 ciphertext given a particular key (integer).
 * <p>
 * No error checking on inputs!
 */
public class Search {

  /**
   * Example illustrating how to search a range of keys.
   * <p>
   * NO ERROR CHECKING, EXCEPTIONS JUST PROPAGATED.
   *
   * @param args[0] key represented as a big integer value
   * @param args[1] key size
   * @param args[2] ciphertext encoded as base64 value
   */
  public static void main(String[] args) throws Exception {

    // Extract the key, turn into an array (of right size) and
    //   convert the base64 ciphertext into an array
    BigInteger bi = new BigInteger(args[0]);
    int keySize = Integer.parseInt(args[1]);
    byte[] key = Blowfish.asByteArray(bi, keySize);
    byte[] ciphertext = Blowfish.fromBase64(args[2]);

    // Go into a loop where we try a range of keys starting at the given one
    String plaintext = null;
    // Search from the key that will give us our desired ciphertext
    for (int i = 0; i < 100; i++) {
      // tell user which key is being checked
      String keyStr = bi.toString();
      System.out.print(keyStr);
      Thread.sleep(100);
      for (int j = 0; j < keyStr.length(); j++) {
        System.out.print("\b");
      }
      // decrypt and compare to known plaintext
      Blowfish.setKey(key);
      plaintext = Blowfish.decryptToString(ciphertext);
      if (plaintext.equals("May good flourish; Kia hua ko te pai")) {
        System.out.println("Plaintext found!");
        System.out.println(plaintext);
        System.out.println("key is (hex) " + Blowfish.toHex(key) + " " + bi);
        System.exit(-1);
      }

      // try the next key
      bi = bi.add(BigInteger.ONE);
      key = Blowfish.asByteArray(bi, keySize);
    }
    System.out.println("No key found!");
  }
}