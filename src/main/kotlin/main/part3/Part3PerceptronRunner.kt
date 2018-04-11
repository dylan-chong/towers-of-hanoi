package main.part3

import java.util.*

val sharedRandom = Random(1L)

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.loadAll(imageDataFileName)
    val features = (0..50).map { Feature.newRandom(images[0], 4, it == 0) }
    println()
  }

}

data class Feature(
  val pixelIndexToSigns: Map<Int, Pixel>,
  val isDummy: Boolean = false
) {

  fun valueFor(image: Image): Int {
    if (isDummy) {
      return 1
    }

    val matches = pixelIndexToSigns.count { (pixelIndex, pixelValue) ->
      image.get(pixelIndex) == pixelValue
    }

    return if (matches >= 3) 1 else 0
  }

  companion object {

    fun newRandom(image: Image, pixels: Int, isDummy: Boolean): Feature {
      return Feature(
        (0 until image.size)
          .shuffled(sharedRandom)
          .take(pixels)
          .map { it to (if (sharedRandom.nextBoolean()) '1' else '0') }
          .toMap(),
        isDummy
      )
    }
  }
}
