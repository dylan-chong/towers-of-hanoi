package main.part1

import main.part1.IrisInstance.ClassKind
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class KNearestNeighbourClassifier {

  fun run(trainingFile: String, testFile: String, k: Int) {
    val trainingSet = IrisSet.loadFile(File(trainingFile).toPath())
    val testSetTrueAnswers = IrisSet.loadFile(File(testFile).toPath())
    run(trainingSet, testSetTrueAnswers, k)
  }

  fun run(trainingSet: IrisSet, testSetTrueAnswers: IrisSet, k: Int) {
    val testSetCalculatedAnswers = trainingSet.calculateClasses(
      testSetTrueAnswers.withoutClasses(),
      k
    )

    val results = getResults(testSetCalculatedAnswers, testSetTrueAnswers)
    val correct = results.filter { it.isCorrect() }.count()
    val incorrect = results.size - correct
    val percentCorrect = 100 * correct / results.size

    println("Results: $percentCorrect% correct ($correct:$incorrect)")
    results.forEachIndexed { index, result ->
      println(
        "$index. " +
          "isCorrect: ${result.isCorrect()} " +
          "result: ${result.resultInstance} " +
          "actualClassKind: ${result.resultInstance.classKind} "
      )
    }
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

      return fromInstances(instances)
    }

    fun fromInstances(instances: MutableList<IrisInstance>): IrisSet {
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
  fun calculateClasses(testSetUnclassed: IrisSet, k: Int): IrisSet {
    return IrisSet(
      testSetUnclassed.instances.map { it.withClass(calculateClass(it, k)) },
      features
    )
  }

  fun calculateClass(instance: IrisInstance, k: Int): ClassKind {
    if (k <= 0) {
      throw IllegalArgumentException(k.toString())
    }

    val sortedInstances = instances
      .stream()
      .sorted(Comparator.comparing {
        it: IrisInstance -> it.distanceTo(instance, this)
      })
      .limit(k.toLong())
      .collect(Collectors.toList())

    val groups: MutableMap<ClassKind, MutableList<ClassKind>> = sortedInstances
      .stream()
      .map { it.classKind!! }
      .collect(Collectors.groupingBy { it: ClassKind -> it })
    val sortedGroups: List<Pair<ClassKind, MutableList<ClassKind>>> = groups
      .map { Pair(it.key, it.value) }
      .sortedBy { it.second.size }

    return sortedGroups
      .last()
      .second
      .first()
  }
}

data class IrisInstance(
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
