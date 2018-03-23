package main.part1

import main.part1.IrisInstance.ClassKind
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class KNearestNeighbourClassifier {

  fun run(trainingFile: String, testFile: String) {
    val trainingSet = IrisSet.loadFile(File(trainingFile).toPath())

    val testSetTrueAnswers = IrisSet.loadFile(File(testFile).toPath())
    val testSetCalculatedAnswers =
      trainingSet.calculateClasses(testSetTrueAnswers.withoutClasses())

    val results = getResults(testSetCalculatedAnswers, testSetTrueAnswers)
    val correct = results.filter { it.isCorrect() }.count()
    val incorrect = results.size - correct

    print("Results: correct: $correct, incorrect: $incorrect")
  }

  private fun getResults(
    testSetCalculatedAnswers: IrisSet,
    testSetTrueAnswers: IrisSet
  ): List<Result> {
    val calculatedInstances = testSetCalculatedAnswers.instances
    val trueInstances = testSetTrueAnswers.instances
    return (calculatedInstances zip trueInstances)
      .map { Result(it.first, it.second) }
  }

  class Result(
    val resultInstance: IrisInstance,
    val correctInstance: IrisInstance
  ) {
    init {
      if (!resultInstance.equalsIgnoreClass(correctInstance)) {
        throw IllegalArgumentException(this.toString())
      }
    }

    fun isCorrect(): Boolean {
      return resultInstance.classKind == correctInstance.classKind
    }
  }
}

class IrisSet(val instances: List<IrisInstance>, val features: List<Feature>) {

  companion object {

    fun loadFile(path: Path): IrisSet {
      val instances = Files
        .lines(path)
        .filter { it.isNotEmpty() }
        .map { IrisInstance.fromLine(it) }
        .collect(Collectors.toList())

      val features = listOf(
        Feature({ it.sepalLength }, instances),
        Feature({ it.sepalWidth }, instances),
        Feature({ it.petalLength }, instances),
        Feature({ it.petalWidth }, instances)
      )

      return IrisSet(instances, features)
    }
  }

  fun withoutClasses(): IrisSet {
    return IrisSet(
      instances.map { it.withClass(null) },
      features
    )
  }

  /**
   * Call this if this is the training set
   */
  fun calculateClasses(testSetUnclassed: IrisSet): IrisSet {
    return IrisSet(
      testSetUnclassed.instances.map { it.withClass(calculateClass(it)) },
      features
    )
  }

  fun calculateClass(instance: IrisInstance): ClassKind {
    return ClassKind.SETOSA//todo
  }
}

class IrisInstance(
  val sepalLength: Double,
  val sepalWidth: Double,
  val petalLength: Double,
  val petalWidth: Double,
  val classKind: ClassKind? // null for unassigned yet
) {

  companion object {

    fun fromLine(line: String): IrisInstance {
      var params = line.split(Regex("\\s+"))
      return IrisInstance(
        params[0].toDouble(),
        params[1].toDouble(),
        params[2].toDouble(),
        params[3].toDouble(),
        ClassKind.valueOf(params[4].split("-")[1].toUpperCase())
      )
    }
  }

  enum class ClassKind {
    SETOSA,
    VERSICOLOR,
    VIRGINICA
  }

  fun distanceTo(other: IrisInstance, set: IrisSet): Double {
    return Math.sqrt(
      set.features
        .stream()
        .mapToDouble {
          val thisValue = it.getter(this)
          val otherValue = it.getter(other)

          Math.pow(otherValue - thisValue, 2.0) / Math.pow(it.range, 2.0)
        }
        .sum()
    )
  }

  fun withClass(kind: ClassKind?): IrisInstance {
    return IrisInstance(sepalLength, sepalWidth, petalLength, petalWidth, kind)
  }

  fun equalsIgnoreClass(other: IrisInstance): Boolean {
    return sepalLength == other.sepalLength &&
      sepalWidth == other.sepalWidth &&
      petalLength == other.petalLength &&
      petalWidth == other.petalWidth
  }

}

class Feature(
  val getter: (IrisInstance) -> Double,
  instances: List<IrisInstance>
) {

  val range: Double

  init {
    val values = instances.map { getter(it) }
    val min = values.min()!!
    val max = values.max()!!
    range = max - min
  }
}
