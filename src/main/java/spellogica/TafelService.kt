package spellogica

import Toepen
import model.*
import util.Util
import java.util.*

object TafelService {

  fun vervolgSpel(tafel: Tafel) {
    werkScoreBij(tafel)
    val tafelMetNieuweScore = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }

    if (tafelMetNieuweScore.tafelWinnaar != null) return
    if (tafelMetNieuweScore.toeper != null) {
      // verwerk toep
      val alleSpelersHebbenToepkeuzeGemaakt = tafelMetNieuweScore.spelers.all { it.toepKeuze != Toepkeuze.GEEN_KEUZE }
      if (alleSpelersHebbenToepkeuzeGemaakt) {
        val iedereenGepast = tafelMetNieuweScore.spelers.none { it.toepKeuze == Toepkeuze.MEE }
        if (iedereenGepast) {
          // einde deze ronde!
          tafelMetNieuweScore.slagWinnaar = tafelMetNieuweScore.spelers.firstOrNull { it.toepKeuze == Toepkeuze.TOEP }?.id
          tafelMetNieuweScore.huidigeSpeler = tafelMetNieuweScore.slagWinnaar
          eindeSlag(tafelMetNieuweScore)
        } else {
          tafelMetNieuweScore.huidigeSpeler = tafelMetNieuweScore.toeper
          tafelMetNieuweScore.toeper = null
        }
      } else {
        tafelMetNieuweScore.huidigeSpeler = volgendeSpelerDieMoetToepen(tafelMetNieuweScore, tafelMetNieuweScore.findHuidigeSpeler())?.id
      }
    } else {
      // verwerk slag
      val alleSpelersHebbenKaartIngezet = tafelMetNieuweScore.spelers.all { it.gespeeldeKaart != null || !it.actiefInSpel || it.gepast }
      if (alleSpelersHebbenKaartIngezet) {
        tafelMetNieuweScore.slagWinnaar = zoekSlagWinnaar(tafelMetNieuweScore)?.id
        tafelMetNieuweScore.huidigeSpeler = tafelMetNieuweScore.slagWinnaar
      } else {
        tafelMetNieuweScore.huidigeSpeler = volgendeSpelerDieMoetSpelen(tafelMetNieuweScore, tafelMetNieuweScore.findHuidigeSpeler())?.id
      }

    }
  }

  fun eindeSlag(tafel: Tafel) {
    Toepen.broadcastSlagWinnaar(tafel)
    werkScoreBij(tafel)
    val tafelMetNieuweScore = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }

    val laatsteSlag = tafelMetNieuweScore.spelers.firstOrNull { it.gepast == false && it.actiefInSpel }?.kaarten?.size ?: 0 == 0
    val aantalSpelersDezeRonde = tafelMetNieuweScore.spelers.filter { it.gepast == false && it.actiefInSpel }.size
    if (laatsteSlag || aantalSpelersDezeRonde < 2) {
      Toepen.broadcastRondeWinnaar(tafelMetNieuweScore)

      val nieuweSpelers = tafelMetNieuweScore.spelers.map{
        // als je nog in het spel zat, en niet gepast had en niet de winnaar bent, dan ben je lucifers kwijt!
        var totaalLucifers = it.totaalLucifers
        var actiefInSpel = it.actiefInSpel

        if (it.actiefInSpel && !it.gepast && tafelMetNieuweScore.slagWinnaar != it.id) {
          totaalLucifers -= it.ingezetteLucifers
          if (it.totaalLucifers == 0) actiefInSpel = false
        }
        it.copy(
          actiefInSpel = actiefInSpel,
          totaalLucifers = totaalLucifers
        )
      }.toMutableList()
      val updatedTafel = tafelMetNieuweScore.copy(
        spelers = nieuweSpelers
      )
      SpelContext.spelData.updateTafel(updatedTafel)
      val tafelMetUpdatedSpelers = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }



      val eindeSpel = tafelMetUpdatedSpelers.spelers.filter { it.actiefInSpel }.size == 1
      werkScoreBij(tafelMetUpdatedSpelers)
      val tafelMetUpdatedScore = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }
      if (eindeSpel) {// einde spel
        Toepen.broadcastSpelWinnaar(tafelMetUpdatedScore)
        tafelMetUpdatedScore.tafelWinnaar = tafelMetUpdatedScore.slagWinnaar
        tafelMetUpdatedScore.huidigeSpeler = null
        tafelMetUpdatedScore.slagWinnaar = null
        tafelMetUpdatedScore.toeper = null

        var scores: MutableList<SpelerScore> = emptyList<SpelerScore>().toMutableList()
        tafelMetUpdatedScore.spelers.forEach {
          val gebruiker = SpelContext.findGebruiker(it.id)
          if (gebruiker != null) {
            gebruiker.score = gebruiker.score + it.scoreDezeRonde
          }
          scores.add(SpelerScore(it.naam, it.scoreDezeRonde))
        }
        SpelContext.spelData.uitslagen.add(Uitslag(
          Date().toString(), tafelMetUpdatedScore.tafelNr, scores
        ))
        println("#Uislagen:" + SpelContext.spelData.uitslagen.size)
      } else {// niet einde spel
        tafelMetUpdatedScore.huidigeSpeler = volgendeActieveSpeler(tafelMetUpdatedScore, tafelMetUpdatedScore.findSlagWinnaar())?.id
        tafelMetUpdatedScore.opkomer = tafelMetUpdatedScore.huidigeSpeler
        tafelMetUpdatedScore.slagWinnaar = null
        tafelMetUpdatedScore.toeper = null

//        nieuweRonde(tafel)

        val updatedTafel = nieuweRonde(tafelMetUpdatedScore)
        SpelContext.spelData.updateTafel(updatedTafel)
      }
    } else { // niet de laatste slag
      tafelMetNieuweScore.huidigeSpeler = tafelMetNieuweScore.slagWinnaar
      tafelMetNieuweScore.opkomer = tafelMetNieuweScore.slagWinnaar
      tafelMetNieuweScore.slagWinnaar = null
      tafelMetNieuweScore.toeper = null

      val updatedTafel = tafelMetNieuweScore.copy(
        spelers = tafelMetNieuweScore.spelers.map{if (it.actiefInSpel) SpelerService.nieuweSlag(it) else it}.toMutableList()
      )
      SpelContext.spelData.updateTafel(updatedTafel)

    }
  }

  fun werkScoreBij(tafel: Tafel) {
    val spelersDieAfZijn = tafel.spelers.filter { it.actiefInSpel == false }
    val aantalSpelersDieInSpelZitten = tafel.spelers.filter { it.actiefInSpel == true }
    val aantalSpelersDieInSpelZittenCount = aantalSpelersDieInSpelZitten.size
    val nieuweSpelersDieAfZijn = spelersDieAfZijn.filter { !tafel.spelersDieAfZijn.contains(it.id) }
    val score = 4 - aantalSpelersDieInSpelZittenCount
    nieuweSpelersDieAfZijn.forEach {
      // laad de tafel opnieuw, die kan aangepast zijn
      val tafel3 = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }
      val nieuweSpelersDieAfZijn = tafel3.spelersDieAfZijn.plus(it.id)
      var scoreDezeRonde = 0
      if (score > 0) {
        scoreDezeRonde = score
      }
      val aangepasteSpeler = it.copy(scoreDezeRonde = scoreDezeRonde)
      val updatedTafel = tafel3.updateSpeler(aangepasteSpeler)
      val updatedTafel2 = updatedTafel.copy(spelersDieAfZijn = nieuweSpelersDieAfZijn)
      SpelContext.spelData.updateTafel(updatedTafel2)

    }
    if (aantalSpelersDieInSpelZittenCount == 1) {
      aantalSpelersDieInSpelZitten.forEach {
        val aangepasteSpeler = it.copy(scoreDezeRonde = 5)
        // laad de tafel opnieuw, die kan aangepast zijn
        val tafel3 = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }
        val updatedTafel = tafel3.updateSpeler(aangepasteSpeler)
        SpelContext.spelData.updateTafel(updatedTafel)
      }
    }
  }

  fun zoekSlagWinnaar(tafel: Tafel): Speler? {
    val startKaart = tafel.findOpkomer()?.gespeeldeKaart
    if (startKaart == null) return null
    val winnaar = tafel.spelers.filter { it.actiefInSpel && !it.gepast }.maxBy { it.berekenScore(startKaart) }
    if (winnaar?.gepast ?: false) {
      // oei, diegene die gepast heeft, heeft gewonnen!
      // laat nu de eerste speler winnen die nog in het spel zit
      return tafel.spelers.firstOrNull() { it.actiefInSpel && !it.gepast }
    }
    return winnaar
  }


  fun volgendeActieveSpeler(tafel: Tafel, speler: Speler?): Speler? {
    val actieveSpelers = tafel.spelers.filter { it.actiefInSpel || it == speler }
    if (speler == null) return actieveSpelers.firstOrNull()
    if (!actieveSpelers.contains(speler)) return null
    if (actieveSpelers.last() == speler) return actieveSpelers.firstOrNull()
    val index = actieveSpelers.indexOf(speler)
    return actieveSpelers.get(index + 1)
  }

  fun volgendeSpelerDieMoetToepen(tafel: Tafel, speler: Speler?): Speler? {
    val spelersDieMoetenToepen = tafel.spelers.filter { it.toepKeuze == Toepkeuze.GEEN_KEUZE || it == speler }
    if (spelersDieMoetenToepen.size == 1) return null // er zit maar 1 iemand in, dat is de speler zelf. Geeft dus null terum om aan te geven dat iedereen getoept heeft
    if (speler == null) return spelersDieMoetenToepen.firstOrNull()
    if (!spelersDieMoetenToepen.contains(speler)) return null
    if (spelersDieMoetenToepen.last() == speler) return spelersDieMoetenToepen.firstOrNull()
    val index = spelersDieMoetenToepen.indexOf(speler)
    return spelersDieMoetenToepen.get(index + 1)
  }

  fun volgendeSpelerDieMoetSpelen(tafel: Tafel, speler: Speler?): Speler? {
    val spelersDieMoetenSpelen = tafel.spelers.filter { (it.gespeeldeKaart == null && it.gepast == false && it.actiefInSpel) || it == speler }
    if (speler == null) return spelersDieMoetenSpelen.firstOrNull()
    if (!spelersDieMoetenSpelen.contains(speler)) return null
    if (spelersDieMoetenSpelen.last() == speler) return spelersDieMoetenSpelen.firstOrNull()
    val index = spelersDieMoetenSpelen.indexOf(speler)
    return spelersDieMoetenSpelen.get(index + 1)
  }

  fun nieuweRonde(tafel: Tafel):Tafel {
    val kaarten = Util.getGeschutKaartenDeck()
    return tafel.copy(
      spelers = tafel.spelers.map { speler: Speler ->
        val handKaarten = (1..4).map { kaarten.removeAt(0) }
        SpelerService.nieuweRonde(speler, handKaarten)
      }.toMutableList(),
      inzet = 1
    )
  }

  fun nieuwSpel(tafel: Tafel, startscore: Int): Tafel {
    val newTafel = tafel.copy(
      huidigeSpeler = tafel.spelers.firstOrNull()?.id,
      opkomer = tafel.spelers.firstOrNull()?.id,
      tafelWinnaar = null,
      slagWinnaar = null,
      spelers = tafel.spelers.map { SpelerService.nieuwSpel(it, startscore) }.toMutableList(),
      spelersDieAfZijn = emptyList<String>().toMutableList()
    )
    return nieuweRonde(newTafel)
  }


  fun toep(tafel: Tafel, speler: Speler) {
    if (tafel.toeper == null) tafel.toeper = speler.id // de eerste toeper bewaren

    // set toep keuze voor alle spelers
    tafel.spelers.forEach {
      var toepKeuze =    Toepkeuze.GEEN_KEUZE
      if (!it.actiefInSpel) {
        toepKeuze = Toepkeuze.PAS
      }
      if (it.gepast) {
        toepKeuze = Toepkeuze.PAS
      }
      if (it.totaalLucifers == it.ingezetteLucifers) {
        toepKeuze = Toepkeuze.MEE
      }
      val tafel2 = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }
      val updatedTafel = tafel2.updateSpeler(it.copy(toepKeuze = toepKeuze))
      SpelContext.spelData.updateTafel(updatedTafel)


    }
    // laad de tafel opnieuw, die kan aangepast zijn
    val tafel2 = SpelContext.spelData.tafels.first { it.tafelNr==tafel.tafelNr }

    // set toep keuze op toep voor de toeper
    val updatedSpeler = speler.copy(toepKeuze = Toepkeuze.TOEP)
    val updatedTafel =  tafel2.updateSpeler(updatedSpeler)
    SpelContext.spelData.updateTafel(updatedTafel)

    val volgendeSpelerVoorToep = volgendeSpelerDieMoetToepen(updatedTafel, updatedSpeler)
    if (volgendeSpelerVoorToep == null) {
      updatedTafel.huidigeSpeler = updatedTafel.toeper
      updatedTafel.toeper = null
    } else {
      updatedTafel.huidigeSpeler = volgendeSpelerVoorToep.id
    }

  }
}
