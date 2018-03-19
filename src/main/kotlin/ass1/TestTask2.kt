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
  fun <T> permute(input: List<T>, max: Int = 400): List<List<T>> {
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

      listOf(
        listOf(1.5),
        listOf(1.5, 0.9),
        listOf(1.5, 0.999, 0.9999),
        listOf(1.5, -234.0, 0.999, 0.9999),
        listOf(123123123123.0, 1.5, -234.0, 0.999, 0.9999),
        listOf(123123123123.0, 1.5, 234.0, -234.0, 0.999, 0.9999),
        listOf(
          123123123123.0, 1.5, 234.0, -234.0, 0.999, 0.9999, 1.0, 1.0, 1.0,
          -3.0, 1.0, 1.0
        ),
        listOf(
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238
        ),
        listOf(
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238,
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238
        ),
        listOf(
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238,
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238,
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238,
          123123123123.0, 8.0, 8.9999999, 3.23e45, 1.5, 234.0, -234.0, 0.999,
          0.9999, 1.0, 1.0, 1.0, -3.0, 1.0, 1.0, 234.134, 98.2378, 8923823.238
        )
      ).forEach { inputList ->
        context("given a list of ${inputList.size} items") {

          permute(inputList).forEachIndexed { i, permutedList ->
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