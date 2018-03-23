package main

import main.part1.KNearestNeighbourClassifier

fun main(args: Array<String>) {
  try {
    acceptInputs(args)
  } catch (e: Exception) {
    throw Error("""

        Some error occurred. Maybe you passed incorrect parameters?
        Try something like:
        java -jar submit/app.jar part1 trainingFile testFile

    """.trimIndent(), e)
  }
}

fun acceptInputs(args: Array<String>) {
  when (args[0]) {
    "part1" -> {
      KNearestNeighbourClassifier().run(args[1], args[2])
    }

    else -> {
      throw IllegalArgumentException("Invalid 1st argument")
    }
  }
}
