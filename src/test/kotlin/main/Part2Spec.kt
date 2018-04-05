package main

import main.part2.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File

class Part2Spec : Spek({

  listOf(SimpleChildFactory(), ProperChildFactory())
    .forEach { factory ->
      val suffix = "for factory ${factory.javaClass.simpleName}"

      it("does not crash $suffix") {
        Part2DecisionTreeRunner().run(
          "src/main/resources/ass1-data/part2/hepatitis-training.dat",
          "src/main/resources/ass1-data/part2/hepatitis-test.dat",
          { listOf(factory) }
        )
      }

      it("does not crash when creating the representation $suffix") {
        val decisionTree = DecisionTree.newRoot(
          DecisionTreeData.fromFile(
            File("src/main/resources/ass1-data/part2/hepatitis-training.dat")
              .toPath()
          ),
          factory
        )

        val representation = decisionTree
          .representation()
          .joinToString(separator = "\n")
        println(representation)
      }
    }
})