package main.part3

import java.util.*

//val sharedRandom = Random(1L)
val sharedRandom = Random()

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    println((0..0).map { doRun(imageDataFileName) })
  }

  private fun doRun(imageDataFileName: String): Int {
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
      val iterations = repeat * images.size + index

      if (index == images.size) {
        if (repeat == 1000000) {
          println("Done")
          return iterations
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
      val accuracyPercent = accuracy * 100
      if (!isSkip) {
//        println(accuracyPercent)
      }
      if (!isSkip && accuracy >= 1 && repeat >= 1) {
        println(
          "Done (accuracy: $accuracyPercent is very high) on " +
            "iterations: $iterations, " +
            "repeat (epoch): $repeat"
        )
        return iterations
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

    val correct = valueResults.count { pair ->
      pair.first.category == pair.second.value.toCategory()
    }

    val decimalCorrect = correct.toDouble() / valueResults.size

    return decimalCorrect to valueResults
  }
}

