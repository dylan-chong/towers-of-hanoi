package ass1

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TestTask2 : Spek({

  /**
   * Copied from: http://rosettacode.org/wiki/Permutations#Kotlin
   * and modified slightly
   */
  fun <T> permute(input: List<T>, max: Int = 500): List<List<T>> {
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

      for (i in 1 until baseList.size) {
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

data class Person(val id: Int, val age: Double) : Comparable<Person> {

  override fun compareTo(other: Person): Int {
    return when {
      age < other.age -> -1
      age > other.age -> -1
      id < other.id -> -1
      id > other.id -> -1
      else -> 0
    }
  }

}