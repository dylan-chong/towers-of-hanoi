package junit;

import assignment5.Encoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dylan on 3/06/17.
 */
public class TestUtils {
    public static final boolean RUN_BENCHMARKS = false;

    private static final List<Integer> WINDOW_SIZES
            = Arrays.asList(255, 1023, 4095);

    public static String getTextFromFile(File path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path.getPath()));
            StringBuilder text = new StringBuilder();
            for (String line : lines)
                text.append(line).append('\n');
            return text.toString();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static long getRuntimeDuration(Runnable runnable) {
        System.gc();
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static void runBenchmarkForDifferentWindowSizes(
            BiFunction<String, Integer, Encoder> encoderFactory) {
        if (!RUN_BENCHMARKS)
            return;

        List<File> testFiles =
                Arrays.stream(new File("./data/").listFiles(File::isFile))
                        .filter(file -> file.getName().endsWith(".txt"))
                        .collect(Collectors.toList());

        // testFiles = Arrays.asList(new File("./data/war_and_peace.txt"));

        for (File file : testFiles) {
            String text = TestUtils.getTextFromFile(file);

            System.out.printf("File : %s\n", file);

            for (Integer windowSize : WINDOW_SIZES) {
                long start = System.currentTimeMillis();
                Encoder encoder = encoderFactory.apply(text, windowSize);
                String encoded = encoder.encode();
                long end = System.currentTimeMillis();

                System.out.printf("" +
                                "- window size            : %s\n" +
                                "  - input length         : %d\n" +
                                "  - output length        : %d\n" +
                                "  - out/in ratio         : %f\n" +
                                "  - compression duration : %f s\n",
                        windowSize,
                        text.length(),
                        encoded.length(),
                        encoded.length() * 1.0 / text.length(),
                        (end - start) / 1000.0
                );

                assertEquals(text, encoder.decode(encoded));
            }

            System.out.println();
        }
    }
}
