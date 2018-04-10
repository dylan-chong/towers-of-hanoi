package main.part2

class ResultsPrinter {

  fun printResults(
    title: String,
    representation: String,
    results: List<Pair<DecisionTreeData.Instance, ClassKind>>
  ) {
    println("# Results for $title")
    println("Classifier representation:\n$representation")

    val correct = results.count(::isCorrect)
    val incorrect = results.size - correct
    val percentCorrect = 100 * correct / results.size

    println("Results: $percentCorrect% correct ($correct:$incorrect)")
    printLiveDieTotals(results)
    println()

    println("## Results per instance:")
    results.forEachIndexed { index, result ->
      println(
        "$index. " +
          "isCorrect: ${isCorrect(result)}, " +
          "result: ${result.first}, " +
          "actualClassKind: ${result.second} "
      )
    }

    println()
  }

  private fun printLiveDieTotals(results: List<Pair<DecisionTreeData.Instance, ClassKind>>) {
    results
      .map { it.second }
      .toSet()
      .forEach { classKind: ClassKind ->
        val matchingResults = results.filter { it.first.classKind == classKind }
        val correct = matchingResults.count { isCorrect(it) }
        val total = matchingResults.count()
        println("$classKind: $correct correct out of $total")
      }
  }

  private fun isCorrect(pair: Pair<DecisionTreeData.Instance, ClassKind>): Boolean {
    return pair.first.classKind == pair.second
  }
}