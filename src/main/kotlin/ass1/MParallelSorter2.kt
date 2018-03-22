package ass1

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask

/**
 * To fork join pool uses a work stealing algorithm so there will be less
 * overhead creating lots of new threads if threads get blocked. This algorithm
 * is almost identical to the first parallel sort algorithm, except for the
 * work stealing that is used by the fork join pool. I learned that i probably
 * should not have used the common pool for the first merge sort algorithm, in
 * case one part of the program submits a lot of work to be done and another
 * part of the program submits a tiny bit of work - the tiny bit of work may
 * not be done for a long time (i believe this is called starvation).
 */
class MParallelSorter2(val threshold: Int = 20) : Sorter {

  override fun <T : Comparable<T>> sort(list: List<T>): List<T> {
    return ForkJoinPool
      .commonPool()
      .submit(SortTask(list))
      .invoke()
  }

  inner class SortTask<T : Comparable<T>>(private val list: List<T>)
    : RecursiveTask<List<T>>() {

    override fun compute(): List<T> {
      if (list.size < threshold) {
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
