package main.part1

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