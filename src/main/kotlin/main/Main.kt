package main

import gui.Gui
import java.util.*

fun main(args: Array<String>) {
  Gui.main(args)
}

fun avgTime(queue: Queue<Long>): Double {
  return queue.average()
}
