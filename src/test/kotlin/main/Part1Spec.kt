package main

import main.part1.KNearestNeighbourClassifier
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.stream.IntStream

class Part1Spec : Spek({

  IntStream
    .range(1, 70)
    .forEach { k ->
      it("runs with k: $k") {
        val classifier = KNearestNeighbourClassifier()
        classifier.run(
          "src/main/resources/ass1-data/part1/iris-training.txt",
          "src/main/resources/ass1-data/part1/iris-test.txt",
          k
        )
      }
    }
})