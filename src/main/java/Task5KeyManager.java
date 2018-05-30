import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Distributed key cracker. This class delegates the work to {@link Client}s
 * that say they are available to do work.
 * <p>
 * Each time a client asks for work, a unique amount of work is given in return.
 * The client can then message this manager later with the result of the work.
 */
public class Task5KeyManager {

  private static final long TIMEOUT_MS = 1000 * 20;

  /**
   * @param args[0] key represented as a big integer value
   * @param args[1] key size
   * @param args[2] ciphertext encoded as base64 value
   * @param args[3] optional: port number
   * @param args[4] optional: simulate slow network (true)
   * @param args[5] optional: time limit (seconds)
   */
  public static void main(String[] args) throws IOException, InterruptedException {
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

    long maxDuration = 0;
    if (args.length > 5) {
      maxDuration = Long.parseLong(args[5]) * 1000;
    }

    Task5KeyManager keyManager = new Task5KeyManager(
      nextKeyToCheck,
      keySize,
      cipherText,
      simulateSlowNetwork,
      maxDuration == 0 ?
        Long.MAX_VALUE : maxDuration + System.currentTimeMillis()
    );
    keyManager.serve(port);
  }

  private final int keySize;
  private final String cipherText;
  private final boolean simulateSlowNetwork;
  private final long endTime;

  private SortedSet<Job> jobs;
  private String correctKey = null;
  private BigInteger nextKeyToCheck;

  public Task5KeyManager(
    BigInteger nextKeyToCheck,
    int keySize,
    String cipherText,
    boolean simulateSlowNetwork,
    long endTime
  ) {
    this.nextKeyToCheck = nextKeyToCheck;
    this.keySize = keySize;
    this.cipherText = cipherText;
    this.simulateSlowNetwork = simulateSlowNetwork;
    this.endTime = endTime;
    this.jobs = new TreeSet<>(Job.comparator());
  }

  public void serve(int port) throws IOException, InterruptedException {
    try (ServerSocket socket = new ServerSocket(port)) {
      socket.setSoTimeout(1000);
      System.out.println("Waiting for connections on " + socket.getLocalPort());

      while (correctKey == null) {
        if (simulateSlowNetwork) {
          Thread.sleep(1000);
        }

        if (System.currentTimeMillis() > endTime) {
          throw new IllegalStateException("Took too long to finish!");
        }

        Socket client = null;

        try {
          client = socket.accept();
        } catch (SocketTimeoutException e) {
          continue;
        }

        serveClient(client, (socket1, in, out) -> {
          String message = in.readLine();
          System.out.println("Input: " + message);
          handleResponse(message, out);
          return null;
        });
        client.close();
      }

      System.out.println("Correct key: " + correctKey);
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

  private void handleResponse(String message, PrintWriter output) {
    String[] messageSplit = message.split(" ");

    if (message.startsWith(Client.ASK_FOR_WORK)) {
      sendNextJob(messageSplit, output);
    } else if (message.startsWith(Client.RESPONSE)) {
      handleFinishedJob(messageSplit);
    } else {
      throw new IllegalArgumentException(message);
    }
  }

  private void handleFinishedJob(String[] messageSplit) {
    BigInteger startingKey = new BigInteger(messageSplit[3]);
    Job matchingJob = jobs.stream()
      .filter(job -> job.firstKey.equals(startingKey))
      .findAny()
      .orElse(null);

    if (matchingJob == null) {
      // The job timed out and was given to someone else
      return;
    }

    jobs.remove(matchingJob);

    if (messageSplit.length < 5) {
      return;
    }

    correctKey = messageSplit[4];
  }

  private void sendNextJob(String[] askMessageSplit, PrintWriter output) {
    int numberOfKeysToCheck = Integer.parseInt(askMessageSplit[3]);

    Job job = findNextTimedOutJob(numberOfKeysToCheck);
    if (job == null) {
      job = createNextJob(numberOfKeysToCheck);
    }

    startJob(job);

    String responseMessage = String.format(
      "%s %d %s",
      job.firstKey,
      keySize,
      cipherText
    );
    output.println(responseMessage);
    System.out.println("Output: " + responseMessage);
  }

  private void startJob(Job job) {
    job.startTime = System.currentTimeMillis();
    jobs.add(job);
  }

  private Job findNextTimedOutJob(int numberOfKeysToCheck) {
    Job timedOutJob = jobs.stream()
      .filter(job -> System.currentTimeMillis() - job.startTime > TIMEOUT_MS)
      .findAny()
      .orElse(null);

    if (timedOutJob == null) {
      return null;
    }

    jobs.remove(timedOutJob);

    if (timedOutJob.numberOfKeysToCheck <= numberOfKeysToCheck) {
      return timedOutJob;
    }

    // TODO LAST
    throw new Error();
  }

  private Job createNextJob(int numberOfKeysToCheck) {
    Job job = new Job(nextKeyToCheck, numberOfKeysToCheck);
    nextKeyToCheck = nextKeyToCheck.add(BigInteger.valueOf(numberOfKeysToCheck));
    return job;
  }

  private static class Job {
    public static Comparator<Job> comparator() {
      return Comparator.comparingLong(value -> value.startTime);
    }

    private final BigInteger firstKey;
    private final long numberOfKeysToCheck;
    private long startTime = Long.MAX_VALUE;

    private Job(BigInteger firstKey, long numberOfKeysToCheck) {
      this.firstKey = firstKey;
      this.numberOfKeysToCheck = numberOfKeysToCheck;
    }
  }
}
