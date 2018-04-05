package main

import main.part1.Part1KNearestNeighbourRunner
import main.part2.Part2DecisionTreeRunner
import main.part2.ProperChildFactory

const val run = "java -jar submit/app.jar"
const val data = "src/main/resources/ass1-data/"
val instructions = """

    Examples of running this program:

    $run part1 <trainingFile> <testFile> <kValue>
    $run part1 ${data}part1/iris-training.txt ${data}part1/iris-test.txt 3

    $run part2 <trainingFile> <testFile>
    $run part2 ${data}part2/hepatitis-training.dat ${data}part2/hepatitis-test.dat

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
      Part1KNearestNeighbourRunner().run(args[1], args[2], args[3].toInt())
    }

    "part2" -> {
      Part2DecisionTreeRunner().run(args[1], args[2]) {
        listOf(ProperChildFactory())
      }
    }

    else -> {
      throw IllegalArgumentException("Invalid 1st argument")
    }
  }
}
