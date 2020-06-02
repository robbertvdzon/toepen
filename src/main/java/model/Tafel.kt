package model

data class Tafel(
  val tafelNr: Int,
  val spelers: List<Speler> = emptyList(),
  val spelersDieAfZijn: List<String> = emptyList(),
  val opkomer: String? = null,
  val huidigeSpeler: String? = null,
  val toeper: String? = null,
  val inzet: Int = 0,
  val slagWinnaar: String? = null,
  val tafelWinnaar: String? = null,
  val gepauzeerd: Boolean
) {
  fun findOpkomer(spelData: SpelData) = spelData.findSpeler(opkomer)
  fun findHuidigeSpeler(spelData: SpelData) = spelData.findSpeler(huidigeSpeler)
  fun findSlagWinnaar(spelData: SpelData) = spelData.findSpeler(slagWinnaar)
  fun changeSpeler(speler: Speler): Tafel {
    val spelers = spelers.map { if (it.id == speler.id) speler else it }.toMutableList()
    return this.copy(spelers = spelers)
  }

}
