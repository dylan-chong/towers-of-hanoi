package main.part2

import main.part2.DecisionTreeData.Instance
import java.io.File

typealias ClassifierFactory = (DecisionTreeData) -> Classifier

class Part2DecisionTreeRunner {

  val defaultFactories: Collection<ClassifierFactory> by lazy {
    listOf(
      { it: DecisionTreeData ->
        BaselineClassifier(it)
      },
      { it: DecisionTreeData ->
        DecisionTree.newRoot(it, ProperChildFactory())
      }
    )
  }

  fun run(
    trainingFile: String,
    testFile: String,
    treeFactories: Collection<ClassifierFactory> = defaultFactories
  ) {
    val trainingData = DecisionTreeData.fromFile(File(trainingFile).toPath())
    val testData = DecisionTreeData.fromFile(File(testFile).toPath())
    run(trainingData, testData, treeFactories)
  }

  fun run(
    trainingData: DecisionTreeData,
    testData: DecisionTreeData,
    treeFactories: Collection<ClassifierFactory> = defaultFactories
  ) {
    if (trainingData.classNames != testData.classNames
      || trainingData.featureNames != testData.featureNames) {
      throw IllegalArgumentException(
        "Training and test datasets are too different"
      )
    }

    treeFactories
      .map { it(trainingData) }
      .forEach { classifier ->
        val results = testData
          .instances
          .map { it to classifier.calculateClass(it) }

        printResults(
          classifier.name(),
          classifier
            .representation()
            .joinToString(separator = "\n"),
          results
        )
      }
  }

  private fun printResults(
    title: String,
    description: String,
    results: List<Pair<Instance, ClassKind>>
  ) {
    println("Results for $title")

    println(description)

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

