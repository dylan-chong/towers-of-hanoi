package main.part3

import java.util.*

//val sharedRandom = Random(1L)
val sharedRandom = Random()

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.loadAll(imageDataFileName)
    val features = (0..50).map { Feature.newRandom(images[0], 4, it == 0) }

    val imagesToFeatureValues = images.map { image ->
      image to features.map { it.valueFor(image).toDouble() }
    }

    var index = 0
    var epoch = 0
    val iterations = { epoch * images.size + index }
    var p = Perceptron(images[0].size, features)
    var lastAccuracy = -1.0

    while (true) {
      if (index == images.size) {
        if (epoch == 100000) {
          println("Done (reached maximum epochs $epoch)")
          break
        } else {
          index = 0
          epoch++
          continue
        }
      }

//      println("Starting iteration $iterations for index: $index and epoch: $epoch")

      val skipAccuracy = index % 10 != 0
      val cache = if (skipAccuracy) {
        imagesToFeatureValues.subList(index, index + 1)
      } else {
        imagesToFeatureValues
      }

      val (accuracy, valueResultPairs) = testAccuracy(p, cache)
      lastAccuracy = accuracy
      if (!skipAccuracy) {
//        println(accuracyPercent * 100)
      }
      if (!skipAccuracy && accuracy >= 1 && epoch >= 1) {
        println("Done (accuracy: is very high)")
        break
      }

      val valueResult = valueResultPairs[if (skipAccuracy) 0 else index].second

      val newP = p.train(
        valueResult,
        images[index]
          .category
          .toValue()
      )

      index++
      p = newP
    }

    println("- accuracy: ${lastAccuracy * 100}")
    println("- repeats (epoch): $epoch")
    println("- iterations: ${iterations()} (number of times trained on a single instance)")

    println("- Features (pixelIndexToSigns is a map from 1d position to ideal")
    println("  pixel value. Position is 1d rather than 2d for simplicity)")
    features.forEachIndexed { i, it -> println("  - $i. $it") }

    println("- Weights:")
    p.weights.forEachIndexed { i, it -> println("  - $i. ${"% 4.2f".format(it)}") }
  }

  private fun testAccuracy(
    p: Perceptron,
    imagesToFeatureValues: List<Pair<Image, List<Double>>>
  ): Pair<Double, List<Pair<Image, ValueResult>>> {
    val valueResults = imagesToFeatureValues.map { (image, featureValues) ->
      image to p.categoryFor(image, featureValues)
    }

    val correct = valueResults.count { pair ->
      pair.first.category == pair.second.value.toCategory()
    }

    val decimalCorrect = correct.toDouble() / valueResults.size

    return decimalCorrect to valueResults
  }
}

