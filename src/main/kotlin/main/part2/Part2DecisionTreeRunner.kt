package main.part2

import main.part2.DecisionTreeData.Instance
import java.io.File

class Part2DecisionTreeRunner {

  fun run(trainingFile: String, testFile: String) {
    val trainingData = DecisionTreeData.fromFile(File(trainingFile).toPath())
    val testData = DecisionTreeData.fromFile(File(testFile).toPath())
    run(trainingData, testData)
  }

  fun run(trainingData: DecisionTreeData, testData: DecisionTreeData) {
    val simpleDecisionTree = DecisionTree.newRoot(
      trainingData,
      SimpleChildFactory()
    )

    if (trainingData.classNames != testData.classNames
      || trainingData.featureNames != testData.featureNames) {
      throw IllegalArgumentException(
        "Training and test datasets are too different"
      )
    }

    println(
      simpleDecisionTree
        .representation()
        .joinToString(separator = "\n")
    )

    val results = testData
      .instances
      .map { it to simpleDecisionTree.calculateClass(it) }

    printResults(results)
  }

  private fun printResults(results: List<Pair<Instance, ClassKind>>) {
    val correct = results.count(::isCorrect)
    val incorrect = results.size - correct
    val percentCorrect = 100 * correct / results.size

    println("Results: $percentCorrect% correct ($correct:$incorrect)")
    results.forEachIndexed { index, result ->
      println(
        "$index. " +
          "isCorrect: ${isCorrect(result)}, " +
          "result: ${result.first}, " +
          "actualClassKind: ${result.second} "
      )
    }
  }

  private fun isCorrect(pair: Pair<Instance, ClassKind>): Boolean {
    return pair.first.classKind == pair.second
  }
}

