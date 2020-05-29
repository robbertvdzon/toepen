package spellogica

import model.SpelContext
import model.Speler
import model.SpelerScore
import model.Tafel
import Toepen
import model.Toepkeuze
import model.Uitslag
import util.Util
import java.util.*

object TafelService {

    fun vervolgSpel(tafel: Tafel) {
        werkScoreBij(tafel)
        if (tafel.tafelWinnaar != null) return
        if (tafel.toeper != null) {
            // verwerk toep
            val alleSpelersHebbenToepkeuzeGemaakt = tafel.spelers.all { it.toepKeuze != Toepkeuze.GEEN_KEUZE }
            if (alleSpelersHebbenToepkeuzeGemaakt) {
                val iedereenGepast = tafel.spelers.none { it.toepKeuze == Toepkeuze.MEE }
                if (iedereenGepast) {
                    // einde deze ronde!
                    tafel.slagWinnaar = tafel.spelers.firstOrNull { it.toepKeuze == Toepkeuze.TOEP }?.id
                    tafel.huidigeSpeler = tafel.slagWinnaar
                    eindeSlag(tafel)
                } else {
                    tafel.huidigeSpeler = tafel.toeper
                    tafel.toeper = null
                }
            } else {
                tafel.huidigeSpeler = volgendeSpelerDieMoetToepen(tafel, tafel.findHuidigeSpeler())?.id
            }
        } else {
            // verwerk slag
            val alleSpelersHebbenKaartIngezet = tafel.spelers.all { it.gespeeldeKaart != null || !it.actiefInSpel || it.gepast }
            if (alleSpelersHebbenKaartIngezet) {
                tafel.slagWinnaar = zoekSlagWinnaar(tafel)?.id
                tafel.huidigeSpeler = tafel.slagWinnaar
            } else {
                tafel.huidigeSpeler = volgendeSpelerDieMoetSpelen(tafel, tafel.findHuidigeSpeler())?.id
            }

        }
    }

    fun eindeSlag(tafel: Tafel) {
        Toepen.broadcastSlagWinnaar(tafel)
        werkScoreBij(tafel)

        val laatsteSlag = tafel.spelers.firstOrNull { it.gepast == false && it.actiefInSpel }?.kaarten?.size ?: 0 == 0
        val aantalSpelersDezeRonde = tafel.spelers.filter { it.gepast == false && it.actiefInSpel }.size
        if (laatsteSlag || aantalSpelersDezeRonde < 2) {
            Toepen.broadcastRondeWinnaar(tafel)

            tafel.spelers.forEach {
                // als je nog in het spel zat, en niet gepast had en niet de winnaar bent, dan ben je lucifers kwijt!
                if (it.actiefInSpel && !it.gepast && tafel.slagWinnaar != it.id) {
                    it.totaalLucifers -= it.ingezetteLucifers
                    if (it.totaalLucifers == 0) it.actiefInSpel = false
                }
                it.ingezetteLucifers = 0
            }
            val eindeSpel = tafel.spelers.filter { it.actiefInSpel }.size == 1
            werkScoreBij(tafel)
            if (eindeSpel) {// einde spel
                Toepen.broadcastSpelWinnaar(tafel)
                tafel.tafelWinnaar = tafel.slagWinnaar
                tafel.huidigeSpeler = null
                tafel.slagWinnaar = null
                tafel.toeper = null

                var scores: MutableList<SpelerScore> = emptyList<SpelerScore>().toMutableList()
                tafel.spelers.forEach {
                    val gebruiker = SpelContext.findGebruiker(it.id)
                    if (gebruiker!=null){
                        gebruiker.score = gebruiker.score + it.scoreDezeRonde
                    }
                    scores.add(SpelerScore(it.naam, it.scoreDezeRonde))
                }
                SpelContext.spelData.uitslagen.add(Uitslag(
                        Date().toString(), tafel.tafelNr, scores
                ))
                println("#Uislagen:"+ SpelContext.spelData.uitslagen.size)
            } else {// niet einde spel
                tafel.huidigeSpeler = volgendeActieveSpeler(tafel, tafel.findSlagWinnaar())?.id
                tafel.opkomer = tafel.huidigeSpeler
                tafel.slagWinnaar = null
                tafel.toeper = null
                nieuweRonde(tafel)

                tafel.spelers.forEach { speler: Speler ->
                    tafel.log.add("speler${speler.id}.clear()")
                    speler.kaarten.forEach {
                        tafel.log.add("speler${speler.id}.kaarten.add(model.Kaart(${it.symbool}, ${it.waarde}))")
                    }
                }


            }
        } else { // niet de laatste slag
            tafel.huidigeSpeler = tafel.slagWinnaar
            tafel.opkomer = tafel.slagWinnaar
            tafel.slagWinnaar = null
            tafel.toeper = null
            tafel.spelers.filter { it.actiefInSpel }.forEach {
                ToepSpel.nieuweSlag(it)
            }
        }
    }

    fun werkScoreBij(tafel: Tafel) {
        val spelersDieAfZijn = tafel.spelers.filter { it.actiefInSpel == false }
        val aantalSpelersDieInSpelZitten = tafel.spelers.filter { it.actiefInSpel == true }
        val aantalSpelersDieInSpelZittenCount = aantalSpelersDieInSpelZitten.size
        val nieuweSpelersDieAfZijn = spelersDieAfZijn.filter { !tafel.spelersDieAfZijn.contains(it.id) }
        var score = 4 - aantalSpelersDieInSpelZittenCount
        nieuweSpelersDieAfZijn.forEach {
            tafel.spelersDieAfZijn.add(it.id)
            if (score>0) {
                it.scoreDezeRonde = score
            }

        }
        if (aantalSpelersDieInSpelZittenCount == 1) {
            aantalSpelersDieInSpelZitten.forEach {
                it.scoreDezeRonde = 5 // de winnaar!
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

    fun nieuweRonde(tafel: Tafel) {
        val kaarten = Util.getGeschutKaartenDeck()
        tafel.spelers.forEach { speler: Speler ->
            val handKaarten = (1..4).map { kaarten.removeAt(0) }
            ToepSpel.nieuweRonde(speler, handKaarten)
        }
        tafel.inzet = 1
    }

    fun nieuwSpel(tafel: Tafel, startscore: Int) {
        tafel.huidigeSpeler = tafel.spelers.firstOrNull()?.id
        tafel.opkomer = tafel.spelers.firstOrNull()?.id
        tafel.tafelWinnaar = null
        tafel.slagWinnaar = null
        tafel.spelers.forEach { ToepSpel.nieuwSpel(it, startscore) }
        tafel.spelersDieAfZijn = emptyList<String>().toMutableList()
        nieuweRonde(tafel)
        logNieuwSpel(tafel)

    }

    private fun logNieuwSpel(tafel: Tafel) {
        tafel.log.clear()
        var spelernr: Int = 0
        tafel.spelers.forEach { speler: Speler ->
            spelernr++
            tafel.log.add("val speler${speler.id} = maakSpeler(\"speler.naam\", \"${spelernr}\")")
            tafel.log.add("val speler$spelernr =  speler${speler.id}")
            tafel.log.add("var speler${speler.id}kaarten: MutableList<model.Kaart> = emptyList<model.Kaart>().toMutableList()")
            speler.kaarten.forEach {
                tafel.log.add("speler${speler.id}kaarten.add(model.Kaart(${it.symbool}, ${it.waarde}))")
            }
            tafel.log.add("SpelerService.nieuwSpel(speler$spelernr, 5)")
            tafel.log.add("SpelerService.nieuweRonde(speler$spelernr, speler${speler.id}kaarten)")
        }
        tafel.log.add("val tafel = model.Tafel(1)")
        tafel.log.add("tafel.spelers = listOf(speler1, speler2, speler3, speler4).toMutableList()")
        tafel.log.add("tafel.opkomer = speler1")
        tafel.log.add("tafel.huidigeSpeler = speler1")
        tafel.log.add("tafel.inzet = 1")
        tafel.log.add("model.SpelContext.spelData.tafels = listOf(tafel).toMutableList()")
    }

    fun toep(tafel: Tafel, speler: Speler) {
        if (tafel.toeper == null) tafel.toeper = speler.id // de eerste toeper bewaren
        tafel.spelers.forEach {
            if (it.actiefInSpel) {
                it.toepKeuze = Toepkeuze.GEEN_KEUZE
            }
            if (!it.actiefInSpel) {
                it.toepKeuze = Toepkeuze.PAS
            }
            if (it.gepast) {
                it.toepKeuze = Toepkeuze.PAS
            }
            if (it.totaalLucifers == it.ingezetteLucifers) {
                it.toepKeuze = Toepkeuze.MEE
            }
        }
        speler.toepKeuze = Toepkeuze.TOEP
        val volgendeSpelerVoorToep = volgendeSpelerDieMoetToepen(tafel, speler)
        if (volgendeSpelerVoorToep == null) {
            tafel.huidigeSpeler = tafel.toeper
            tafel.toeper = null
        } else {
            tafel.huidigeSpeler = volgendeSpelerVoorToep.id
        }

    }
}
