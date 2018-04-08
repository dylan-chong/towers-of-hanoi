package main

import main.part1.IrisSet
import main.part1.Part1KNearestNeighbourRunner
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.util.stream.IntStream

class Part1Spec : Spek({

  context("given the training and test sets") {

    val trainingSet = IrisSet.loadFile(
      File("src/main/resources/ass1-data/part1/iris-training.txt").toPath()
    )
    val testSetTrueAnswers = IrisSet.loadFile(
      File("src/main/resources/ass1-data/part1/iris-test.txt").toPath()
    )

    IntStream
      .range(1, 70)
      .forEach { k ->
        it("runs with k: $k") {
          val classifier = Part1KNearestNeighbourRunner()
          classifier.run(trainingSet, testSetTrueAnswers, k)
        }
      }
  }
})
