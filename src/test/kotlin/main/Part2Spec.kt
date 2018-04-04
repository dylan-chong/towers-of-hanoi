package main

import main.part2.DecisionTree
import main.part2.DecisionTreeData
import main.part2.Part2DecisionTreeRunner
import main.part2.SimpleChildFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File

class Part2Spec : Spek({

  it("does not crash") {
    Part2DecisionTreeRunner().run(
      "src/main/resources/ass1-data/part2/hepatitis-training.dat",
      "src/main/resources/ass1-data/part2/hepatitis-test.dat"
    )
  }

  it("does not crash when creating the representation") {
    val decisionTree = DecisionTree.newRoot(
      DecisionTreeData.fromFile(
        File("src/main/resources/ass1-data/part2/hepatitis-training.dat")
          .toPath()
      ),
      SimpleChildFactory()
    )

    val representation = decisionTree
      .representation()
      .joinToString(separator = "\n")
    println(representation)
  }
})