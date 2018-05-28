import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

public class Client {

  public static final String ASK_FOR_WORK = "ASK";
  public static final String RESPONSE = "RESPONSE";

  private final String hostname;
  private final int port;
  private final int numberOfKeysToCheck;

  public Client(String hostname, int port, int numberOfKeysToCheck) {
    this.hostname = hostname;
    this.port = port;
    this.numberOfKeysToCheck = numberOfKeysToCheck;
  }

  /**
   * @param args[0] key manager hostname
   * @param args[1] key manager port
   * @param args[2] number of keys to check
   */
  public static void main(String[] args) throws IOException {
    String hostname = args[0];
    int port = Integer.parseInt(args[1]);
    int numberOfKeysToCheck = Integer.parseInt(args[2]);

    Client client = new Client(hostname, port, numberOfKeysToCheck);

    try {
      while (true) {
        client.workForKeyManager();
      }
    } catch (IOException e) {
      throw new IOException(
        "There was a problem connecting to the KeyManager. " +
          "Maybe the key was already found? " +
          "Or maybe there actually was a problem connecting. ",
        e
      );
    }
  }

  private void workForKeyManager() throws IOException {
    String askForWorkResponse = findWorkToDo();
    String matchingKey = processKeys(askForWorkResponse);
    sendWorkResult(matchingKey);
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

  private void sendWorkResult(String matchingKey) throws IOException {
    talkToManager((socket, in, out) -> {
      String completionMessage = String.format(
        "%s %s %d %s",
        RESPONSE,
        socket.getLocalAddress().getHostAddress(),
        socket.getLocalPort(),
        matchingKey == null ? "" : matchingKey
      ).trim();
      System.out.println("Output: " + completionMessage);
      out.println(completionMessage);
      return null;
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
      System.out.print('.');

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

  public interface NetworkWork<T> {
    T workWith(Socket socket, BufferedReader in, PrintWriter out)
      throws IOException;
  }
}
