package ass1

import ass1.PersonDataset.baseList
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestTask2 : Spek({

  /**
   * Copied from: http://rosettacode.org/wiki/Permutations#Kotlin
   * and modified slightly
   */
  fun <T> permute(input: List<T>, max: Long = 200): List<List<T>> {
    if (input.size <= 1) return listOf(input)
    val perms = mutableListOf<List<T>>()
    val toInsert = input[0]
    for (perm in permute(input.drop(1))) {
      for (i in 0..perm.size) {
        if (perms.size >= max) {
          return perms
        }
        val newPerm = perm.toMutableList()
        newPerm.add(i, toInsert)
        perms.add(newPerm)
      }
    }
    return perms
  }

  listOf(
    ISequentialSorter(),
    MSequentialSorter(),
    MParallelSorter1(),
    MParallelSorter2()
  ).forEach { sorter ->
    describe("sorter ${sorter.javaClass.simpleName}") {

      it("should sort an empty list") {
        assert(sorter.sort(listOf<Int>()) == listOf<Int>())
      }

      for (i in 1..baseList.size) {
        val subList = baseList.subList(0, i)

        context("given a list of ${subList.size} items") {

          permute(subList).forEachIndexed { i, permutedList ->
            context("when given permutation $i") {
              it("should sort correctly") {
                val expected = permutedList.sorted()
                val result = sorter.sort(permutedList)
                assert(result == expected)
              }
            }
          }
        }
      }
    }
  }
})

object PersonDataset {

  val baseList = listOf(
    Person( 1, 0.0),
    Person( 2, 2.01),
    Person( 3, -3.0),
    Person( 4, -1.0),
    Person( 5, -1.0),
    Person( 6, 0.0),
    Person( 7, -5.0),
    Person( 8, -1.0),
    Person( 9, 77e-45),
    Person(10, 78e-45),
    Person(11, 78e-45),
    Person(12, 23e98),
    Person(13, 23.0001e98)
  )
}

class TestTask2Extras {

  @Test
  fun mSequentialSorter_sortsFor_emptyList() {
    assertEquals(listOf<Int>(), MSequentialSorter().sort(listOf<Int>()))
  }

  @Test
  fun mSequentialSorter_sortsFor_oneItem() {
    assertEquals(listOf(5), MSequentialSorter().sort(listOf(5)))
  }

  @Test
  fun mSequentialSorter_sortsFor_twoItems() {
    assertEquals(listOf(5, 7), MSequentialSorter().sort(listOf(7, 5)))
  }

  @Test
  fun mSequentialSorter_sortsFor_3Items() {
    assertEquals(listOf(1, 5, 7), MSequentialSorter().sort(listOf(7, 1, 5)))
  }

  @Test
  fun mParallelSorter_doesnt_deadlock() {
    MParallelSorter1().sort((1..100000).toList())
  }
}
