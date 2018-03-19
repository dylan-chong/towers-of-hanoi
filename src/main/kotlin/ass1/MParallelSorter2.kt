package ass1

import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MParallelSorter2 : Sorter {


    companion object {
        const val THRESHOLD = 20

        val pool = Executors.newCachedThreadPool()!!
    }

    override fun <T : Comparable<T>> sort(list: List<T>): List<T> {
        if (list.size < THRESHOLD) {
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
