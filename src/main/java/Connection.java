/**
 * A separate thread that is created by the server. This thread is used
 * to interact with the client.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection extends Thread {

  private static AtomicInteger count = new AtomicInteger(0); // track the connection

  public Connection(Socket c) {
    client = c;
    count.getAndIncrement();
  }


  /**
   * this method is invoked as a separate thread
   */
  public void run() {
    BufferedReader networkBin = null;
    OutputStreamWriter networkPout = null;

    try {
      /**
       * get the input and output streams associated with the socket.
       */
      networkBin = new BufferedReader(new InputStreamReader(client.getInputStream()));
      networkPout = new OutputStreamWriter(client.getOutputStream());

      /**
       * the following successively reads from the input stream and returns
       * what was read. The loop terminates with ^D or with the string "bye\r\n"
       * from the  input stream.
       */
      String response = null;
      while (true) {
        String line = networkBin.readLine();
        System.out.println("Client " + count + ": " + line);
        if ((line == null) || line.equals("bye")) {
          break;
        }
        if (line.equals("Knock, knock")) {
          response = "Who's there?";
        } else if (line.equals("Canoe")) {
          response = "Canoe who?";
        } else if (line.equals("Canoe do my homework?")) {
          response = "<<<<groan>>>>";
        }

        // send the response plus a return and newlines (as expected by readLine)
        networkPout.write(response + "\r\n");
        System.out.println("Server: " + response);
        // force the send to prevent buffering
        networkPout.flush();
      }
    } catch (IOException ioe) {
      System.err.println(ioe);
    } finally {
      try {
        if (networkBin != null)
          networkBin.close();
        if (networkPout != null)
          networkPout.close();
        if (client != null)
          client.close();
      } catch (IOException ioee) {
        System.err.println(ioee);
      }
    }
  }

  private Socket client;
}

