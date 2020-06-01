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
//  fun findSpelersDieAfZijn() = spelersDieAfZijn.map { SpelContext.findSpeler(it) }.filter { it != null }.map { it as Speler }
  fun findOpkomer() = SpelContext.findSpeler(opkomer)
  fun findHuidigeSpeler() = SpelContext.findSpeler(huidigeSpeler)
//  fun findToeper() = SpelContext.findSpeler(toeper)
  fun findSlagWinnaar() = SpelContext.findSpeler(slagWinnaar)
//  fun findTafelWinnaar() = SpelContext.findSpeler(tafelWinnaar)
  fun updateSpeler(speler:Speler):Tafel {
    val spelers = spelers.map {if (it.id==speler.id) speler else it}.toMutableList()
    return this.copy(spelers = spelers)
  }

}
