package main.part3

import java.util.*
import java.util.stream.Collectors

//val sharedRandom = Random(1L)
val sharedRandom = Random()

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    doRun(imageDataFileName)
  }

  private fun doRepeatRun(imageDataFileName: String) {
    println(
      "Number of epochs to get 100%: "
        + (0..10000)
        .toList()
        .parallelStream()
        .map { doRun(imageDataFileName) }
        .collect(Collectors.toList())
    )
  }

  private fun doRun(imageDataFileName: String): Int {
    val images = Image.loadAll(imageDataFileName)
    val features = (0..50).map { Feature.newRandom(images[0], 4, it == 0) }

    val imagesToFeatureValues = images.map { image ->
      image to features.map { it.valueFor(image).toDouble() }
    }

    var index = 0
    var epoch = 0
    var p = Perceptron(images[0].size, features)
//    var iterations = { (epoch * images.size) + index }

    while (true) {
      val iterations = epoch * images.size + index

      if (index == images.size) {
        if (epoch == 100000) {
          println("Done")
          return epoch
        } else {
          index = 0
          epoch++
          continue
        }
      }

//      println("Starting iteration for index: $index and epoch: $epoch")

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
      if (!isSkip && accuracy >= 1 && epoch >= 1) {
        println(
          "Done (accuracy: $accuracyPercent is very high) on\t" +
            "iterations: $iterations,\t" +
            "epoch (epoch): $epoch"
        )
        return epoch
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

