package main

import main.part1.KNearestNeighbourClassifier

fun main(args: Array<String>) {
  try {
    acceptInputs(args)
  } catch (e: Exception) {
    throw Error("""

        Some error occurred. Maybe you passed incorrect parameters?

        Try something like:
        java -jar submit/app.jar part1 <trainingFile> <testFile> <kValue>
        java -jar submit/app.jar part1 src/main/resources/ass1-data/part1/iris-training.txt src/main/resources/ass1-data/part1/iris-test.txt 3

    """.trimIndent(), e)
  }
}

fun acceptInputs(args: Array<String>) {
  when (args[0]) {
    "part1" -> {
      KNearestNeighbourClassifier().run(args[1], args[2], args[3].toInt())
    }

    else -> {
      throw IllegalArgumentException("Invalid 1st argument")
    }
  }
}
