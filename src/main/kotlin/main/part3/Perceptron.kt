package main.part3

typealias Value = Boolean

fun Value.toDouble(): Double {
  return if (this) 1.0 else 0.0
}

fun Value.toCategory(): Char {
  return if (this) 'X' else 'O'
}

fun Char.toValue(): Value {
  return when (this) {
    'X' -> true
    'O' -> false
    else -> throw IllegalArgumentException(this.toString())
  }
}

data class ValueResult(val value: Value, val featureValues: List<Double>)

class Perceptron(
  val imageSize: Int,
  val features: List<Feature>,
  val weights: List<Double> = (0 until features.size)
    .map { 0.01 * (sharedRandom.nextDouble() - 0.5) },
  val learningRate: Double = 0.2
) {

  init {
    if (features.size != weights.size) {
      throw IllegalArgumentException(toString())
    }

//    println("- Weights: " + weights.toString())
  }

  fun categoryFor(image: Image): ValueResult {
    if (image.size != imageSize) {
      throw IllegalArgumentException(image.toString())
    }

    // TODO cache
    val featureValues = features.map { it.valueFor(image).toDouble() }
    // TODO dup
    val featureValuesWithWeights = weights.mapIndexed { index, weight ->
      weight * featureValues[index]
    }

    return ValueResult(featureValuesWithWeights.sum() > 0, featureValues)
  }

  fun train(valueResult: ValueResult, expectedValue: Value): Perceptron {
    if (valueResult.value == expectedValue) {
      return this
    }

    return Perceptron(
      imageSize,
      features,
      weights.mapIndexed { index, weight ->
        val actualValue = valueResult.featureValues[index]
        val diff = expectedValue.toDouble() - valueResult.value.toDouble()
        val change = learningRate * diff * actualValue
        weight + change
      }
    )
  }
}