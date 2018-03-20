package ass1

data class Person(val id: Int, val age: Double) : Comparable<Person> {

  override fun compareTo(other: Person): Int {
    return when {
      age < other.age -> -1
      age > other.age -> 1
      id < other.id -> -1
      id > other.id -> 1
      else -> 0
    }
  }
}