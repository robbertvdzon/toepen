package spellogica

import model.Kaart
import model.Speler
import model.Toepkeuze

object SpelerService {

    fun nieuwSpel(speler: Speler, startscore:Int) = speler.apply {
        actiefInSpel = true
        kaarten = mutableListOf()
        gespeeldeKaart = null
        totaalLucifers = startscore
        ingezetteLucifers = 0
        scoreDezeRonde = 0
        gepast = false
        toepKeuze = Toepkeuze.GEEN_KEUZE
    }

    fun nieuweRonde(speler: Speler, kaarten: List<Kaart>){
        speler.gespeeldeKaart = null
        speler.gepast = false
        speler.toepKeuze = Toepkeuze.GEEN_KEUZE
        if (speler.actiefInSpel) {
            speler.kaarten = kaarten.toMutableList()
            speler.ingezetteLucifers = 1
        }
    }

    fun nieuweSlag(speler: Speler){
        if (speler.actiefInSpel) {
            speler.gespeeldeKaart = null
        }
    }

}
