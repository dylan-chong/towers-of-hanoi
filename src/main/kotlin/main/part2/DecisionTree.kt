package main.part2

import java.util.stream.Collectors
import java.util.stream.Stream

data class DecisionTree private constructor(
  val instances: Set<DecisionTreeData.Instance>,
  val allData: DecisionTreeData,
  val featureValue: FeatureValue?,
  val parent: DecisionTree?
) {

  val children: Set<DecisionTree>
  val isRoot: Boolean
    get() = parent == null
  val isLeaf: Boolean
    get() = children.isEmpty()
  val depth: Int = if (parent == null) 0 else parent.depth + 1

  init {
    if (instances.isEmpty()) {
       throw IllegalArgumentException()
    }
    if (isRoot != (featureValue == null && parent == null)) {
      throw IllegalArgumentException()
    }

    children = createChildren()
  }

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

  fun calculateClass(instance: DecisionTreeData.Instance): ClassKind {
    if (isLeaf) {
      return mostCommonClassKind
    }

    return children
      .find { it.featureValue!! == it.featureValue }!!
      .calculateClass(instance)
  }

  fun representation(): List<String> {
    return listOf(
      localRepresentation().map { indent(it) },
      children.flatMap { it.representation() }
    ).flatMap { it }
  }

  fun localRepresentation(): List<String> {
    if (isRoot) {
      return emptyList()
    }

    val featureLine = "${featureValue!!.feature} = ${featureValue.value}:"
    val leafLine by lazy {
      val probability = instances.count { it.classKind == mostCommonClassKind }
      val items = instances.count()
      indent(
        "Category $mostCommonClassKind, prob = $probability% : /$items",
        1
      )
    }

    return if (isLeaf) {
      listOf(featureLine, leafLine)
    } else {
      listOf(featureLine)
    }
  }

  private fun indent(line: String, levels: Int = this.depth - 1): String {
    return " ".repeat(levels * 4) + line
  }

  private val mostCommonClassKind by lazy {
    if (!isLeaf) {
      throw IllegalStateException()
    }

    val classGroupings = instances
      .groupBy { it.classKind }
      .toList()

    if (classGroupings.size != 1) {
      println("WARNING: Leaf does not have pure instance class kinds: $this")
    }

    classGroupings
      .sortedBy { it.second.size }
      .last()
      .first
  }

  private fun createChildren(): Set<DecisionTree> {
    val possibleFeatures = possibleFeatures()
    if (possibleFeatures.isEmpty()) {
      return emptySet()
    }
    val childFeature = possibleFeatures.first()

    return allData
      .valuesPerFeature[childFeature]!!
      .mapNotNull { createChild(childFeature, it) }
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
    if (isRoot) {
      return Stream.empty()
    }

    return Stream
      .of(
        parent!!.usedFeatures(),
        Stream.of(featureValue!!.feature)
      )
      .flatMap { it }
  }

}