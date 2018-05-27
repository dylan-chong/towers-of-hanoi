import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class KeyManager {

  /**
   * @param args[0] key represented as a big integer value
   * @param args[1] key size
   * @param args[2] ciphertext encoded as base64 value
   * @param args[3] optional: port number
   */
  public static void main(String[] args) throws IOException {
    BigInteger nextKeyToCheck = new BigInteger(args[0]);
    int keySize = Integer.parseInt(args[1]);
    String ciperText = args[2];

    String correctKey = null;

    int port = 0; // auto pick
    if (args.length > 3) {
      port = Integer.parseInt(args[3]);
    }

    try (ServerSocket socket = new ServerSocket(port)) {
      System.out.println("Waiting for connections on " + socket.getLocalPort());

      while (correctKey == null) {
        Socket client = socket.accept();
        // TODO Do works on a new thread or pool

        try (
          BufferedReader input = new BufferedReader(
            new InputStreamReader(client.getInputStream())
          );
          PrintWriter output = new PrintWriter(
            client.getOutputStream(), true
          )
        ) {
          String message = input.readLine();
          System.out.println("Input: " + message);
          String[] messageSplit = message.split(" ");

          if (message.startsWith(Client.ASK_FOR_WORK)) {
            int numberOfKeysToCheck = Integer.parseInt(messageSplit[3]);

            String responseMessage = String.format(
              "%s %d %s",
              nextKeyToCheck,
              keySize,
              ciperText
            );
            output.println(responseMessage);
            System.out.println("Output: " + responseMessage);
            nextKeyToCheck = nextKeyToCheck.add(
              BigInteger.valueOf(numberOfKeysToCheck)
            );
          } else if (message.startsWith(Client.RESPONSE)) {
            if (messageSplit.length >= 4) {
              correctKey = messageSplit[3];
            }
          } else {
            throw new IllegalArgumentException(message);
          }
        }
      }

      System.out.println("Correct key: " + correctKey);
    }
  }
}
