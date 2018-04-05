package main.part2

import main.part2.DecisionTreeData.Instance
import kotlin.math.pow

interface ChildFactory {
  fun createFor(tree: DecisionTree): Set<DecisionTree>
}

/**
 * Primitive algorithm. Divides the instances until they are pure or there
 * are no more features left to divde by. In that case, the most common
 * ClassKind is used.
 */
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

/**
 * The proper way you are supposed to do this for the assignment (using
 * the instructions provided in the assignment pdf)
 */
class ProperChildFactory: ChildFactory {

  override fun createFor(tree: DecisionTree): Set<DecisionTree> {
    return when {
      tree.instances.isEmpty() -> emptySet()
      tree.instancesArePure -> emptySet()
      tree.possibleFeatures().isEmpty() -> emptySet()
      else -> bestChildrenByPurity(tree)
    }
  }

  private fun bestChildrenByPurity(tree: DecisionTree): Set<DecisionTree> {
    return tree
      .possibleFeatures()
      .asSequence()
      .map { FeatureValue(it, tree.allData.valuesPerFeature[it]!!.first()) }
      .map { it to childrenFor(it, tree) }
      .sortedBy { purityOfTrees(it.second) }
      .first()
      .second
  }

  private fun purityOfTrees(trees: Set<DecisionTree>): Double {
    if (trees.size > 2) {
      throw AssertionError()
    }

    val totalInstances = trees
      .asSequence()
      .flatMap { it.instances.asSequence() }
      .count()

    return trees
      .map { (it.instances.size.toDouble() / totalInstances) * purityOf(it) }
      .sum()
  }

  private fun purityOf(tree: DecisionTree): Double {
    val groupings = tree.classGroupings.toList()

    if (groupings.size > 2) {
      throw AssertionError()
    }
    if (groupings.size <= 1) {
      return 0.0
    }

    val get = { index: Int -> groupings[index].second.size.toDouble() }
    val m = get(0)
    val n = get(1)

    return (2 * m * n) / (m + n.pow(2))
  }

  private fun childrenFor(
    featureValue: FeatureValue,
    tree: DecisionTree
  ): Set<DecisionTree> {
    val (feature, value) = featureValue

    val (matches, nonMatches) = tree
      .instances
      .partition {
        it.featureNameToValue[feature] == value
      }

    // Assumes that features are binary
    val nonMatchingValue = tree
      .allData
      .valuesPerFeature[feature]!!
      .subtract(setOf(value))
      .first()

    return setOf(
      createTree(featureValue, matches, tree),
      createTree(FeatureValue(feature, nonMatchingValue), nonMatches, tree)
    )
  }

  private fun createTree(
    childFeatureValue: FeatureValue,
    instances: Collection<Instance>,
    parent: DecisionTree
  ): DecisionTree {
    return DecisionTree(
      instances.toSet(),
      parent.allData,
      childFeatureValue,
      parent,
      parent.childFactory
    )
  }
}
