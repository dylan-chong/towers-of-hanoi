package main.part3

import java.io.File
import java.util.*

typealias Pixel = Char

data class Image(
  val rows: Int,
  val cols: Int,
  val imageBits: List<Pixel>,
  val category: Char
) {

  val size
    get() = rows * cols

  fun get(pixel: Int): Pixel {
    return imageBits[pixel]
  }

// TODO Implement this when needed
//  fun get(row: Int, col: Int) {
//
//  }

  companion object {

    fun loadAll(filename: String): List<Image> {
      val scanner = Scanner(File(filename))
      val images = mutableListOf<Image>()

      while (scanner.hasNext()) {
        val p1 = scanner.nextLine()// scan past p1
        if (p1 != "P1") {
          throw IllegalArgumentException()
        }

        val commentLine = scanner.nextLine()
        val category = commentLine[1]
        if (!commentLine.startsWith("#")
          || !listOf('X', 'O').contains(category)) {
          throw IllegalArgumentException()
        }

        val rows = scanner.nextInt()
        val cols = scanner.nextInt()
        val size = rows * cols

        var imageBits = ""
        while (imageBits.length < size && scanner.hasNext()) {
          imageBits += scanner.nextLine()
        }

        if (imageBits.length != size) {
          throw IllegalArgumentException()
        }

        val imageBitsList = imageBits.toList()
        if (!imageBitsList.all { "01".toList().contains(it) }) {
          throw IllegalArgumentException(imageBitsList.toString())
        }

        images.add(Image(rows, cols, imageBitsList, category))
      }

      return images
    }
  }
}
