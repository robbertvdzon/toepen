package model

data class SpelData(
  val alleSpelers: List<Gebruiker> = emptyList(),
  val tafels: List<Tafel> = emptyList(),
  val uitslagen: List<Uitslag> = emptyList(),
  val automatischNieuweTafels: Boolean = true,
  val nieuweTafelAutoPause: Boolean = false,
  val aantalAutomatischeNieuweTafels: Int = 3,
  val aantalFishesNieuweTafels: Int = 10,
  val monkeyDelayMsec: Long = 5000
) {
  val scorelijst: List<Gebruiker>
    get() = alleSpelers.filter { it.wilMeedoen }.sortedBy { it.score }.reversed()

  fun changeTafel(tafel: Tafel): Pair<SpelData, Tafel> {
    val tafels = tafels.map { if (it.tafelNr == tafel.tafelNr) tafel else it }
    val spelData = this.copy(tafels = tafels)
    return Pair(spelData, tafel)
  }

  fun changeGebruiker(gebruiker: Gebruiker): Pair<SpelData, Gebruiker> {
    val alleSpelers = alleSpelers.map { if (it.id == gebruiker.id) gebruiker else it }
    val spelData = this.copy(alleSpelers = alleSpelers)
    return Pair(spelData, gebruiker)
  }

  fun changeGebruiker2(gebruiker: Gebruiker): List<Gebruiker>  = alleSpelers.map { if (it.id == gebruiker.id) gebruiker else it }

  fun findGebruiker(spelerId: String?) = this.alleSpelers.firstOrNull { it.id == spelerId }
  fun findSpeler(spelerId: String?) = this.tafels.firstOrNull { containsSpeler(it, spelerId ?: "") }?.spelers?.firstOrNull { it.id == spelerId }
  fun findTafel(tafelNr: Int): Tafel = this.tafels.first { it.tafelNr == tafelNr }
  private fun containsSpeler(it: Tafel, spelerId: String?) = it.spelers.filter { speler -> speler.id == spelerId ?: "" }.firstOrNull() != null

}
