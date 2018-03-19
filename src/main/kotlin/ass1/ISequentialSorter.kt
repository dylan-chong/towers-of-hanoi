package ass1

import java.util.*

/*
 * A simple sequential implementation of insertion sort
 */
class ISequentialSorter : Sorter {

  override fun <T : Comparable<T>> sort(list: List<T>): List<T> {
    val result = ArrayList<T>()
    for (l in list) {
      insert(result, l)
    }
    return result
  }

  private fun <T : Comparable<T>> insert(list: MutableList<T>, elem: T) {
    for (i in list.indices) {
      if (list[i] < elem) {
        continue
      }
      list.add(i, elem)
      return
    }
    list.add(elem)
  }

}
