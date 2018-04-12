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
  }

  fun categoryFor(image: Image): ValueResult {
    if (image.size != imageSize) {
      throw IllegalArgumentException(image.toString())
    }

    val featureValues = (weights zip features).map { (weight, feature) ->
      // The value for method could be optimised
      weight * feature.valueFor(image)
    }

    return ValueResult(featureValues.sum() > 0, featureValues)
  }

  fun train(valueResult: ValueResult, predictedValue: Value): Perceptron {
    return Perceptron(
      imageSize,
      features,
      weights.mapIndexed { index, weight ->
        val diff = valueResult.value.toDouble() - predictedValue.toDouble()
        weight + (learningRate * diff * valueResult.featureValues[index])
      }
    )
  }
}