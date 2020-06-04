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
        huidigeSpeler = volgendeSpelerDieMoetToepen(tafel, tafel.findHuidigeSpeler(spelData))?.id
      )
    }

    val updatedSpelData = spelData.changeTafel(newTafel)

    return if (alleSpelersHebbenToepkeuzeGemaakt && iedereenGepast) {
      eindeSlag(newTafel, updatedSpelData)
    }
    else {
      updatedSpelData
    }
  }

  /*
  TODO: deze functie kan vast mooier
   */
  fun eindeSlag(tafelX: Tafel, spelDataX: SpelData): SpelData {
    var tafel = tafelX
    var spelData = spelDataX
    Toepen.broadcastSlagWinnaar(tafel)
    spelData = werkScoreBij(tafel, spelData)
    tafel = spelData.findTafel(tafel.tafelNr)

    val laatsteSlag = tafel.spelers.firstOrNull { it.gepast == false && it.actiefInSpel }?.kaarten?.size ?: 0 == 0
    val aantalSpelersDezeRonde = tafel.spelers.filter { it.gepast == false && it.actiefInSpel }.size
    if (laatsteSlag || aantalSpelersDezeRonde < 2) {
      Toepen.broadcastRondeWinnaar(tafel)

      val nieuweSpelers = tafel.spelers.map {
        // als je nog in het spel zat, en niet gepast had en niet de winnaar bent, dan ben je lucifers kwijt!
        var totaalLucifers = it.totaalLucifers
        var actiefInSpel = it.actiefInSpel

        if (it.actiefInSpel && !it.gepast && tafel.slagWinnaar != it.id) {
          totaalLucifers -= it.ingezetteLucifers
          if (it.totaalLucifers <= 0) actiefInSpel = false
        }
        it.copy(
          actiefInSpel = actiefInSpel,
          totaalLucifers = totaalLucifers
        )
      }
      val updatedTafel = tafel.copy(
        spelers = nieuweSpelers
      )
      val newSpeldata = spelData.changeTafel(updatedTafel)
      tafel = updatedTafel
      spelData = newSpeldata

      val eindeSpel = tafel.spelers.filter { it.actiefInSpel }.size == 1
      spelData = werkScoreBij(tafel, spelData)
      tafel = spelData.findTafel(tafel.tafelNr)
      if (eindeSpel) {// einde spel
        Toepen.broadcastSpelWinnaar(tafel)
        val newTafel = tafel.copy(
          tafelWinnaar = tafel.slagWinnaar,
          huidigeSpeler = null,
          slagWinnaar = null,
          toeper = null
        )
        val newSpeldata = spelData.changeTafel(
          newTafel
        )
        tafel = newTafel
        spelData = newSpeldata

        val scores = tafel.spelers.map {
          SpelerScore(it.naam, it.scoreDezeRonde)
        }

        val updatedGebruikers = spelData.alleSpelers.map { gebruiker ->
          val speler = tafel.spelers.find { it.id == gebruiker.id }
          if (speler != null) {
            gebruiker.copy(
              score = gebruiker.score + speler.scoreDezeRonde
            )
          } else gebruiker
        }

        spelData = spelData.copy(
          uitslagen = spelData.uitslagen.plus(Uitslag(
            Date().toString(), tafel.tafelNr, scores
          )),
          alleSpelers = updatedGebruikers
        )
      } else {// niet einde spel
        val huidigeSpeler = volgendeActieveSpeler(tafel, tafel.findSlagWinnaar(spelData))?.id
        val newTafel = tafel.copy(
          huidigeSpeler = huidigeSpeler,
          opkomer = huidigeSpeler,
          slagWinnaar = null,
          toeper = null
        )
        val newSpeldata = spelData.changeTafel(
          newTafel
        )
        tafel = newTafel
        spelData = newSpeldata

        val updatedTafel = nieuweRonde(tafel)
        val newSpeldata2 = spelData.changeTafel(updatedTafel)
        tafel = updatedTafel
        spelData = newSpeldata2
      }
    } else { // niet de laatste slag
      val newTafel2 = tafel.copy(
        huidigeSpeler = tafel.slagWinnaar,
        opkomer = tafel.slagWinnaar,
        slagWinnaar = null,
        toeper = null
      )
      val newSpeldata2 = spelData.changeTafel(
        newTafel2
      )
      tafel = newTafel2
      spelData = newSpeldata2

      val updatedTafel = tafel.copy(
        spelers = tafel.spelers.map { if (it.actiefInSpel) SpelerService.nieuweSlag(it) else it }
      )
      val newSpelData = spelData.changeTafel(updatedTafel)
      spelData = newSpelData
    }
    return spelData
  }

  /*
  TODO: deze functie kan vast mooier
   */
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

  /*
  TODO: deze functie kan vast mooier
   */
  fun toep(spelDataX: SpelData, tafelX: Tafel, speler: Speler): SpelData {
    var tafel = tafelX
    var spelData = spelDataX

    if (tafel.toeper == null) {
      val newTafel = tafel.copy(
        toeper = speler.id // de eerste toeper bewaren
      )
      val newSpeldata = spelData.changeTafel(
        newTafel
      )
      tafel = newTafel
      spelData = newSpeldata

    }

    // set toep keuze voor alle spelers
    tafel.spelers.forEach {
      var toepKeuze = Toepkeuze.GEEN_KEUZE
      if (!it.actiefInSpel) {
        toepKeuze = Toepkeuze.PAS
      }
      if (it.gepast) {
        toepKeuze = Toepkeuze.PAS
      }
      if (it.totaalLucifers == it.ingezetteLucifers) {
        toepKeuze = Toepkeuze.MEE
      }
      val updatedTafel = tafel.changeSpeler(it.copy(toepKeuze = toepKeuze))
      val newSpeldata = spelData.changeTafel(updatedTafel)
      tafel = updatedTafel
      spelData = newSpeldata


    }
    // laad de tafel opnieuw, die kan aangepast zijn
    val tafel2 = spelData.tafels.first { it.tafelNr == tafel.tafelNr }

    // set toep keuze op toep voor de toeper
    val updatedSpeler = speler.copy(toepKeuze = Toepkeuze.TOEP)
    val updatedTafel = tafel2.changeSpeler(updatedSpeler)
    val newSpelData = spelData.changeTafel(updatedTafel)
    spelData = newSpelData

    val volgendeSpelerVoorToep = volgendeSpelerDieMoetToepen(updatedTafel, updatedSpeler)
    if (volgendeSpelerVoorToep == null) {

      val newSpelData = spelData.changeTafel(updatedTafel.copy(
        huidigeSpeler = updatedTafel.toeper,
        toeper = null
      ))
      spelData = newSpelData


    } else {
      val newSpelData = spelData.changeTafel(updatedTafel.copy(
        huidigeSpeler = volgendeSpelerVoorToep.id
      ))
      spelData = newSpelData
    }

    return spelData
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

  private fun volgendeSpelerDieMoetToepen(tafel: Tafel, speler: Speler?): Speler? {
    val spelersDieMoetenToepen = tafel.spelers.filter { it.toepKeuze == Toepkeuze.GEEN_KEUZE || it == speler }
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
