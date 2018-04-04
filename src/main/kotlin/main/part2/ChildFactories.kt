package main.part2

interface ChildFactory {
  fun createFor(tree: DecisionTree): Set<DecisionTree>
}

class SimpleChildFactory: ChildFactory {

  override fun createFor(tree: DecisionTree): Set<DecisionTree> {
    val possibleFeatures = tree.possibleFeatures()
    if (possibleFeatures.isEmpty()) {
      return emptySet()
    }
    val childFeature = possibleFeatures.first()

    return tree
      .allData
      .valuesPerFeature[childFeature]!!
      .mapNotNull { createChild(tree, childFeature, it) }
      .toSet()
  }

  private fun createChild(
    tree: DecisionTree,
    feature: Feature,
    value: Value
  ): DecisionTree? {
    val filteredInstances = tree.instances.filter {
      it.featureNameToValue[feature] == value
    }

    if (filteredInstances.isEmpty()) {
      return null
    }

    return DecisionTree(
      filteredInstances.toSet(),
      tree.allData,
      FeatureValue(feature, value),
      tree,
      tree.childFactory
    )
  }

}
