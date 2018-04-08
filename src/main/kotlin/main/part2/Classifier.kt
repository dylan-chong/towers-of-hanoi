package main.part2

interface Classifier {

  fun calculateClass(instance: DecisionTreeData.Instance): ClassKind

  fun name(): String

  fun representation(): List<String>
}

class BaselineClassifier(val data: DecisionTreeData) : Classifier {

  val mostCommonClassKind by lazy {
    data
      .instances
      .groupBy { it.classKind }
      .toList()
      .asSequence()
      .sortedBy { it.second.size }
      .last()
      .first
  }

  override fun calculateClass(instance: DecisionTreeData.Instance): ClassKind {
    return mostCommonClassKind
  }

  override fun name(): String {
    return javaClass.simpleName
  }

  override fun representation(): List<String> {
    return listOf("mostCommonClassKind: $mostCommonClassKind")
  }

}