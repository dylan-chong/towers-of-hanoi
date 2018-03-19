package ass1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestExtras {

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
}