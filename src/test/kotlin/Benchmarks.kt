
import gui.Gui
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.nio.file.Files

class Benchmarks : Spek({

  fun run(isParallel: Boolean) {
    val times = (1..10).map {
      // Start the simulation
      Gui.main(if (isParallel) arrayOf("--parallel") else emptyArray())

      // Wait for results from printAvgTime method in Gui.java
      var millisecondsPerFrame = -1
      while (millisecondsPerFrame == -1) {
        Thread.sleep(200)
        millisecondsPerFrame = Gui.staticAvgTime
      }

      // Kill the simulation
      Gui.instance.forceClose()

      millisecondsPerFrame
    }

    println("Milliseconds per frame results: $times")

    // Drop first few to allow for warm up
    val toDrop = 2
    val remaining = if (times.size > toDrop) times.drop(toDrop) else times
    val average = remaining.average()
    println("Milliseconds per frame average: $average")

    // Logging
    val resultsFile = File("src/main/resources/results.txt")
    if (!resultsFile.exists()) {
      resultsFile.createNewFile()
    }
    val allResults = Files
      .readAllLines(resultsFile.toPath())
      .toMutableList()
    allResults.add("{currentTime: ${System.currentTimeMillis()}, average: $average, times: $times}")
    Files.write(resultsFile.toPath(), allResults)
  }

  listOf(false, true).forEach { parallel ->
    val type = if (parallel) "parallel" else "sequential"
    it("runs and print out the frame times ($type)") {
      run(parallel)
    }
  }
})