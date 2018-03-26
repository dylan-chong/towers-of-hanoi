package main.part2

import java.util.stream.Collectors
import java.util.stream.Stream

class DecisionTree(
  val instances: Set<DecisionTreeData.Instance>,
  val topData: DecisionTreeData,
  val featureValue: FeatureValue,
  val parent: DecisionTree?
) {

  companion object {

    fun newRoot(data: DecisionTreeData): DecisionTree {
      return DecisionTree(
        data.instances.toSet(),
        data,
        data.possibleFeatureValues.first(),
        null
      )
    }
  }

  val feature
    get() = featureValue.feature
  val value
    get() = featureValue.value

  init {
    instances.isNotEmpty() || throw IllegalArgumentException()
  }

  fun split(): List<DecisionTree> {
    val childFeature = pickChildFeature()
    return topData
      .valuesPerFeature[childFeature]!!
      .map { possibleValue ->
        val filteredInstances = instances.filter {
          it.featureNameToValue[childFeature] == possibleValue
        }

        DecisionTree(
          filteredInstances.toSet(),
          topData,
          FeatureValue(childFeature, possibleValue),
          this
        )
      }
      .filter { it.instances.isNotEmpty() }
  }

  private fun pickChildFeature(): Feature {
    val possibleFeatures = possibleFeatures()
    if (possibleFeatures.isEmpty()) {
      throw IllegalStateException()
    }
    return possibleFeatures.first()
  }

  private fun possibleFeatures(): Set<Feature> {
    val used = usedFeatures().collect(Collectors.toSet())
    val all = topData.featureNames
    return all - used
  }

  private fun usedFeatures(): Stream<Feature> {
    return if (parent == null) {
      Stream.of(feature)
    } else {
      Stream.of(
        parent.usedFeatures(),
        Stream.of(feature)
      )
        .flatMap { it }
    }
  }
}