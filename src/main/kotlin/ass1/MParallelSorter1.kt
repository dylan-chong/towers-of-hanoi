package ass1

import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * This parallel merge sort using futures will be faster than the sequential
 * merge sort on a machine where some other cores/threads are available (they
 * aren't currently being used). This has very little typing overhead in
 * comparison to the sequential merge sort - sort(firstHalf) becomes
 * ForkJoinPool.common().submit(Callable { sort(firstHalf) }) - which is not
 * much extra typing. Using the common pool reduces the overhead of creating
 * new threads, except maybe when starting the program when the pool may not
 * have been used yet. I learned that you can avoid creating your own thread
 * pool by using the common fork join pool (by accidentally discovering it).
 */
class MParallelSorter1(val threshold: Int = 20) : Sorter {

  companion object {
    val pool = Executors.newCachedThreadPool()!!
  }

  override fun <T : Comparable<T>> sort(list: List<T>): List<T> {
    if (list.size < threshold) {
      return ISequentialSorter().sort(list)
    }

    val halfWay = list.size / 2
    val firstHalf = list.subList(0, halfWay)
    val secondHalf = list.subList(halfWay, list.size)

    val firstFuture = pool.submit(Callable { sort(firstHalf) })
    val secondSorted = sort(secondHalf)

    return Merger.merge(firstFuture.get(), secondSorted)
  }

}
