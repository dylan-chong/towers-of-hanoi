package main.part2

import java.nio.file.Files
import java.nio.file.Path

data class DecisionTreeData(
  val classNames: Set<ClassKind>,
  val featureNames: Set<Feature>,
  val instances: List<Instance>
) {

  val valuesPerFeature: Map<Feature, Set<Value>> by lazy {
    featureNames
      .map { feature ->
        Pair(
          feature,
          instances
            .map { it.featureNameToValue[feature]!! }
            .toSet()
        )
      }
      .toMap()
  }
  val possibleFeatureValues: Set<FeatureValue> by lazy {
    valuesPerFeature
      .flatMap { entry ->
        entry.value.map { value -> FeatureValue(entry.key, value) }
      }
      .toSet()
  }

  init {
    (classNames.isNotEmpty() && instances.isNotEmpty()) || throwInvalid()

    instances.forEach {
      classNames.contains(it.classKind) || throwInvalid()

      it.featureNameToValue.keys == featureNames || throwInvalid()
    }
  }

  private fun throwInvalid(): Nothing {
    throw IllegalArgumentException(toString())
  }

  companion object {

    fun fromFile(path: Path): DecisionTreeData {
      val lines = Files.readAllLines(path)

      fun elements(index: Int): List<String> {
        return lines[index]
          .split(Regex("\\s+"))
          .filter { it.isNotEmpty() }
          .toList()
      }

      val classNames = elements(0).toSet()
      val featureNames = elements(1)

      val instances = (2 until lines.size)
        .map {
          val list = elements(it)
          val featureMap = (featureNames zip list.drop(1)).toMap()
          Instance(list[0], featureMap)
        }

      return DecisionTreeData(classNames, featureNames.toSet(), instances)
    }
  }

  data class Instance(
    val classKind: ClassKind,
    val featureNameToValue: Map<Feature, Value>
  )
}