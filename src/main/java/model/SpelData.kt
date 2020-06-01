package model

data class SpelData(
  var alleSpelers: MutableList<Gebruiker> = emptyList<Gebruiker>().toMutableList(),
  val tafels: MutableList<Tafel> = emptyList<Tafel>().toMutableList(),
  val uitslagen: MutableList<Uitslag> = emptyList<Uitslag>().toMutableList(),
  val automatischNieuweTafels: Boolean? = true,
  val nieuweTafelAutoPause: Boolean? = false,
  val aantalAutomatischeNieuweTafels: Int? = 3,
  val aantalFishesNieuweTafels: Int? = 10,
  val monkeyDelayMsec: Long? = 5000
) {
  var scorelijst: MutableList<Gebruiker>
    get() = alleSpelers.filter { it.wilMeedoen }.sortedBy { it.score }.reversed().toMutableList()
    set(list) {}

  fun updateTafel(tafel:Tafel): Pair<SpelData, Tafel>{
    val tafels = tafels.map {if (it.tafelNr==tafel.tafelNr) tafel else it}.toMutableList()
    val spelData = SpelContext.spelData.copy(
      tafels = tafels
    )
    return Pair(spelData,tafel)
  }

  fun updateGebruiker(gebruiker:Gebruiker): Gebruiker{
    alleSpelers = alleSpelers.map {if (it.id==gebruiker.id) gebruiker else it}.toMutableList()
    return gebruiker
  }

  fun findGebruiker(spelerId: String?) = this
    .alleSpelers
    .firstOrNull { it.id == spelerId }

  fun findSpeler(spelerId: String?) = this
    .tafels
    .firstOrNull { containsSpeler(it, spelerId ?: "") }
    ?.spelers
    ?.firstOrNull { it.id == spelerId }

  fun findTafel(tafelNr:Int): Tafel{
    return this.tafels.first { it.tafelNr==tafelNr }
  }

  private fun containsSpeler(it: Tafel, spelerId: String?) =
    it.spelers.filter { speler -> speler.id == spelerId ?: "" }.firstOrNull() != null


}
