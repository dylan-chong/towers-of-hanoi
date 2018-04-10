package main.part2

import java.io.File
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

  val mostCommonClassKind by lazy {
    instances
      .groupBy { it.classKind }
      .toList()
      .sortedBy { it.second.size }
      .last()
      .first
  }

  init {
    (classNames.isNotEmpty() && instances.isNotEmpty()) || throwInvalid()

    instances.forEach {
      classNames.contains(it.classKind) || throwInvalid()

      it.featureNameToValue.keys == featureNames || throwInvalid()
    }
  }

  fun isCompatibleWith(other: DecisionTreeData): Boolean {
    return classNames == other.classNames
      && featureNames == other.featureNames
  }

  private fun throwInvalid(): Nothing {
    throw IllegalArgumentException(toString())
  }

  companion object {

    fun fromFile(path: String): DecisionTreeData {
      return fromFile(File(path).toPath())
    }

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