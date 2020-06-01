package model

data class Tafel(
  val tafelNr: Int,
  val spelers: List<Speler> = emptyList(),
  val spelersDieAfZijn: List<String> = emptyList(),
  var opkomer: String? = null,
  var huidigeSpeler: String? = null,
  var toeper: String? = null,
  var inzet: Int = 0,
  var slagWinnaar: String? = null,
  var tafelWinnaar: String? = null,
  var gepauzeerd: Boolean = SpelContext.spelData.nieuweTafelAutoPause == true
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
