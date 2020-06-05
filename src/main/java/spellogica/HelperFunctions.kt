package spellogica

import model.SpelData
import model.Speler
import model.Tafel
import util.Util

object HelperFunctions {

  fun startNieuwSpelAlleTafels(tafels: List<Tafel>, startscore: Int): List<Tafel> =
    tafels.map { TafelService.nieuwSpel(it, startscore) }

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

}
