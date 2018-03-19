package ass1

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask

class MParallelSorter2 : Sorter {

  companion object {
    const val THRESHOLD = 20

    val pool = ForkJoinPool()
  }

  override fun <T : Comparable<T>> sort(list: List<T>): List<T> {
    return pool.submit(SortTask(list)).invoke()
  }

  class SortTask<T : Comparable<T>>(private val list: List<T>)
    : RecursiveTask<List<T>>() {

    override fun compute(): List<T> {
      if (list.size < THRESHOLD) {
        return ISequentialSorter().sort(list)
      }

      val halfWay = list.size / 2
      val firstHalf = list.subList(0, halfWay)
      val secondHalf = list.subList(halfWay, list.size)

      val firstFuture = SortTask(firstHalf)
      firstFuture.fork()
      val secondSorted = SortTask(secondHalf).compute()

      return Merger.merge(firstFuture.join(), secondSorted)
    }

  }
}
