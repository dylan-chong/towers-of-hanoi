package main

import main.part1.Part1KNearestNeighbourRunner
import main.part2.Part2DecisionTreeRunner

const val run = "java -jar submit/app.jar"
const val data = "src/main/resources/ass1-data/"
val instructions = """

    Examples of running this program:

    $run part1 <trainingFile> <testFile> <kValue>
    $run part1 ${data}part1/iris-training.txt ${data}part1/iris-test.txt 3

    $run part2 <trainingFile> <testFile>
    $run part2 ${data}part2/hepatitis-training.dat ${data}part2/hepatitis-test.dat
    $run part2 ${data}part2/golf-training.dat ${data}part2/golf-test.dat

    $run part2-q2 <directoryContainingSplitFiles>
    $run part2-q2 ${data}part2/

""".trimIndent()

fun main(args: Array<String>) {
  if (args.isEmpty()) {
    println(instructions)
    throw IllegalArgumentException(
      "No arguments given: see the instructions above"
    )
  }

  try {
    acceptInputs(args)
  } catch (e: Exception) {
    println("Some error occurred. Maybe you passed incorrect parameters?")
    println(instructions)
    throw IllegalArgumentException(e)
  }
}

private fun acceptInputs(args: Array<String>) {
  fun requireArgsSize(size: Int) {
    val actual = args.size

    if (actual == size) {
      return
    }

    val message = "Invalid number of arguments (expected $size, but got $actual)"
    println(message)
    println(instructions)

    throw IllegalArgumentException(message)
  }

  when (args[0]) {
    "part1" -> {
      requireArgsSize(4)
      Part1KNearestNeighbourRunner().run(args[1], args[2], args[3].toInt())
    }

    "part2" -> {
      requireArgsSize(3)
      Part2DecisionTreeRunner().run(args[1], args[2])
    }

    "part2-q2" -> {
      requireArgsSize(2)
      Part2DecisionTreeRunner().runWithSplittable(args[1])
    }

    else -> {
      throw IllegalArgumentException("Invalid 1st argument")
    }
  }
}

