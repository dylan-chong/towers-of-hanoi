package main

import main.part2.DecisionTreeRunner
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class Part2Spec : Spek({

  it("does not crash") {
    DecisionTreeRunner().run(
      "src/main/resources/ass1-data/part2/hepatitis-training.dat",
      "src/main/resources/ass1-data/part2/hepatitis-test.dat"
    )
  }
})