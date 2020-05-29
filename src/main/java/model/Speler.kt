package model

data class Speler(
  val id: String = "",
  val naam: String = "",
  val kaarten: List<Kaart> = emptyList<Kaart>(),
  val gespeeldeKaart: Kaart? = null,
  val totaalLucifers: Int = 0,
  val ingezetteLucifers: Int = 0,
  val gepast: Boolean = false, // gepast na een toep
  val toepKeuze: Toepkeuze = Toepkeuze.GEEN_KEUZE,
  val actiefInSpel: Boolean = true, // zit nog in het spel (is niet af)
  val scoreDezeRonde: Int = 0
) {
  fun berekenScore(startKaart: Kaart): Int {
    if (gespeeldeKaart == null) return 0
    if (startKaart.symbool != gespeeldeKaart.symbool) return 0
    return gespeeldeKaart.waarde ?: 0
  }
}
