package main.part2

import java.io.File

class DecisionTreeRunner {

  fun run(trainingFile: String, testFile: String) {
    val trainingData = DecisionTreeData.fromFile(File(trainingFile).toPath())
    val testData = DecisionTreeData.fromFile(File(testFile).toPath())
    run(trainingData, testData)
  }

  fun run(trainingData: DecisionTreeData, testData: DecisionTreeData) {
    val decisionTree = DecisionTree.newRoot(trainingData)
  }
}

