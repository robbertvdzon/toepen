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
    val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
    Util.shuffleSpelers(spelersDieMeedoen)
    while (spelersDieMeedoen.isNotEmpty()) {
      newTafels = newTafels.map { tafel ->
        if (spelersDieMeedoen.isNotEmpty()) {
          val gebruiker = spelersDieMeedoen.removeAt(0)
          val spelers = tafel.spelers.plus(Speler(id = gebruiker.id, naam = gebruiker.naam))
          val tafel = tafel.copy(spelers = spelers.toMutableList())
          tafel
        } else {
          tafel
        }
      }
    }
    return newTafels
  }

  fun maakLegeTafels(aantalTafels: Int, spelData: SpelData): List<Tafel> =
    (1..aantalTafels).map {
      Tafel(
        tafelNr = it,
        gepauzeerd = spelData.nieuweTafelAutoPause == true
      )
    }


}