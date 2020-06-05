package spellogica

import model.SpelData
import model.Tafel
import model.Toepkeuze

object VervolgSpelService {

  fun vervolgSpel(tafelNr: Int, spelData: SpelData): SpelData {
    val tafel = spelData.findTafel(tafelNr)
    val eindeSpel = tafel.tafelWinnaar != null
    val toepRonde = tafel.toeper != null

    return when {
        eindeSpel -> {
          spelData
        }
        toepRonde -> {
          vervolgToepRonde(tafel, spelData)
        }
        else -> vervolgSlagRonde(tafel, spelData)
    }
  }

  private fun vervolgSlagRonde(tafel: Tafel, spelData: SpelData): SpelData {
    val alleSpelersHebbenKaartIngezet = tafel.spelers.all { it.gespeeldeKaart != null || !it.actiefInSpel || it.gepast }
    val updatedTafel = if (alleSpelersHebbenKaartIngezet) {
      val slagWinnaar = HelperFunctions.zoekSlagWinnaar(tafel, spelData)?.id
      tafel.copy(
        slagWinnaar = slagWinnaar,
        huidigeSpeler = slagWinnaar
      )
    } else {
      tafel.copy(
        huidigeSpeler = HelperFunctions.volgendeSpelerDieMoetSpelen(tafel, tafel.findHuidigeSpeler(spelData))?.id
      )
    }
    return spelData.changeTafel(updatedTafel)
  }

  private fun vervolgToepRonde(tafel: Tafel, spelData: SpelData): SpelData {
    val alleSpelersHebbenToepkeuzeGemaakt = tafel.spelers.all { it.toepKeuze != Toepkeuze.GEEN_KEUZE }
    val iedereenGepast = tafel.spelers.none { it.toepKeuze == Toepkeuze.MEE }
    val eindeRonde = alleSpelersHebbenToepkeuzeGemaakt && iedereenGepast
    val volgendeRonde = alleSpelersHebbenToepkeuzeGemaakt && !iedereenGepast

    val newTafel = when {
        eindeRonde -> { // alle spelers hebben gepast, de toeper heeft de ronde gewonnen
          tafel.copy(
            slagWinnaar = tafel.spelers.firstOrNull { it.toepKeuze == Toepkeuze.TOEP }?.id,
            huidigeSpeler = tafel.slagWinnaar
          )
        }
        volgendeRonde -> { // alle spelers hebben hun toepkeuze doorgegeven
          tafel.copy(
            huidigeSpeler = tafel.toeper,
            toeper = null
          )
        }
        else -> {  // volgende speler moet toepen
          tafel.copy(
            huidigeSpeler = HelperFunctions.volgendeSpelerDieMoetToepen(tafel.spelers, tafel.findHuidigeSpeler(spelData))?.id
          )
        }
    }

    return if (alleSpelersHebbenToepkeuzeGemaakt && iedereenGepast) {
      val updatedSpelData = spelData.changeTafel(newTafel)
      EindeSlagService.eindeSlag(newTafel, updatedSpelData)
    } else {
      spelData.changeTafel(newTafel)
    }
  }


}
