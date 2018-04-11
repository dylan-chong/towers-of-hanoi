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

