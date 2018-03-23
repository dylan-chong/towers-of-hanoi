package main

import main.part1.KNearestNeighbourClassifier
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class Part1Spec : Spek({

  it("runs") {
    val classifier = KNearestNeighbourClassifier()
    classifier.run(
      "src/main/resources/ass1-data/part1/iris-training.txt",
      "src/main/resources/ass1-data/part1/iris-test.txt"
    )
  }
})