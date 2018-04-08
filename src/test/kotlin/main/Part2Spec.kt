package main

import main.part2.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File

class Part2Spec : Spek({

  listOf(
    "DecisionTree with SimpleChildFactory" to { it: DecisionTreeData ->
      DecisionTree.newRoot(it, SimpleChildFactory())
    },
    "DecisionTree with ProperChildFactory" to { it: DecisionTreeData ->
      DecisionTree.newRoot(it, ProperChildFactory())
    },
    "BaselineClassifier" to { it: DecisionTreeData ->
      BaselineClassifier(it)
    }
  )
    .forEach { pair ->
      val (name, factory) = pair
      val suffix = "using $name"

      it("does not crash $suffix") {
        Part2DecisionTreeRunner().run(
          "src/main/resources/ass1-data/part2/hepatitis-training.dat",
          "src/main/resources/ass1-data/part2/hepatitis-test.dat",
          listOf(factory)
        )
      }

      it("does not crash when creating the representation $suffix") {
        val data = DecisionTreeData.fromFile(
          File("src/main/resources/ass1-data/part2/hepatitis-training.dat")
            .toPath()
        )

        val decisionTree = factory(data)

        val representation = decisionTree
          .representation()
          .joinToString(separator = "\n")
        println(representation)
      }
    }
})