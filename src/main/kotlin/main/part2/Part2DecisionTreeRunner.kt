package main.part2

import kotlin.math.roundToInt

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
  ): Double {
    val trainingData = DecisionTreeData.fromFile(trainingFile)
    val testData = DecisionTreeData.fromFile(testFile)
    return run(trainingData, testData, treeFactories)
  }

  fun run(
    trainingData: DecisionTreeData,
    testData: DecisionTreeData,
    treeFactories: Collection<ClassifierFactory> = defaultFactories
  ): Double {
    requireCompatibility(trainingData, testData)

    return treeFactories
      .map { it(trainingData) }
      .map { classifier -> calculateAndPrintResults(classifier, testData) }
      .average()
  }

  fun runWithSplittable(
    dataDirectory: String,
    numberOfDataSets: Int = 10,
    treeFactories: Collection<ClassifierFactory> = defaultFactories
  ): Double {
    val trainingToTestDatas = (1..numberOfDataSets).map { i ->
      val trainingFile = "$dataDirectory/hepatitis-training-run%02d.dat".format(i)
      val testFile = "$dataDirectory/hepatitis-test-run%02d.dat".format(i)

      with (DecisionTreeData) {
        fromFile(trainingFile) to fromFile(testFile)
      }
    }

    val accuracies = trainingToTestDatas.map { (trainingData, testData) ->
      requireCompatibility(trainingData, testData)
      run(trainingData, testData, treeFactories)
    }

    val percentages = accuracies.map { it.times(100).roundToInt() }
    val averageAccuracy = accuracies.average()

    println("# Summary results:")
    println("Accuracies of individual training/test set pairs: $percentages")
    println("Average accuracy: ${averageAccuracy.times(100).roundToInt()}")

    return averageAccuracy
  }

  private fun requireCompatibility(
    trainingData: DecisionTreeData,
    testData: DecisionTreeData
  ) {
    // TODO
//    if (!trainingData.isCompatibleWith(testData)) {
//      throw IllegalArgumentException(
//        "Training and test datasets are too different"
//      )
//    }
  }

  private fun calculateAndPrintResults(
    classifier: Classifier,
    testData: DecisionTreeData
  ): Double {
    val results = testData
      .instances
      .map { it to classifier.calculateClass(it) }

    return ResultsPrinter().printResults(
      classifier.name(),
      classifier
        .representation()
        .joinToString(separator = "\n"),
      results
    )
  }
}

