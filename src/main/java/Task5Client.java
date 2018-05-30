import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Asks for work to do from a {@link KeyManager}, performs the work, and then
 * returns a result. This client then goes back to the start, asking for more
 * work to do.
 */
public class Task5Client {

  public static final String ASK_FOR_WORK = "ASK";
  public static final String RESPONSE = "RESPONSE";

  private static final double RANDOM_SIMULATED_FAILURE_RATE = 0.5;

  /**
   * @param args[0] key manager hostname
   * @param args[1] key manager port
   * @param args[2] number of keys to check
   */
  public static void main(String[] args) throws InterruptedException {
    String hostname = args[0];
    int port = Integer.parseInt(args[1]);
    int numberOfKeysToCheck = Integer.parseInt(args[2]);

    Task5Client client = new Task5Client(hostname, port, numberOfKeysToCheck);

    while (true) {
      try {
        client.workForKeyManager();
      } catch (IOException e) {
        Exception e2 = new IOException(
          "There was a problem connecting to the KeyManager. " +
            "Maybe the key was already found? " +
            "Or maybe there actually was a problem connecting. ",
          e
        );
        e2.printStackTrace();

        Thread.sleep(1000);
      }
    }
  }

  private final String hostname;
  private final int port;
  private final int numberOfKeysToCheck;

  public Task5Client(String hostname, int port, int numberOfKeysToCheck) {
    this.hostname = hostname;
    this.port = port;
    this.numberOfKeysToCheck = numberOfKeysToCheck;
  }

  private void workForKeyManager() throws IOException, InterruptedException {
    String askForWorkResponse = findWorkToDo();

    if (Math.random() < RANDOM_SIMULATED_FAILURE_RATE) {
      System.out.println("Simulating failure ...");
      Thread.sleep(3000);
      return;
    }

    String matchingKey = processKeys(askForWorkResponse);
    sendWorkResult(askForWorkResponse.split(" ")[0], matchingKey);
  }

  private String findWorkToDo() throws IOException {
    return talkToManager((socket, in, out) -> {
      String message = String.format(
        "%s %s %d %s",
        ASK_FOR_WORK,
        socket.getLocalAddress().getHostAddress(),
        socket.getLocalPort(),
        numberOfKeysToCheck
      );
      System.out.println("Output: " + message);
      out.println(message);

      String response = in.readLine();
      System.out.println("Input: " + response);

      return response;
    });
  }

  private String sendWorkResult(String startingKey, String matchingKey) throws IOException {
    return talkToManager((socket, in, out) -> {
      String completionMessage = String.format(
        "%s %s %d %s %s",
        RESPONSE,
        socket.getLocalAddress().getHostAddress(),
        socket.getLocalPort(),
        startingKey,
        matchingKey == null ? "" : matchingKey
      ).trim();
      System.out.println("Output: " + completionMessage);
      out.println(completionMessage);
      return matchingKey;
    });
  }

  private <T> T talkToManager(NetworkWork<T> work) throws IOException {
    try (
      Socket socket = new Socket(hostname, port);
      BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream())
      );
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
    ) {
      return work.workWith(socket, in, out);
    }
  }

  private String processKeys(String askForWorkResponse) {
    String[] askForWorkResponseSplit = askForWorkResponse.split(" ");
    BigInteger startingKey = new BigInteger(askForWorkResponseSplit[0]);
    int keySize = Integer.parseInt(askForWorkResponseSplit[1]);
    byte[] ciphertext = Blowfish.fromBase64(askForWorkResponseSplit[2]);

    return processKeys(
      startingKey,
      keySize,
      ciphertext,
      numberOfKeysToCheck
    );
  }

  private String processKeys(
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
      if (i % 1000 == 0) {
        System.out.print('.');
      }

      // decrypt and compare to known plaintext
      Blowfish.setKey(key);
      plaintext = Blowfish.decryptToString(ciphertext);
      if (plaintext.equals("May good flourish; Kia hua ko te pai")) {
        System.out.println();
        System.out.println("Plaintext found!");
        System.out.println(plaintext);
        System.out.println("key is (hex) " + Blowfish.toHex(key) + " " + bi);
        return keyStr;
      }

      // try the next key
      bi = bi.add(BigInteger.ONE);
      key = Blowfish.asByteArray(bi, keySize);
    }

    System.out.println();
    return null;
  }
}
