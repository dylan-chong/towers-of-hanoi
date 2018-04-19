class Sugar
class Wheat
class Cake(val sugar: Sugar, val wheat: Wheat)

val wheats = generateSequence { Wheat() }
val sugars = generateSequence { Sugar() }
val x = (wheats zip sugars)
  .map { Cake(it.second, it.first) }
