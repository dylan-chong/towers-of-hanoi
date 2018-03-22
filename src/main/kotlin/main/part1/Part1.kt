package main.part1

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class KNearestNeighbourClassifier {

  fun run(trainingFile: String, testFile: String) {
    val trainingInstances = IrisInstance.loadFile(File(trainingFile).toPath())
    val testInstances = IrisInstance.loadFile(File(testFile).toPath())
  }
}

class IrisInstance(
  val sepalLength: Float,
  val sepalWidth: Float,
  val petalLength: Float,
  val petalWidth: Float,
  val classKind: ClassKind
) {

  companion object {

    fun loadFile(path: Path): List<IrisInstance> {
      return Files
        .lines(path)
        .filter { it.isNotEmpty() }
        .map { fromLine(it) }
        .collect(Collectors.toList())
    }

    fun fromLine(line: String): IrisInstance {
      var params = line.split(Regex("\\s+"))
      return IrisInstance(
        params[0].toFloat(),
        params[1].toFloat(),
        params[2].toFloat(),
        params[3].toFloat(),
        ClassKind.valueOf(params[4].split("-")[1].toUpperCase())
      )
    }
  }

  enum class ClassKind {
    SETOSA,
    VERSICOLOR,
    VIRGINICA
  }
}