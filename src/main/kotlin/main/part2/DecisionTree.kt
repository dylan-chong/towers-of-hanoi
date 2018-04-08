package main.part2

import main.part2.DecisionTreeData.Instance
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.math.roundToInt

data class DecisionTree(
  val instances: Set<Instance>,
  val allData: DecisionTreeData,
  val featureValue: FeatureValue?,
  val parent: DecisionTree?,
  val childFactory: ChildFactory
) : Classifier {

  val isRoot = parent == null
  val isLeaf: Boolean
    get() = children.isEmpty()
  val depth: Int = if (parent == null) 0 else parent.depth + 1

  init {
    if (isRoot != (featureValue == null && parent == null)) {
      throw IllegalArgumentException()
    }
  }

  companion object {

    fun newRoot(data: DecisionTreeData, childFactory: ChildFactory): DecisionTree {
      return DecisionTree(
        data.instances.toSet(),
        data,
        null,
        null,
        childFactory
      )
    }
  }

  val children by lazy { childFactory.createFor(this) }

  val classGroupings by lazy {
    instances.groupBy { it.classKind }
  }

  val mostCommonClassKind by lazy {
    if (classGroupings.size != 1) {
      println("WARNING: Leaf does not have pure instance class kinds: $this")
    }

    classGroupings
      .toList()
      .sortedBy { it.second.size }
      .last()
      .first
  }

  val instancesArePure by lazy {
    classGroupings.size == 1
  }

  override fun calculateClass(instance: Instance): ClassKind {
    if (isLeaf) {
      return mostCommonClassKind
    }

    val child = children.find {
      val (feature, value) = it.featureValue!!
      instance.featureNameToValue[feature] == value
    }

    !isLeaf || throw AssertionError()
    return if (child == null) {
      println(
        "WARNING: No matching leaf for instance $instance. " +
          "There must not be enough data from training set."
      )
      mostCommonClassKind
    } else {
      child.calculateClass(instance)
    }
  }

  override fun name(): String {
    return "${javaClass.simpleName} with ${childFactory.javaClass.simpleName}"
  }

  override fun representation(): List<String> {
    return listOf(
      localRepresentation().map { indent(it) },
      children.flatMap { it.representation() }
    ).flatMap { it }
  }

  fun possibleFeatures(): Set<Feature> {
    val used = usedFeatures().collect(Collectors.toSet())
    val all = allData.featureNames
    return all - used
  }

  fun usedFeatures(): Stream<Feature> {
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

  private fun localRepresentation(): List<String> {
    if (isRoot) {
      return emptyList()
    }

    val (feature, value) = featureValue!!

    val featureLine = "$feature = $value: /${instances.size}"
    val leafLine by lazy { leafRepresentation() }

    return if (isLeaf) {
      listOf(featureLine, leafLine)
    } else {
      listOf(featureLine)
    }
  }

  private fun leafRepresentation(): String {
    if (!isLeaf) {
      throw IllegalStateException()
    }

    val items = instances.count()
    val probability = instances
      .count { it.classKind == mostCommonClassKind }
      .toDouble()
      .div(items)
      .times(100)
      .roundToInt()

    return indent(
      "Category $mostCommonClassKind, prob = $probability% : /$items",
      1
    )
  }

  private fun indent(line: String, levels: Int = this.depth - 1): String {
    return " ".repeat(levels * 4) + line
  }
}