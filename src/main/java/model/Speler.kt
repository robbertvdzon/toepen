package model

data class Speler(
  val id: String = "",
  var naam: String = "",
  var kaarten: MutableList<Kaart> = emptyList<Kaart>().toMutableList(),
  var gespeeldeKaart: Kaart? = null,
  var totaalLucifers: Int = 0,
  var ingezetteLucifers: Int = 0,
  var gepast: Boolean = false, // gepast na een toep
  var toepKeuze: Toepkeuze = Toepkeuze.GEEN_KEUZE,
  var actiefInSpel: Boolean = true, // zit nog in het spel (is niet af)
  var scoreDezeRonde: Int = 0
) {
  fun berekenScore(startKaart: Kaart): Int {
    if (gespeeldeKaart == null) return 0
    if (startKaart.symbool != gespeeldeKaart?.symbool) return 0
    return gespeeldeKaart?.waarde ?: 0
  }
}
