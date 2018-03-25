package main.part1

import java.io.File
import java.lang.IllegalArgumentException

class KNearestNeighbourRunner {

  fun run(trainingFile: String, testFile: String, k: Int) {
    val trainingSet = IrisSet.loadFile(File(trainingFile).toPath())
    val testSetTrueAnswers = IrisSet.loadFile(File(testFile).toPath())
    run(trainingSet, testSetTrueAnswers, k)
  }

  fun run(trainingSet: IrisSet, testSetTrueAnswers: IrisSet, k: Int) {
    val testSetCalculatedAnswers = trainingSet.calculateClasses(
      testSetTrueAnswers.withoutClasses(),
      k
    )

    val results = getResults(testSetCalculatedAnswers, testSetTrueAnswers)
    val correct = results.filter { it.isCorrect() }.count()
    val incorrect = results.size - correct
    val percentCorrect = 100 * correct / results.size

    println("Results: $percentCorrect% correct ($correct:$incorrect)")
    results.forEachIndexed { index, result ->
      println(
        "$index. " +
          "isCorrect: ${result.isCorrect()}, " +
          "result: ${result.resultInstance}, " +
          "actualClassKind: ${result.resultInstance.classKind} "
      )
    }
  }

  private fun getResults(
    testSetCalculatedAnswers: IrisSet,
    testSetTrueAnswers: IrisSet
  ): List<Result> {
    val calculatedInstances = testSetCalculatedAnswers.instances
    val trueInstances = testSetTrueAnswers.instances
    return (calculatedInstances zip trueInstances)
      .map { Result(it.first, it.second) }
  }

  class Result(
    val resultInstance: IrisInstance,
    val correctInstance: IrisInstance
  ) {
    init {
      if (!resultInstance.equalsIgnoreClass(correctInstance)) {
        throw IllegalArgumentException(this.toString())
      }
    }

    fun isCorrect(): Boolean {
      return resultInstance.classKind == correctInstance.classKind
    }
  }
}
