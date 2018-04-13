
import gui.Gui
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.nio.file.Files

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
    val toDrop = 2
    val remaining = if (times.size > toDrop) times.drop(toDrop) else times
    val average = remaining.average()
    println("Milliseconds per frame average: $average")

    // Logging
    val resultsFile = File("results.tmp")
    if (!resultsFile.exists()) {
      resultsFile.createNewFile()
    }
    val allResults = Files
      .readAllLines(resultsFile.toPath())
      .toMutableList()
    allResults.add("{currentTime: ${System.currentTimeMillis()}, average: $average, times: $times}")
    Files.write(resultsFile.toPath(), allResults)
  }
})