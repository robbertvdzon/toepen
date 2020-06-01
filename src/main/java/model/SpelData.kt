package model

data class SpelData(
  var alleSpelers: MutableList<Gebruiker> = emptyList<Gebruiker>().toMutableList(),
  var tafels: MutableList<Tafel> = emptyList<Tafel>().toMutableList(),
  var uitslagen: MutableList<Uitslag> = emptyList<Uitslag>().toMutableList(),
  var automatischNieuweTafels: Boolean? = true,
  var nieuweTafelAutoPause: Boolean? = false,
  var aantalAutomatischeNieuweTafels: Int? = 3,
  var aantalFishesNieuweTafels: Int? = 10,
  var monkeyDelayMsec: Long? = 5000
) {
  var scorelijst: MutableList<Gebruiker>
    get() = alleSpelers.filter { it.wilMeedoen }.sortedBy { it.score }.reversed().toMutableList()
    set(list) {}

  fun updateTafel(tafel:Tafel){
    tafels = tafels.map {if (it.tafelNr==tafel.tafelNr) tafel else it}.toMutableList()

  }

}
