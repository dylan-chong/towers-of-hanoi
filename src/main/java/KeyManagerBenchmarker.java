import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Assumes that the clients run continuously
 */
public class KeyManagerBenchmarker {

  private static final int NUMBER_OF_REPEATS = 2;

  /**
   * @param args The same arguments as {@link KeyManager#main(String[])}
   */
  public static void main(String[] args) {
    List<Long> times = new ArrayList<>();
    for (int i = 0; i < NUMBER_OF_REPEATS; i++) {
      try {
        times.add(
          time(() -> {
            KeyManager.main(args);
            return null;
          })
        );
        Thread.sleep(10_000);
      } catch (Exception e) {
        e.printStackTrace();
        i--;
      }
    }

    double averageTime = times.stream()
      .mapToLong(value -> value)
      .average()
      .orElseThrow(AssertionError::new);

    // https://www.mathsisfun.com/data/standard-deviation-formulas.html
    double standardDeviation = Math.sqrt(
      1.0 / times.size() *
        times.stream()
          .mapToDouble(value -> Math.pow(value - averageTime, 2D))
          .sum()
    );

    System.out.println("- Results:             times = " + times);
    System.out.println("- Results:       averageTime = " + averageTime);
    System.out.println("- Results: standardDeviation = " + standardDeviation);
  }

  private static long time(Callable<?> callable) throws Exception {
    long start = System.currentTimeMillis();
    callable.call();
    long end = System.currentTimeMillis();

    return end - start;
  }
}
