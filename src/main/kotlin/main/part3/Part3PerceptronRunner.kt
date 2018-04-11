package main.part3

import java.util.*

val sharedRandom = Random(1L)

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.load(imageDataFileName)
    val features = (1..50).map { Feature.newRandoms(images[0], 4) }
    println()
  }

}

data class Feature(val pixelIndexToSigns: Map<Int, Boolean>) {

  companion object {

    fun newRandoms(image: Image, pixels: Int): Any {
      return Feature(
        (0 until image.size)
          .shuffled(sharedRandom)
          .take(pixels)
          .map { it to sharedRandom.nextBoolean() }
          .toMap()
      )
    }
  }
}
