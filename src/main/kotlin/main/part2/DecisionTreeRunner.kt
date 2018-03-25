package main.part2

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class DecisionTreeRunner {

  fun run(trainingFile: String, testFile: String) {
    val trainingData = DecisionTreeData.fromFile(File(trainingFile).toPath())
    val testData = DecisionTreeData.fromFile(File(testFile).toPath())
  }
}

data class DecisionTreeData(
  val classNames: Set<String>,
  val featureNames: Set<String>,
  val instances: List<Instance>
) {

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
    val classKind: String,
    val featureNameToValue: Map<String, String>
  )
}
