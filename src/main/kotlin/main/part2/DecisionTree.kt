package main.part2

import java.util.stream.Collectors
import java.util.stream.Stream

class DecisionTree private constructor(
  val instances: Set<DecisionTreeData.Instance>,
  val allData: DecisionTreeData,
  val featureValue: FeatureValue?,
  val parent: DecisionTree?
) {

  companion object {

    fun newRoot(data: DecisionTreeData): DecisionTree {
      return DecisionTree(
        data.instances.toSet(),
        data,
        null,
        null
      )
    }
  }

  val children: Set<DecisionTree>

  init {
    if (instances.isEmpty()) {
       throw IllegalArgumentException()
    }
    children = createChildren()
  }

  private fun createChildren(): Set<DecisionTree> {
    val possibleFeatures = possibleFeatures()
    if (possibleFeatures.isEmpty()) {
      return emptySet()
    }
    val childFeature = possibleFeatures.first()

    return allData
      .valuesPerFeature[childFeature]!!
      .map { createChild(childFeature, it) }
      .filter { it != null }
      .map { it!! } // Hack to get compilation to work
      .toSet()
  }

  private fun createChild(feature: Feature, value: Value): DecisionTree? {
    val filteredInstances = instances.filter {
      it.featureNameToValue[feature] == value
    }

    if (filteredInstances.isEmpty()) {
      return null
    }

    return DecisionTree(
      filteredInstances.toSet(),
      allData,
      FeatureValue(feature, value),
      this
    )
  }

  private fun possibleFeatures(): Set<Feature> {
    val used = usedFeatures().collect(Collectors.toSet())
    val all = allData.featureNames
    return all - used
  }

  private fun usedFeatures(): Stream<Feature> {
    return if (parent == null) {
      Stream.empty()
    } else {
      Stream
        .of(
          parent.usedFeatures(),
          Stream.of(featureValue!!.feature)
        )
        .flatMap { it }
    }
  }
}