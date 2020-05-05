object TafelService{
    fun vervolgSpel(tafel:Tafel){
        if (tafel.tafelWinnaar!=null) return
        if (tafel.toeper!=null){
            // verwerk toep
            val alleSpelersHebbenToepkeuzeGemaakt = tafel.spelers.all { it.toepKeuze!=Toepkeuze.GEEN_KEUZE }
            if (alleSpelersHebbenToepkeuzeGemaakt){
                val iedereenGepast = tafel.spelers.none { it.toepKeuze==Toepkeuze.MEE }
                if (iedereenGepast){
                    // einde deze ronde!
                    tafel.slagWinnaar = tafel.spelers.firstOrNull { it.toepKeuze==Toepkeuze.TOEP }
                    tafel.huidigeSpeler = tafel.slagWinnaar
                    eindeSlag(tafel)
                }
                else{
                    tafel.huidigeSpeler = tafel.toeper
                    tafel.toeper = null
                }
            }
            else{
                tafel.huidigeSpeler = volgendeSpelerDieMoetToepen(tafel, tafel.huidigeSpeler)
            }
        }
        else{
            // verwerk slag
            val alleSpelersHebbenKaartIngezet = tafel.spelers.all { it.gespeeldeKaart!=null || !it.actiefInSpel || it.gepast}
            if (alleSpelersHebbenKaartIngezet){
                tafel.slagWinnaar = zoekSlagWinnaar(tafel)
                tafel.huidigeSpeler = tafel.slagWinnaar
            }
            else{
                tafel.huidigeSpeler = volgendeSpelerDieMoetSpelen(tafel, tafel.huidigeSpeler)
            }

        }
    }

    fun eindeSlag(tafel:Tafel){
        val laatsteSlag = tafel.spelers.firstOrNull{it.gepast==false && it.actiefInSpel}?.kaarten?.size?:0==0
        val aantalSpelersDezeRonde = tafel.spelers.filter{it.gepast==false && it.actiefInSpel}.size
        if (laatsteSlag||aantalSpelersDezeRonde<2){
            tafel.spelers.forEach{
                // als je nog in het spel zat, en niet gepast had en niet de winnaar bent, dan ben je lucifers kwijt!
                if (it.actiefInSpel && !it.gepast && tafel.slagWinnaar!=it){
                    it.totaalLucifers-=it.ingezetteLucifers
                    if (it.totaalLucifers==0) it.actiefInSpel = false
                }
                it.ingezetteLucifers = 0
            }
            val eindeSpel = tafel.spelers.filter { it.actiefInSpel }.size==1
            if (eindeSpel){
                tafel.tafelWinnaar = tafel.slagWinnaar
                tafel.huidigeSpeler = null
                tafel.slagWinnaar = null
                tafel.toeper = null
                tafel.tafelWinnaar?.score = 1+(tafel.tafelWinnaar?.score?:0)
            }
            else{// niet einde spel
                tafel.huidigeSpeler = volgendeActieveSpeler(tafel, tafel.slagWinnaar)
                tafel.opkomer = tafel.huidigeSpeler
                tafel.slagWinnaar = null
                tafel.toeper = null
                nieuweRonde(tafel)
            }
        }
        else{ // niet de laatste slag
            tafel.huidigeSpeler = tafel.slagWinnaar
            tafel.opkomer = tafel.slagWinnaar
            tafel.slagWinnaar = null
            tafel.toeper = null
            tafel.spelers.filter { it.actiefInSpel }.forEach{
                SpelerService.nieuweSlag(it)
            }
        }
    }

    fun zoekSlagWinnaar(tafel:Tafel):Speler?{
        val startKaart = tafel.opkomer?.gespeeldeKaart
        if (startKaart==null) return null
        val winnaar = tafel.spelers.filter { it.actiefInSpel && !it.gepast}.maxBy { it.berekenScore(startKaart) }
        if (winnaar?.gepast?:false){
            // oei, diegene die gepast heeft, heeft gewonnen!
            // laat nu de eerste speler winnen die nog in het spel zit
            return tafel.spelers.firstOrNull() { it.actiefInSpel && !it.gepast}
        }
        return winnaar
    }


    fun volgendeActieveSpeler(tafel: Tafel, speler:Speler?):Speler?{
        val actieveSpelers = tafel.spelers.filter { it.actiefInSpel || it==speler}
        if (speler==null) return actieveSpelers.firstOrNull()
        if (!actieveSpelers.contains(speler)) return null
        if (actieveSpelers.last()==speler) return actieveSpelers.firstOrNull()
        val index = actieveSpelers.indexOf(speler)
        return actieveSpelers.get(index+1)
    }

    fun volgendeSpelerDieMoetToepen(tafel: Tafel,speler:Speler?):Speler?{
        val spelersDieMoetenToepen = tafel.spelers.filter { it.toepKeuze==Toepkeuze.GEEN_KEUZE || it==speler}
        if (spelersDieMoetenToepen.size==1) return null // er zit maar 1 iemand in, dat is de speler zelf. Geeft dus null terum om aan te geven dat iedereen getoept heeft
        if (speler==null) return spelersDieMoetenToepen.firstOrNull()
        if (!spelersDieMoetenToepen.contains(speler)) return null
        if (spelersDieMoetenToepen.last()==speler) return spelersDieMoetenToepen.firstOrNull()
        val index = spelersDieMoetenToepen.indexOf(speler)
        return spelersDieMoetenToepen.get(index+1)
    }

    fun volgendeSpelerDieMoetSpelen(tafel: Tafel,speler:Speler?):Speler?{
        val spelersDieMoetenSpelen = tafel.spelers.filter { (it.gespeeldeKaart==null && it.gepast==false && it.actiefInSpel) || it==speler}
        if (speler==null) return spelersDieMoetenSpelen.firstOrNull()
        if (!spelersDieMoetenSpelen.contains(speler)) return null
        if (spelersDieMoetenSpelen.last()==speler) return spelersDieMoetenSpelen.firstOrNull()
        val index = spelersDieMoetenSpelen.indexOf(speler)
        return spelersDieMoetenSpelen.get(index+1)
    }

    fun nieuweRonde(tafel:Tafel){
        val kaarten = Util.getGeschutKaartenDeck()
        tafel.spelers.forEach{speler:Speler ->
            val kaarten =  (1..4).map {kaarten.removeAt(0)}
            SpelerService.nieuweRonde(speler, kaarten )
        }
        tafel.inzet = 1
    }

    fun nieuwSpel(tafel:Tafel, startscore:Int){
        tafel.huidigeSpeler = tafel.spelers.firstOrNull()
        tafel.opkomer = tafel.spelers.firstOrNull()
        tafel.spelers.forEach{SpelerService.nieuwSpel(it, startscore)}
        nieuweRonde(tafel)

    }

    fun toep(tafel: Tafel,speler:Speler){
        if (tafel.toeper==null) tafel.toeper = speler // de eerste toeper bewaren
        tafel.spelers.forEach{
            if (it.actiefInSpel){
                it.toepKeuze = Toepkeuze.GEEN_KEUZE
            }
            if (!it.actiefInSpel){
                it.toepKeuze = Toepkeuze.PAS
            }
            if (it.gepast){
                it.toepKeuze = Toepkeuze.PAS
            }
            if (it.totaalLucifers==it.ingezetteLucifers){
                it.toepKeuze = Toepkeuze.MEE
            }
        }
        speler.toepKeuze = Toepkeuze.TOEP
        val volgendeSpelerVoorToep = volgendeSpelerDieMoetToepen(tafel, speler)
        if (volgendeSpelerVoorToep==null){
            tafel.toeper = null
            tafel.huidigeSpeler = speler
        }
        else{
            tafel.huidigeSpeler = volgendeSpelerVoorToep
        }

    }
}
