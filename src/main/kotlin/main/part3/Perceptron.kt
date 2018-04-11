package main.part3

typealias Value = Boolean

fun Value.toDouble(): Double {
  return if (this) 1.0 else 0.0
}

data class ValueResult(val value: Value, val featureValues: List<Double>)

class Perceptron(
  val size: Int,
  val features: List<Feature>,
  val weights: List<Double> = (0 until size)
    .map { 0.01 * (sharedRandom.nextDouble() - 0.5) },
  val learningRate: Double = 0.2
) {

  init {
    if (!listOf(4, size, features.size, weights.size).all { it == size }) {
      throw IllegalArgumentException(toString())
    }
  }

  fun valueFor(image: Image): ValueResult {
    if (image.size != size) {
      throw IllegalArgumentException(image.toString())
    }

    val featureValues = (0 until size).map {
      // The value for method could be optimised
      weights[it] * features[it].valueFor(image)
    }

    return ValueResult(featureValues.sum() > 0, featureValues)
  }

  fun train(valueResult: ValueResult, predictedValue: Value): Perceptron {
    return Perceptron(
      size,
      features,
      weights.mapIndexed { index, weight ->
        val diff = valueResult.value.toDouble() - predictedValue.toDouble()
        weight + (learningRate * diff * valueResult.featureValues[index])
      }
    )
  }
}