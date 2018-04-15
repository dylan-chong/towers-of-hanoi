package model

import java.util.*

object Timer {

  private var times = TreeMap<String, Long>()

  fun time(runnable: Runnable, name: String) {
    val start = System.nanoTime()
    runnable.run()
    val end = System.nanoTime()

    addResult(end - start, name)
  }

  fun addResult(nanoseconds: Long, name: String) {
    times[name] = times.getOrDefault(name, 0L) + nanoseconds
  }

  fun printResults() {
    times.forEach { name, time ->
      System.out.printf("%15s: % 9.2f ms\n", name, time / 1.0e6)
    }
  }

  fun reset() {
    times = TreeMap()
  }
}
