package main.part3

import java.util.*

val sharedRandom = Random(1L)

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.loadAll(imageDataFileName)
    val features = (0..50).map { Feature.newRandom(images[0], 4, it == 0) }

    val imagesToFeatureValues = images.map { image ->
      image to features.map { it.valueFor(image).toDouble() }
    }

    var index = 0
    var repeat = 0
    var p = Perceptron(images[0].size, features)
//    var iterations = { (repeat * images.size) + index }

    while (true) {
      if (index == images.size) {
        if (repeat == 99) {
          println("Done")
          return
        } else {
          index = 0
          repeat++
          continue
        }
      }

//      println("Starting iteration for index: $index and repeat: $repeat")

      val isSkip = index % 10 != 0
      val cache = if (isSkip) {
        imagesToFeatureValues.subList(index, index + 1)
      } else {
        imagesToFeatureValues
      }

      val (accuracy, valueResultPairs) = testAccuracy(p, cache)
      if (!isSkip) {
        println("- Percent Correct: ${accuracy * 100}")
      }
      if (!isSkip && accuracy > 0.999 && repeat >= 1) {
        println("Done (accuracy is very high)")
        return
      }

      val valueResult = valueResultPairs[if (isSkip) 0 else index].second

      val newP = p.train(
        valueResult,
        images[index]
          .category
          .toValue()
      )

      index++
      p = newP
    }
  }

  private fun testAccuracy(
    p: Perceptron,
    imagesToFeatureValues: List<Pair<Image, List<Double>>>
  ): Pair<Double, List<Pair<Image, ValueResult>>> {
    val valueResults = imagesToFeatureValues.map { (image, featureValues) ->
      image to p.categoryFor(image, featureValues)
    }

    val correct = valueResults
      .parallelStream()
      .filter { pair ->
        pair.first.category == pair.second.value.toCategory()
      }
      .count()

    val decimalCorrect = correct.toDouble() / valueResults.size

    return decimalCorrect to valueResults
  }
}

