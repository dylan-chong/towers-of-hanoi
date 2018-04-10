package main.part2

interface Classifier {

  fun calculateClass(instance: DecisionTreeData.Instance): ClassKind

  fun name(): String

  fun representation(): List<String>
}

class BaselineClassifier(val data: DecisionTreeData) : Classifier {

  val mostCommonClassKind
    get() = data.mostCommonClassKind

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