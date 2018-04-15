
import gui.Gui
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.assertEquals

class ModelSpec : Spek({

  given("a parallel model") {
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
})