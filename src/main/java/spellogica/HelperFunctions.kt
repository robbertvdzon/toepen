package spellogica

import model.SpelData
import model.Speler
import model.Tafel
import model.Toepkeuze
import util.Util

object HelperFunctions {

  fun startNieuwSpelAlleTafels(tafels: List<Tafel>, startscore: Int): List<Tafel> =
    tafels.map { nieuwSpel(it, startscore) }

  fun verdeelSpelersOverTafels(spelData: SpelData, oudeTafels: List<Tafel>): List<Tafel> {
    var newTafels = oudeTafels
    val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }
    val shuffledSpelers = Util.shuffleSpelers(spelersDieMeedoen).toMutableList()
    while (shuffledSpelers.isNotEmpty()) {
      newTafels = newTafels.map { tafel ->
        if (shuffledSpelers.isNotEmpty()) {
          val gebruiker = shuffledSpelers.removeAt(0)
          val spelers = tafel.spelers.plus(Speler(id = gebruiker.id, naam = gebruiker.naam))
          tafel.copy(spelers = spelers)
        } else {
          tafel
        }
      }
    }
    return newTafels
  }

  fun maakLegeTafels(aantalTafels: Int, spelData: SpelData): List<Tafel> =
    (1..aantalTafels).map { tafelNr -> Tafel(tafelNr = tafelNr, gepauzeerd = spelData.nieuweTafelAutoPause) }

  fun zoekSlagWinnaar(tafel: Tafel, spelData: SpelData): Speler? {
    val startKaart = tafel.findOpkomer(spelData)?.gespeeldeKaart
    if (startKaart == null) return null
    val winnaar = tafel.spelers.filter { it.actiefInSpel && !it.gepast }.maxBy { it.berekenScore(startKaart) }
    if (winnaar?.gepast == true) {
      // oei, diegene die gepast heeft, heeft gewonnen!
      // laat nu de eerste speler winnen die nog in het spel zit
      return tafel.spelers.firstOrNull { it.actiefInSpel && !it.gepast }
    }
    return winnaar
  }

  fun volgendeActieveSpeler(tafel: Tafel, speler: Speler?): Speler? {
    val actieveSpelers = tafel.spelers.filter { it.actiefInSpel || it == speler }
    if (speler == null) return actieveSpelers.firstOrNull()
    if (!actieveSpelers.contains(speler)) return null
    if (actieveSpelers.last() == speler) return actieveSpelers.firstOrNull()
    val index = actieveSpelers.indexOf(speler)
    return actieveSpelers[index + 1]
  }

  fun volgendeSpelerDieMoetToepen(spelers: List<Speler>, speler: Speler?): Speler? {
    val spelersDieMoetenToepen = spelers.filter { it.toepKeuze == Toepkeuze.GEEN_KEUZE || it == speler }
    if (spelersDieMoetenToepen.size == 1) return null // er zit maar 1 iemand in, dat is de speler zelf. Geeft dus null terum om aan te geven dat iedereen getoept heeft
    if (speler == null) return spelersDieMoetenToepen.firstOrNull()
    if (!spelersDieMoetenToepen.contains(speler)) return null
    if (spelersDieMoetenToepen.last() == speler) return spelersDieMoetenToepen.firstOrNull()
    val index = spelersDieMoetenToepen.indexOf(speler)
    return spelersDieMoetenToepen[index + 1]
  }

  fun volgendeSpelerDieMoetSpelen(tafel: Tafel, speler: Speler?): Speler? {
    val spelersDieMoetenSpelen = tafel.spelers.filter { (it.gespeeldeKaart == null && !it.gepast && it.actiefInSpel) || it == speler }
    if (speler == null) return spelersDieMoetenSpelen.firstOrNull()
    if (!spelersDieMoetenSpelen.contains(speler)) return null
    if (spelersDieMoetenSpelen.last() == speler) return spelersDieMoetenSpelen.firstOrNull()
    val index = spelersDieMoetenSpelen.indexOf(speler)
    return spelersDieMoetenSpelen[index + 1]
  }

  fun nieuweRonde(tafel: Tafel): Tafel {
    val kaarten = Util.getGeschutKaartenDeck()
    return tafel.copy(
      spelers = tafel.spelers.map { speler: Speler ->
        val handKaarten = (1..4).map { kaarten.removeAt(0) }
        SpelerService.nieuweRonde(speler, handKaarten)
      },
      inzet = 1
    )
  }

  fun nieuwSpel(tafel: Tafel, startscore: Int): Tafel {
    val newTafel = tafel.copy(
      huidigeSpeler = tafel.spelers.firstOrNull()?.id,
      opkomer = tafel.spelers.firstOrNull()?.id,
      tafelWinnaar = null,
      slagWinnaar = null,
      spelers = tafel.spelers.map { SpelerService.nieuwSpel(it, startscore) },
      spelersDieAfZijn = emptyList()
    )
    return nieuweRonde(newTafel)
  }

}
