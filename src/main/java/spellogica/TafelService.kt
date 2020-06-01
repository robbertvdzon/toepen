package spellogica

import Toepen
import model.*
import util.Util
import java.util.*

object TafelService {


  fun vervolgSpel(tafelX: Tafel, spelDataX: SpelData): SpelData {
    var tafel = tafelX
    var spelData = spelDataX
    spelData = werkScoreBij(tafel, spelData)

    tafel = spelData.findTafel(tafel.tafelNr)

    if (tafel.tafelWinnaar != null) {
      return spelData
    }
    if (tafel.toeper != null) {
      // verwerk toep
      val alleSpelersHebbenToepkeuzeGemaakt = tafel.spelers.all { it.toepKeuze != Toepkeuze.GEEN_KEUZE }
      if (alleSpelersHebbenToepkeuzeGemaakt) {
        val iedereenGepast = tafel.spelers.none { it.toepKeuze == Toepkeuze.MEE }
        if (iedereenGepast) {
          // einde deze ronde!
          val (newSpeldata, newTafel) = spelData.updateTafel(tafel.copy(
            slagWinnaar = tafel.spelers.firstOrNull { it.toepKeuze == Toepkeuze.TOEP }?.id,
            huidigeSpeler = tafel.slagWinnaar
          ))
          tafel = newTafel
          spelData = newSpeldata
          spelData = eindeSlag(tafel, spelData)
        } else {
          val (newSpeldata, newTafel) = spelData.updateTafel(tafel.copy(
            huidigeSpeler = tafel.toeper,
            toeper = null
          ))
          tafel = newTafel
          spelData = newSpeldata

        }
      } else {
        val (newSpeldata, newTafel) = spelData.updateTafel(tafel.copy(
          huidigeSpeler = volgendeSpelerDieMoetToepen(tafel, tafel.findHuidigeSpeler(spelData))?.id
        ))
        tafel = newTafel
        spelData = newSpeldata
      }
    } else {
      // verwerk slag
      val alleSpelersHebbenKaartIngezet = tafel.spelers.all { it.gespeeldeKaart != null || !it.actiefInSpel || it.gepast }
      if (alleSpelersHebbenKaartIngezet) {
        val slagWinnaar = zoekSlagWinnaar(tafel, spelData)?.id
        val (newSpeldata, newTafel) = spelData.updateTafel(tafel.copy(
          slagWinnaar = slagWinnaar,
          huidigeSpeler = slagWinnaar
        ))
        tafel = newTafel
        spelData = newSpeldata
      } else {
        val (newSpeldata, newTafel) = spelData.updateTafel(tafel.copy(
          huidigeSpeler = volgendeSpelerDieMoetSpelen(tafel, tafel.findHuidigeSpeler(spelData))?.id
        ))
        tafel = newTafel
        spelData = newSpeldata
      }

    }
    return spelData
  }

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
          if (it.totaalLucifers == 0) actiefInSpel = false
        }
        it.copy(
          actiefInSpel = actiefInSpel,
          totaalLucifers = totaalLucifers
        )
      }.toMutableList()
      val updatedTafel = tafel.copy(
        spelers = nieuweSpelers
      )
      val (newSpeldata, newTafel) = spelData.updateTafel(updatedTafel)
      tafel = newTafel
      spelData = newSpeldata

      val eindeSpel = tafel.spelers.filter { it.actiefInSpel }.size == 1
      spelData = werkScoreBij(tafel, spelData)
      tafel = spelData.findTafel(tafel.tafelNr)
      if (eindeSpel) {// einde spel
        Toepen.broadcastSpelWinnaar(tafel)
        val (newSpeldata, newTafel) = spelData.updateTafel(
          tafel.copy(
            tafelWinnaar = tafel.slagWinnaar,
            huidigeSpeler = null,
            slagWinnaar = null,
            toeper = null
          )
        )
        tafel = newTafel
        spelData = newSpeldata


        val scores: MutableList<SpelerScore> = emptyList<SpelerScore>().toMutableList()
        tafel.spelers.forEach {
          val gebruiker = spelData.findGebruiker(it.id)
          if (gebruiker != null) {
            val (updatedSpelData, _) = spelData.updateGebruiker(gebruiker.copy(
              score = gebruiker.score + it.scoreDezeRonde
            ))
            spelData = updatedSpelData
          }
          scores.add(SpelerScore(it.naam, it.scoreDezeRonde))
        }

        spelData = spelData.copy(
          uitslagen = spelData.uitslagen.plus(Uitslag(
            Date().toString(), tafel.tafelNr, scores
          ))
        )



        println("#Uitslagen:" + spelData.uitslagen.size)
      } else {// niet einde spel
        val huidigeSpeler = volgendeActieveSpeler(tafel, tafel.findSlagWinnaar(spelData))?.id
        val (newSpeldata, newTafel) = spelData.updateTafel(
          tafel.copy(
            huidigeSpeler = huidigeSpeler,
            opkomer = huidigeSpeler,
            slagWinnaar = null,
            toeper = null
          )
        )
        tafel = newTafel
        spelData = newSpeldata

        val updatedTafel = nieuweRonde(tafel)
        val (newSpeldata2, newTafel2) = spelData.updateTafel(updatedTafel)
        tafel = newTafel2
        spelData = newSpeldata2
      }
    } else { // niet de laatste slag
      val (newSpeldata2, newTafel2) = spelData.updateTafel(
        tafel.copy(
          huidigeSpeler = tafel.slagWinnaar,
          opkomer = tafel.slagWinnaar,
          slagWinnaar = null,
          toeper = null
        )
      )
      tafel = newTafel2
      spelData = newSpeldata2

      val updatedTafel = tafel.copy(
        spelers = tafel.spelers.map { if (it.actiefInSpel) SpelerService.nieuweSlag(it) else it }.toMutableList()
      )
      val (newSpelData, newTafel) = spelData.updateTafel(updatedTafel)
      spelData = newSpelData


    }
    return spelData
  }

  private fun werkScoreBij(tafelX: Tafel, spelDataX: SpelData): SpelData {
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
      tafel = tafel.updateSpeler(aangepasteSpeler)
      tafel = tafel.copy(spelersDieAfZijn = nieuweSpelersDieAfZijn)
      val (newSpelData, newTafel) = spelData.updateTafel(tafel)
      spelData = newSpelData

    }
    if (aantalSpelersDieInSpelZittenCount == 1) {
      aantalSpelersDieInSpelZitten.forEach {
        val aangepasteSpeler = it.copy(scoreDezeRonde = 5)
        // laad de tafel opnieuw, die kan aangepast zijn
        tafel = spelData.tafels.first { it.tafelNr == tafel.tafelNr }
        tafel = tafel.updateSpeler(aangepasteSpeler)
        val (newSpelData, newTafel) = spelData.updateTafel(tafel)
        spelData = newSpelData
      }
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
      }.toMutableList(),
      inzet = 1
    )
  }

  fun nieuwSpel(spelData: SpelData, tafel: Tafel, startscore: Int): SpelData {
    val newTafel = tafel.copy(
      huidigeSpeler = tafel.spelers.firstOrNull()?.id,
      opkomer = tafel.spelers.firstOrNull()?.id,
      tafelWinnaar = null,
      slagWinnaar = null,
      spelers = tafel.spelers.map { SpelerService.nieuwSpel(it, startscore) }.toMutableList(),
      spelersDieAfZijn = emptyList<String>().toMutableList()
    )
    val nieuweTafel = nieuweRonde(newTafel)
    val (newSpelData, _) = spelData.updateTafel(nieuweTafel)
    return newSpelData
  }


  fun toep(spelDataX: SpelData, tafelX: Tafel, speler: Speler): SpelData {
    var tafel = tafelX
    var spelData = spelDataX

    if (tafel.toeper == null) {
      val (newSpeldata, newTafel) = spelData.updateTafel(
        tafel.copy(
          toeper = speler.id // de eerste toeper bewaren
        )
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
      val updatedTafel = tafel.updateSpeler(it.copy(toepKeuze = toepKeuze))
      val (newSpeldata, newTafel) = spelData.updateTafel(updatedTafel)
      tafel = newTafel
      spelData = newSpeldata


    }
    // laad de tafel opnieuw, die kan aangepast zijn
    val tafel2 = spelData.tafels.first { it.tafelNr == tafel.tafelNr }

    // set toep keuze op toep voor de toeper
    val updatedSpeler = speler.copy(toepKeuze = Toepkeuze.TOEP)
    val updatedTafel = tafel2.updateSpeler(updatedSpeler)
    val (newSpelData, newTafel) = spelData.updateTafel(updatedTafel)
    spelData = newSpelData

    val volgendeSpelerVoorToep = volgendeSpelerDieMoetToepen(updatedTafel, updatedSpeler)
    if (volgendeSpelerVoorToep == null) {

      val (newSpelData, newTafel) = spelData.updateTafel(updatedTafel.copy(
        huidigeSpeler = updatedTafel.toeper,
        toeper = null
      ))
      spelData = newSpelData


    } else {
      val (newSpelData, newTafel) = spelData.updateTafel(updatedTafel.copy(
        huidigeSpeler = volgendeSpelerVoorToep.id
      ))
      spelData = newSpelData
    }

    return spelData
  }
}
