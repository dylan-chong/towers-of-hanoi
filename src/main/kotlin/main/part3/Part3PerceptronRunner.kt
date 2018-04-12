package main.part3

import java.util.*

val sharedRandom = Random(1L)

class Part3PerceptronRunner {

  fun run(imageDataFileName: String) {
    val images = Image.loadAll(imageDataFileName)
    val features = (0..50).map { Feature.newRandom(images[0], 4, it == 0) }
    val size = images[0].size

    run(Perceptron(size, features), images)
  }

  tailrec fun run(p: Perceptron, images: List<Image>) {
    if (images.isEmpty()) {
      println("Done")
      return
    }

    val (accuracy, valueResults) = testAccuracy(p, images)
    if (accuracy > 0.95) {
      println("Done")
      return
    }
    run(
      p.train(
        valueResults[0].second,
        images
          .first()
          .category
          .toValue()
      ),
      images.drop(1)
    )
  }

  private fun testAccuracy(
    p: Perceptron,
    images: List<Image>
  ): Pair<Double, List<Pair<Image, ValueResult>>> {
    val valueResults = images.map { it to p.categoryFor(it) }

    val correct = valueResults.count { pair ->
      pair.first.category == pair.second.value.toCategory()
    }

    val percentCorrect = correct.toDouble() / valueResults.size
    println("Correct: $percentCorrect")

    return percentCorrect to valueResults
  }
}

