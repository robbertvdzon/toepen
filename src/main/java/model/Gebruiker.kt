package model

data class Gebruiker(
  val id: String = "",
  val naam: String = "",
  val wilMeedoen: Boolean = false, // bij nieuwe tafel indeling, deze speler mee laten doen
  val isMonkey: Boolean = false,
  val score: Int = 0
)
