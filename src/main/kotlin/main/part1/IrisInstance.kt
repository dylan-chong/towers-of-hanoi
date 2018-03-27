package main.part1

data class IrisInstance(
  val sepalLength: Double,
  val sepalWidth: Double,
  val petalLength: Double,
  val petalWidth: Double,
  val classKind: ClassKind? // null for unassigned yet
) {

  companion object {

    fun fromLine(line: String): IrisInstance {
      val params = line.split(Regex("\\s+"))
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