
import gui.Gui
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class Benchmarks : Spek({

  it("runs and print out the run times") {

    val times = (1..10).map {
      // Start the simulation
      Gui.main(emptyArray())

      var millisecondsPerFrame = -1
      while (millisecondsPerFrame == -1) {
        Thread.sleep(200)
        millisecondsPerFrame = Gui.staticAvgTime
      }
      Gui.staticAvgTime = -1

      // Kill the simulation
      Gui.instance.forceClose()
      Thread.sleep(1000)

      millisecondsPerFrame
    }

    println("Milliseconds per frame results: $times")
    // Drop first few to allow for warm up
    println("Milliseconds per frame average: ${times.drop(3).average()}")
  }
})