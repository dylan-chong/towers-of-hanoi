package main.part3

data class Feature(
  val pixelIndexToSigns: Map<Int, Pixel>,
  val isDummy: Boolean = false
) {

  fun valueFor(image: Image): Value {
    if (isDummy) {
      return true
    }

    val matches = pixelIndexToSigns.count { (pixelIndex, pixelValue) ->
      image.get(pixelIndex) == pixelValue
    }

    return matches >= 3
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