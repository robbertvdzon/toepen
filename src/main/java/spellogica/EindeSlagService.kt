package spellogica

import model.*
import java.util.*

object EindeSlagService {

  fun eindeSlag(tafel: Tafel, spelData: SpelData): SpelData {
    Toepen.broadcastSlagWinnaar(tafel)
    val bijgewerkteSpelData = ScoreService.werkScoreBij(tafel, spelData)
    val aangepasteTafel = bijgewerkteSpelData.findTafel(tafel.tafelNr)
    val laatsteSlag = aangepasteTafel.spelers.firstOrNull { !it.gepast && it.actiefInSpel }?.kaarten?.size ?: 0 == 0
    val aantalSpelersDezeRonde = aangepasteTafel.spelers.filter { !it.gepast && it.actiefInSpel }.size
    return if (laatsteSlag || aantalSpelersDezeRonde < 2) {
      verwerktLaatsteSlag(aangepasteTafel, bijgewerkteSpelData)
    } else {
      nieuweSpelerNaEindeSlag(aangepasteTafel, bijgewerkteSpelData)
    }
  }

  private fun nieuweSpelerNaEindeSlag(tafel: Tafel, spelData: SpelData): SpelData {
    val nieuweSpelers = tafel.spelers.map { if (it.actiefInSpel) SpelerService.nieuweSlag(it) else it }
    return spelData.changeTafel(
      tafel.copy(
        spelers = nieuweSpelers,
        huidigeSpeler = tafel.slagWinnaar,
        opkomer = tafel.slagWinnaar,
        slagWinnaar = null,
        toeper = null
      )
    )
  }

  private fun verwerktLaatsteSlag(tafel: Tafel, spelData: SpelData): SpelData {
    Toepen.broadcastRondeWinnaar(tafel)
    val tafelMetGeupdateSpelers = werkLucifersSpelersBij(tafel)
    val spelDataMetBijgewerkteScore = ScoreService.werkScoreBij(tafelMetGeupdateSpelers, spelData.changeTafel(tafelMetGeupdateSpelers))
    val tafelMetBijgewerkteScore = spelDataMetBijgewerkteScore.findTafel(tafelMetGeupdateSpelers.tafelNr)
    val eindeSpel = tafelMetBijgewerkteScore.spelers.filter { it.actiefInSpel }.size == 1

    return if (eindeSpel) {
      verwerkEindeSpel(tafelMetBijgewerkteScore, spelDataMetBijgewerkteScore)
    } else {
      val huidigeSpeler = HelperFunctions.volgendeActieveSpeler(tafelMetBijgewerkteScore, tafelMetBijgewerkteScore.findSlagWinnaar(spelDataMetBijgewerkteScore))?.id
      val updatedTafel = HelperFunctions.nieuweRonde(
        tafelMetBijgewerkteScore.copy(
          huidigeSpeler = huidigeSpeler,
          opkomer = huidigeSpeler,
          slagWinnaar = null,
          toeper = null
        )
      )
      spelDataMetBijgewerkteScore.changeTafel(updatedTafel)
    }
  }


  private fun werkLucifersSpelersBij(tafel: Tafel): Tafel {
    val nieuweSpelers = tafel.spelers.map {
      // als je nog in het spel zat, en niet gepast had en niet de winnaar bent, dan ben je lucifers kwijt
      if (it.actiefInSpel && !it.gepast && tafel.slagWinnaar != it.id) {
        it.copy(
          actiefInSpel = it.actiefInSpel && (it.totaalLucifers > 0),
          totaalLucifers = it.totaalLucifers - it.ingezetteLucifers
        )
      } else it
    }
    return tafel.copy(spelers = nieuweSpelers)
  }

  private fun verwerkEindeSpel(tafel: Tafel, spelData: SpelData): SpelData {
    Toepen.broadcastSpelWinnaar(tafel)
    val newTafel = tafel.copy(
      tafelWinnaar = tafel.slagWinnaar,
      huidigeSpeler = null,
      slagWinnaar = null,
      toeper = null
    )
    val spelDataMetNieuweTafel = spelData.changeTafel(newTafel)
    val spelDataNieuweGebruikers = updateGebruikersScore(spelDataMetNieuweTafel, newTafel)
    return updateUitslagen(newTafel, spelDataNieuweGebruikers)
  }

  private fun updateUitslagen(tafel: Tafel, spelData: SpelData): SpelData {
    val scores = tafel.spelers.map {
      SpelerScore(it.naam, it.scoreDezeRonde)
    }
    return spelData.copy(
      uitslagen = spelData.uitslagen.plus(Uitslag(Date().toString(), tafel.tafelNr, scores))
    )
  }

  private fun updateGebruikersScore(spelData: SpelData, tafel: Tafel): SpelData {
    val updatedGebruikers = spelData.alleSpelers.map { gebruiker ->
      val speler = tafel.spelers.find { it.id == gebruiker.id }
      if (speler != null) {
        gebruiker.copy(score = gebruiker.score + speler.scoreDezeRonde)
      } else gebruiker
    }
    return spelData.copy(alleSpelers = updatedGebruikers)
  }





}
