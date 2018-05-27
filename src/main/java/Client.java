import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

public class Client {

  /**
   * @param args[0] key manager hostname
   * @param args[1] key manager port
   * @param args[2] number of keys to check
   */
  public static void main(String[] args) throws IOException {
    String hostname = args[0];
    int port = Integer.parseInt(args[1]);
    String numberOfKeysToCheck = args[2];

    while (true) {
      try (
        Socket socket = new Socket(hostname, port);
        BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream())
        );
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
      ) {

        String askForWorkMessage = String.format(
          "%s %d %s",
          socket.getLocalAddress().getHostAddress(),
          socket.getLocalPort(),
          numberOfKeysToCheck
        );
        out.write(askForWorkMessage);

        String askForWorkResponse = in.readLine();
        System.out.println(askForWorkResponse);

        // TODO Disconnect when processing

        String[] askForWorkResponseSplit = askForWorkResponse.split(" ");
        BigInteger startingKey = new BigInteger(askForWorkResponseSplit[0]);
        int keySize = Integer.parseInt(askForWorkResponseSplit[1]);
        byte[] ciphertext = Blowfish.fromBase64(askForWorkResponseSplit[2]);

        String matchingKey = processKeys(
          startingKey,
          keySize,
          ciphertext,
          Integer.parseInt(numberOfKeysToCheck)
        );

        String completionMessage = String.format(
          "%s %d %s",
          socket.getLocalAddress().getHostAddress(),
          socket.getLocalPort(),
          matchingKey == null ? "" : matchingKey
        ).trim();
        out.write(completionMessage);
      }
    }
  }

  private static String processKeys(
    BigInteger bi,
    int keySize,
    byte[] ciphertext,
    int numberOfKeysToCheck
  ) {
    byte[] key = Blowfish.asByteArray(bi, keySize);

    // Extract the key, turn into an array (of right size) and
    //   convert the base64 ciphertext into an array

    // Go into a loop where we try a range of keys starting at the given one
    String plaintext = null;
    // Search from the key that will give us our desired ciphertext
    for (int i = 0; i < numberOfKeysToCheck; i++) {
      // tell user which key is being checked
      String keyStr = bi.toString();
      System.out.print(keyStr);
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

    return null;
  }
}
