package ass1

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

    while (true) {
      val canUseFirst = firstIndex < firstHalf.size
      val canUseSecond = secondIndex < secondHalf.size

      if (canUseFirst && canUseSecond) {
        if (firstHalf[firstIndex] < secondHalf[secondIndex]) {
          result.add(firstHalf[firstIndex])
          firstIndex++
        } else {
          result.add(secondHalf[secondIndex])
          secondIndex++
        }
      } else if (canUseFirst && !canUseSecond) {
        result.add(firstHalf[firstIndex])
        firstIndex++
      } else if (!canUseFirst && canUseSecond) {
        result.add(secondHalf[secondIndex])
        secondIndex++
      } else {
        break
      }
    }

    if (result.size != firstHalf.size + secondHalf.size) {
      throw AssertionError()
    }

    return result
  }

}
