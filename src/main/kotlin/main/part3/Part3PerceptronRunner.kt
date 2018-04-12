package main.part3

import java.util.*

val sharedRandom = Random(1L)

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.loadAll(imageDataFileName)
    val features = (0..50).map { Feature.newRandom(images[0], 4, it == 0) }
    val size = images[0].size

    var index = 0
    var repeat = 0
    var p = Perceptron(size, features)

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

      val (accuracy, valueResultPairs) = testAccuracy(p, images)
      if (accuracy > 0.999) {
        println("Done (accuracy is very high)")
        return
      }

      val newP = p.train(
        valueResultPairs[index].second,
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
    images: List<Image>
  ): Pair<Double, List<Pair<Image, ValueResult>>> {
    val valueResults = images.map { it to p.categoryFor(it) }

    val correct = valueResults
      .parallelStream()
      .filter { pair ->
        pair.first.category == pair.second.value.toCategory()
      }
      .count()

    val percentCorrect = correct.toDouble() / valueResults.size
    println("- percentCorrect: $percentCorrect")

    return percentCorrect to valueResults
  }
}

