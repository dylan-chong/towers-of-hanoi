package ass1

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestTask2Extras {

  @Test
  fun mSequentialSorter_sortsFor_emptyList() {
    Assertions.assertEquals(listOf<Int>(), MSequentialSorter().sort(listOf<Int>()))
  }

  @Test
  fun mSequentialSorter_sortsFor_oneItem() {
    Assertions.assertEquals(listOf(5), MSequentialSorter().sort(listOf(5)))
  }

  @Test
  fun mSequentialSorter_sortsFor_twoItems() {
    Assertions.assertEquals(listOf(5, 7), MSequentialSorter().sort(listOf(7, 5)))
  }

  @Test
  fun mSequentialSorter_sortsFor_3Items() {
    Assertions.assertEquals(listOf(1, 5, 7), MSequentialSorter().sort(listOf(7, 1, 5)))
  }

  @Test
  fun mParallelSorter_doesnt_deadlock() {
    MParallelSorter1().sort((1..100000).toList())
  }
}