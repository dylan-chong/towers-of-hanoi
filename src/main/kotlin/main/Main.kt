package main

import main.part1.KNearestNeighbourRunner
import main.part2.DecisionTreeRunner

val instructions = """

    Examples of running this program:

    java -jar submit/app.jar part1 <trainingFile> <testFile> <kValue>
    java -jar submit/app.jar part1 src/main/resources/ass1-data/part1/iris-training.txt src/main/resources/ass1-data/part1/iris-test.txt 3

    java -jar submit/app.jar part2 <trainingFile> <testFile>
    java -jar submit/app.jar part2 src/main/resources/ass1-data/part2/hepatitis-training.dat src/main/resources/ass1-data/part2/hepatitis-test.dat

""".trimIndent()

fun main(args: Array<String>) {
  if (args.isEmpty()) {
    println(instructions)
    return
  }

  try {
    acceptInputs(args)
  } catch (e: Exception) {
    println("Some error occurred. Maybe you passed incorrect parameters?")
    println(instructions)
    throw Error(e)
  }
}

fun acceptInputs(args: Array<String>) {
  when (args[0]) {
    "part1" -> {
      KNearestNeighbourRunner().run(args[1], args[2], args[3].toInt())
    }

    "part2" -> {
      DecisionTreeRunner().run(args[1], args[2])
    }

    else -> {
      throw IllegalArgumentException("Invalid 1st argument")
    }
  }
}
