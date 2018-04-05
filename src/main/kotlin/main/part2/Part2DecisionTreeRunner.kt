package main.part2

import main.part2.DecisionTreeData.Instance
import java.io.File

class Part2DecisionTreeRunner {

  fun run(
    trainingFile: String,
    testFile: String,
    childFactoryFactories: () -> Collection<ChildFactory>
  ) {
    val trainingData = DecisionTreeData.fromFile(File(trainingFile).toPath())
    val testData = DecisionTreeData.fromFile(File(testFile).toPath())
    run(trainingData, testData, childFactoryFactories)
  }

  fun run(
    trainingData: DecisionTreeData,
    testData: DecisionTreeData,
    childFactoryFactories: () -> Collection<ChildFactory>
  ) {
    if (trainingData.classNames != testData.classNames
      || trainingData.featureNames != testData.featureNames) {
      throw IllegalArgumentException(
        "Training and test datasets are too different"
      )
    }

    childFactoryFactories()
      .map { DecisionTree.newRoot(trainingData, it) }
      .forEach { tree ->
        val results = testData
          .instances
          .map { it to tree.calculateClass(it) }

        printResults(tree, results)
      }
  }

  private fun printResults(
    tree: DecisionTree,
    results: List<Pair<Instance, ClassKind>>
  ) {
    println("Results for ${tree.childFactory.javaClass.simpleName}")

    println(
      tree
        .representation()
        .joinToString(separator = "\n")
    )

    val correct = results.count(::isCorrect)
    val incorrect = results.size - correct
    val percentCorrect = 100 * correct / results.size

    println("Results: $percentCorrect% correct ($correct:$incorrect)")
    printLiveDieTotals(results)
    println()

    results.forEachIndexed { index, result ->
      println(
        "$index. " +
          "isCorrect: ${isCorrect(result)}, " +
          "result: ${result.first}, " +
          "actualClassKind: ${result.second} "
      )
    }
  }

  private fun printLiveDieTotals(results: List<Pair<Instance, ClassKind>>) {
    results
      .map { it.second }
      .toSet()
      .forEach { classKind: ClassKind ->
        val matchingResults = results.filter { it.first.classKind == classKind }
        val correct = matchingResults.count { isCorrect(it) }
        val total = matchingResults.count()
        println("$classKind: $correct correct out of $total")
      }
  }

  private fun isCorrect(pair: Pair<Instance, ClassKind>): Boolean {
    return pair.first.classKind == pair.second
  }
}

