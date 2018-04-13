package model

import java.util.*
import java.util.function.Consumer

object Timer {

  private val times = TreeMap<String, Long>()

  fun time(runnable: Runnable, name: String) {
    val start = System.nanoTime()
    runnable.run()
    val end = System.nanoTime()

    addResult(end - start, name)
  }

  fun time(runnable: Consumer<TimerController>) {
    runnable.accept(TimerController())
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
    times.clear()
  }

  class TimerController {

    private var currentName: String? = null
    private var startTime: Long = 0

    fun start(name: String) {
      currentName = name
      startTime = System.nanoTime()
    }

    fun stop() {
      val endTime = System.nanoTime()
      addResult(endTime - startTime, currentName!!)
    }
  }
}
