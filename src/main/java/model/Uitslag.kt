package model

data class Uitslag(
  val timestamp: String,
  val tafel: Int,
  val logregels: List<SpelerScore>
)
