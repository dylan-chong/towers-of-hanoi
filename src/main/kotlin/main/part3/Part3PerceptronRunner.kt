package main.part3

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.load(imageDataFileName)
  }

}

data class Feature(val pixelIndexes: List<Int>, val signs: List<Boolean>) {

}
