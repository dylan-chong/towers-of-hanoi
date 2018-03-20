package ass1

/**
 * This sequential merge sort algorithm will be much faster than the sequential
 * sort for large data. It only uses the current thread so if this is run on a
 * web server where each thread handles a request, the sorting algorithm will
 * not slow down other requests by using other threads. It does not have the
 * overhead of creating extra objects (futures/recursive task) so will be
 * slightly faster if only one core/thread is available on the machine. I
 * learned that there can be easy to make stupid mistakes in the merge function
 * if you try to write the algorithm from memory in the overly concise style in
 * textbooks/some lecture slides from other courses. I had to spend a while
 * debugging these mistakes. In the end are refracted the code to use variable
 * names for the conditions, so that it was easier to understand and get right
 * the first time.
 */
class MSequentialSorter : Sorter {

  override fun <T : Comparable<T>> sort(list: List<T>): List<T> {
    return when (list.size) {
      0, 1 -> {
        list.toMutableList()
      }
      else -> {
        val halfWay = list.size / 2
        val firstHalf = sort(list.subList(0, halfWay))
        val secondHalf = sort(list.subList(halfWay, list.size))

        Merger.merge(firstHalf, secondHalf)
      }
    }
  }
}

object Merger {
  fun <T : Comparable<T>> merge(
    firstHalf: List<T>,
    secondHalf: List<T>
  ): List<T> {
    var firstIndex = 0
    var secondIndex = 0
    val result: MutableList<T> = mutableListOf()

    fun useFirst() {
      result.add(firstHalf[firstIndex])
      firstIndex++
    }
    fun useSecond() {
      result.add(secondHalf[secondIndex])
      secondIndex++
    }

    loop@ while (true) {
      val canUseFirst = firstIndex < firstHalf.size
      val canUseSecond = secondIndex < secondHalf.size

      when {
        canUseFirst && canUseSecond ->
          if (firstHalf[firstIndex] < secondHalf[secondIndex]) {
            useFirst()
          } else {
            useSecond()
          }
        canUseFirst && !canUseSecond -> useFirst()
        !canUseFirst && canUseSecond -> useSecond()
        else -> break@loop
      }
    }

    if (result.size != firstHalf.size + secondHalf.size) {
      throw AssertionError()
    }

    return result
  }

}
