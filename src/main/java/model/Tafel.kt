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
  val gepauzeerd: Boolean = SpelContext.spelData.nieuweTafelAutoPause == true
) {
  fun findOpkomer() = SpelContext.spelData.findSpeler(opkomer)
  fun findHuidigeSpeler() = SpelContext.spelData.findSpeler(huidigeSpeler)
  fun findSlagWinnaar() = SpelContext.spelData.findSpeler(slagWinnaar)
  fun updateSpeler(speler:Speler):Tafel {
    val spelers = spelers.map {if (it.id==speler.id) speler else it}.toMutableList()
    return this.copy(spelers = spelers)
  }

}
