package spellogica

import Toepen
import model.*
import util.Util
import java.util.*

object TafelService {

  fun vervolgSpel(tafelNr: Int, spelData: SpelData): SpelData {
    val tafel = spelData.findTafel(tafelNr)
    val eindeSpel = tafel.tafelWinnaar != null
    val toepRonde = tafel.toeper != null

    return if (eindeSpel) {
      spelData
    } else if (toepRonde) {
      vervolgToepRonde(tafel, spelData)
    } else vervolgSlagRonde(tafel, spelData)
  }

  private fun vervolgSlagRonde(tafel: Tafel, spelData: SpelData): SpelData {
    val alleSpelersHebbenKaartIngezet = tafel.spelers.all { it.gespeeldeKaart != null || !it.actiefInSpel || it.gepast }
    val updatedTafel = if (alleSpelersHebbenKaartIngezet) {
      val slagWinnaar = zoekSlagWinnaar(tafel, spelData)?.id
      tafel.copy(
        slagWinnaar = slagWinnaar,
        huidigeSpeler = slagWinnaar
      )
    } else {
      tafel.copy(
        huidigeSpeler = volgendeSpelerDieMoetSpelen(tafel, tafel.findHuidigeSpeler(spelData))?.id
      )
    }
    return spelData.changeTafel(updatedTafel)
  }

  private fun vervolgToepRonde(tafel: Tafel, spelData: SpelData): SpelData {
    val alleSpelersHebbenToepkeuzeGemaakt = tafel.spelers.all { it.toepKeuze != Toepkeuze.GEEN_KEUZE }
    val iedereenGepast = tafel.spelers.none { it.toepKeuze == Toepkeuze.MEE }
    val eindeRonde = alleSpelersHebbenToepkeuzeGemaakt && iedereenGepast
    val volgendeRonde = alleSpelersHebbenToepkeuzeGemaakt && !iedereenGepast

    val newTafel = if (eindeRonde) { // alle spelers hebben gepast, de toeper heeft de ronde gewonnen
      tafel.copy(
        slagWinnaar = tafel.spelers.firstOrNull { it.toepKeuze == Toepkeuze.TOEP }?.id,
        huidigeSpeler = tafel.slagWinnaar
      )
    } else if (volgendeRonde) { // alle spelers hebben hun toepkeuze doorgegeven
      tafel.copy(
        huidigeSpeler = tafel.toeper,
        toeper = null
      )
    } else {  // volgende speler moet toepen
      tafel.copy(
        huidigeSpeler = volgendeSpelerDieMoetToepen(tafel.spelers, tafel.findHuidigeSpeler(spelData))?.id
      )
    }

    return if (alleSpelersHebbenToepkeuzeGemaakt && iedereenGepast) {
      val updatedSpelData = spelData.changeTafel(newTafel)
      eindeSlag(newTafel, updatedSpelData)
    } else {
      spelData.changeTafel(newTafel)
    }
  }

  fun eindeSlag(tafel: Tafel, spelData: SpelData): SpelData {
    Toepen.broadcastSlagWinnaar(tafel)
    val bijgewerkteSpelData = werkScoreBij(tafel, spelData)
    val aangepasteTafel = bijgewerkteSpelData.findTafel(tafel.tafelNr)
    val laatsteSlag = aangepasteTafel.spelers.firstOrNull { it.gepast == false && it.actiefInSpel }?.kaarten?.size ?: 0 == 0
    val aantalSpelersDezeRonde = aangepasteTafel.spelers.filter { it.gepast == false && it.actiefInSpel }.size
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
    val spelDataMetBijgewerkteScore = werkScoreBij(tafelMetGeupdateSpelers, spelData.changeTafel(tafelMetGeupdateSpelers))
    val tafelMetBijgewerkteScore = spelDataMetBijgewerkteScore.findTafel(tafelMetGeupdateSpelers.tafelNr)
    val eindeSpel = tafelMetBijgewerkteScore.spelers.filter { it.actiefInSpel }.size == 1

    return if (eindeSpel) {
      verwerkEindeSpel(tafelMetBijgewerkteScore, spelDataMetBijgewerkteScore)
    } else {
      val huidigeSpeler = volgendeActieveSpeler(tafelMetBijgewerkteScore, tafelMetBijgewerkteScore.findSlagWinnaar(spelDataMetBijgewerkteScore))?.id
      val updatedTafel = nieuweRonde(
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

  fun werkScoreBij(tafelX: Tafel, spelDataX: SpelData): SpelData {
    var tafel = tafelX
    var spelData = spelDataX

    val spelersDieAfZijn = tafel.spelers.filter { it.actiefInSpel == false }
    val aantalSpelersDieInSpelZitten = tafel.spelers.filter { it.actiefInSpel == true }
    val aantalSpelersDieInSpelZittenCount = aantalSpelersDieInSpelZitten.size
    val nieuweSpelersDieAfZijn = spelersDieAfZijn.filter { !tafel.spelersDieAfZijn.contains(it.id) }
    val score = 4 - aantalSpelersDieInSpelZittenCount
    nieuweSpelersDieAfZijn.forEach {
      // laad de tafel opnieuw, die kan aangepast zijn
      tafel = spelData.tafels.first { it.tafelNr == tafel.tafelNr }
      val nieuweSpelersDieAfZijn = tafel.spelersDieAfZijn.plus(it.id)
      var scoreDezeRonde = 0
      if (score > 0) {
        scoreDezeRonde = score
      }
      val aangepasteSpeler = it.copy(scoreDezeRonde = scoreDezeRonde)
      tafel = tafel.changeSpeler(aangepasteSpeler)
      tafel = tafel.copy(spelersDieAfZijn = nieuweSpelersDieAfZijn)
      val newSpelData = spelData.changeTafel(tafel)
      spelData = newSpelData

    }
    if (aantalSpelersDieInSpelZittenCount == 1) {
      aantalSpelersDieInSpelZitten.forEach {
        val aangepasteSpeler = it.copy(scoreDezeRonde = 5)
        // laad de tafel opnieuw, die kan aangepast zijn
        tafel = spelData.tafels.first { it.tafelNr == tafel.tafelNr }
        tafel = tafel.changeSpeler(aangepasteSpeler)
        val newSpelData = spelData.changeTafel(tafel)
        spelData = newSpelData
      }
    }
    return spelData
  }

  fun nieuwSpel(tafel: Tafel, startscore: Int): Tafel {
    val newTafel = tafel.copy(
      huidigeSpeler = tafel.spelers.firstOrNull()?.id,
      opkomer = tafel.spelers.firstOrNull()?.id,
      tafelWinnaar = null,
      slagWinnaar = null,
      spelers = tafel.spelers.map { SpelerService.nieuwSpel(it, startscore) },
      spelersDieAfZijn = emptyList<String>()
    )
    val nieuweTafel = nieuweRonde(newTafel)
    return nieuweTafel
  }

  fun toep(spelData: SpelData, tafel: Tafel, speler: Speler): SpelData {
    // set automatische toep keuze voor alle spelers die geen keuze hebben
    val spelersMetToepKeuze = tafel.spelers.map {
      val toepKeuze =
        if (it.id == speler.id) {
          Toepkeuze.TOEP
        } else if (it.totaalLucifers == it.ingezetteLucifers) {
          Toepkeuze.MEE
        } else if (it.gepast) {
          Toepkeuze.PAS
        } else if (!it.actiefInSpel) {
          Toepkeuze.PAS
        } else Toepkeuze.GEEN_KEUZE
      it.copy(toepKeuze = toepKeuze)
    }

    val volgendeSpelerVoorToep = volgendeSpelerDieMoetToepen(spelersMetToepKeuze, spelersMetToepKeuze.first { it.id == speler.id })
    return if (volgendeSpelerVoorToep == null) {
      spelData.changeTafel(tafel.copy(
        huidigeSpeler = tafel.toeper,
        spelers = spelersMetToepKeuze,
        toeper = null
      ))
    } else {
      return spelData.changeTafel(tafel.copy(
        huidigeSpeler = volgendeSpelerVoorToep.id,
        spelers = spelersMetToepKeuze,
        toeper = if (tafel.toeper != null) tafel.toeper else speler.id
      ))
    }
  }

  private fun zoekSlagWinnaar(tafel: Tafel, spelData: SpelData): Speler? {
    val startKaart = tafel.findOpkomer(spelData)?.gespeeldeKaart
    if (startKaart == null) return null
    val winnaar = tafel.spelers.filter { it.actiefInSpel && !it.gepast }.maxBy { it.berekenScore(startKaart) }
    if (winnaar?.gepast ?: false) {
      // oei, diegene die gepast heeft, heeft gewonnen!
      // laat nu de eerste speler winnen die nog in het spel zit
      return tafel.spelers.firstOrNull() { it.actiefInSpel && !it.gepast }
    }
    return winnaar
  }

  private fun volgendeActieveSpeler(tafel: Tafel, speler: Speler?): Speler? {
    val actieveSpelers = tafel.spelers.filter { it.actiefInSpel || it == speler }
    if (speler == null) return actieveSpelers.firstOrNull()
    if (!actieveSpelers.contains(speler)) return null
    if (actieveSpelers.last() == speler) return actieveSpelers.firstOrNull()
    val index = actieveSpelers.indexOf(speler)
    return actieveSpelers.get(index + 1)
  }

  private fun volgendeSpelerDieMoetToepen(spelers: List<Speler>, speler: Speler?): Speler? {
    val spelersDieMoetenToepen = spelers.filter { it.toepKeuze == Toepkeuze.GEEN_KEUZE || it == speler }
    if (spelersDieMoetenToepen.size == 1) return null // er zit maar 1 iemand in, dat is de speler zelf. Geeft dus null terum om aan te geven dat iedereen getoept heeft
    if (speler == null) return spelersDieMoetenToepen.firstOrNull()
    if (!spelersDieMoetenToepen.contains(speler)) return null
    if (spelersDieMoetenToepen.last() == speler) return spelersDieMoetenToepen.firstOrNull()
    val index = spelersDieMoetenToepen.indexOf(speler)
    return spelersDieMoetenToepen.get(index + 1)
  }

  private fun volgendeSpelerDieMoetSpelen(tafel: Tafel, speler: Speler?): Speler? {
    val spelersDieMoetenSpelen = tafel.spelers.filter { (it.gespeeldeKaart == null && it.gepast == false && it.actiefInSpel) || it == speler }
    if (speler == null) return spelersDieMoetenSpelen.firstOrNull()
    if (!spelersDieMoetenSpelen.contains(speler)) return null
    if (spelersDieMoetenSpelen.last() == speler) return spelersDieMoetenSpelen.firstOrNull()
    val index = spelersDieMoetenSpelen.indexOf(speler)
    return spelersDieMoetenSpelen.get(index + 1)
  }

  private fun nieuweRonde(tafel: Tafel): Tafel {
    val kaarten = Util.getGeschutKaartenDeck()
    return tafel.copy(
      spelers = tafel.spelers.map { speler: Speler ->
        val handKaarten = (1..4).map { kaarten.removeAt(0) }
        SpelerService.nieuweRonde(speler, handKaarten)
      },
      inzet = 1
    )
  }

}
