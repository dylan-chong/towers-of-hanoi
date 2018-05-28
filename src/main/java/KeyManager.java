import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Distributed key cracker. This class delegates the work to {@link Client}s
 * that say they are available to do work.
 * <p>
 * Each time a client asks for work, a unique amount of work is given in return.
 * The client can then message this manager later with the result of the work.
 */
public class KeyManager {

  /**
   * @param args[0] key represented as a big integer value
   * @param args[1] key size
   * @param args[2] ciphertext encoded as base64 value
   * @param args[3] optional: port number
   * @param args[4] optional: simulate slow network (true)
   */
  public static void main(String[] args) throws IOException {
    BigInteger nextKeyToCheck = new BigInteger(args[0]);
    int keySize = Integer.parseInt(args[1]);
    String cipherText = args[2];

    int port = 0; // auto pick
    if (args.length > 3) {
      port = Integer.parseInt(args[3]);
    }

    boolean simulateSlowNetwork = false;
    if (args.length > 4) {
      simulateSlowNetwork = Boolean.parseBoolean(args[4]);
    }

    KeyManager keyManager = new KeyManager(
      nextKeyToCheck,
      keySize,
      cipherText,
      simulateSlowNetwork
    );
    keyManager.serve(port);
  }

  private final int keySize;
  private final String cipherText;
  private final boolean simulateSlowNetwork;

  private volatile String correctKey = null;
  private volatile BigInteger nextKeyToCheck;

  public KeyManager(
    BigInteger nextKeyToCheck,
    int keySize,
    String cipherText,
    boolean simulateSlowNetwork
  ) {
    this.nextKeyToCheck = nextKeyToCheck;
    this.keySize = keySize;
    this.cipherText = cipherText;
    this.simulateSlowNetwork = simulateSlowNetwork;
  }

  public void serve(int port) throws IOException {
    try (ServerSocket socket = new ServerSocket(port)) {
      System.out.println("Waiting for connections on " + socket.getLocalPort());

      while (correctKey == null) {
        Socket client = socket.accept();
        if (simulateSlowNetwork) {
          Thread.sleep(1000);
        }

        serveClient(client, (socket1, in, out) -> {
          String message = in.readLine();
          System.out.println("Input: " + message);
          handleResponse(message, out);
          return null;
        });
      }

      System.out.println("Correct key: " + correctKey);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private <T> T serveClient(
    Socket client,
    NetworkWork<T> work
  ) throws IOException {
    try (
      BufferedReader input = new BufferedReader(
        new InputStreamReader(client.getInputStream())
      );
      PrintWriter output = new PrintWriter(
        client.getOutputStream(), true
      )
    ) {
      return work.workWith(client, input, output);
    }
  }

  private synchronized void handleResponse(String message, PrintWriter output) {
    String[] messageSplit = message.split(" ");

    if (message.startsWith(Client.ASK_FOR_WORK)) {
      nextKeyToCheck = findNextKeyToCheck(messageSplit, output, nextKeyToCheck);
    } else if (message.startsWith(Client.RESPONSE)) {
      if (messageSplit.length >= 4) {
        correctKey = messageSplit[3];
      }
    } else {
      throw new IllegalArgumentException(message);
    }
  }

  private BigInteger findNextKeyToCheck(
    String[] askMessageSplit,
    PrintWriter output,
    BigInteger nextKeyToCheck
  ) {
    int numberOfKeysToCheck = Integer.parseInt(askMessageSplit[3]);

    String responseMessage = String.format(
      "%s %d %s",
      nextKeyToCheck,
      keySize,
      cipherText
    );
    output.println(responseMessage);
    System.out.println("Output: " + responseMessage);

    return nextKeyToCheck.add(BigInteger.valueOf(numberOfKeysToCheck));
  }
}
