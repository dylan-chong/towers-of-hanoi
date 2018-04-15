
import gui.Gui
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.system.measureNanoTime

class ModelSpec : Spek({

  given("a parallel model (variant 1)") {
    val parallelModel = Gui.createModel(true)

    val sequentialModel = Gui.createModel(false)
    val models = listOf(parallelModel, sequentialModel)

    for (i in 1..2000) {
      on("stepping all models to step $i") {
        models.forEach { it.step() }

        it("should behave the same as the sequential one") {
          val parallelParticles = parallelModel.p.toHashSet()
          val sequentialParticles = sequentialModel.p.toHashSet()

          assertEquals(parallelParticles, sequentialParticles)
        }
      }
    }
  }

  // Another variation of the test above, but harder to debug
  // It also acts as a benchmark
  (0..99).forEach { i ->

    it("the parallel and sequential models behave the same (variant 2) on iteration $i") {
      val parallelModel = Gui.createModel(true)

      val sequentialModel = Gui.createModel(false)
      val models = listOf(parallelModel, sequentialModel)

      models.forEachIndexed { index, model ->
        val nanos = measureNanoTime {
          (1..2000).forEach({ model.step() })
        }

        println("Model $index took ${nanos / 1000000}ms at iteration $i")
      }

      val parallelParticles = parallelModel.p.toHashSet()
      val sequentialParticles = sequentialModel.p.toHashSet()

      assertEquals(parallelParticles, sequentialParticles)
    }
  }
})