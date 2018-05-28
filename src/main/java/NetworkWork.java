import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public interface NetworkWork<T> {
  T workWith(Socket socket, BufferedReader in, PrintWriter out)
    throws IOException;
}
