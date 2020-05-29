package model

data class Gebruiker(
  var id: String = "",
  var naam: String = "",
  var wilMeedoen: Boolean = false, // bij nieuwe tafel indeling, deze speler mee laten doen
  var isMonkey: Boolean = false,
  var score: Int = 0
)
