package main.part1

import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class IrisSet(val instances: List<IrisInstance>, val features: List<Feature>) {

  companion object {

    fun loadFile(path: Path): IrisSet {
      val instances = Files.lines(path)
        .filter { it.isNotEmpty() }
        .map { IrisInstance.fromLine(it) }
        .collect(Collectors.toList())
        .toList()

      return fromInstances(instances)
    }

    fun fromInstances(instances: List<IrisInstance>): IrisSet {
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

  fun calculateClass(instance: IrisInstance, k: Int): IrisInstance.ClassKind {
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

    val groups: MutableMap<IrisInstance.ClassKind, MutableList<IrisInstance.ClassKind>> = sortedInstances
      .stream()
      .map { it.classKind!! }
      .collect(Collectors.groupingBy { it: IrisInstance.ClassKind -> it })
    val sortedGroups: List<Pair<IrisInstance.ClassKind, MutableList<IrisInstance.ClassKind>>> = groups
      .map { Pair(it.key, it.value) }
      .sortedBy { it.second.size }

    return sortedGroups
      .last()
      .second
      .first()
  }
}