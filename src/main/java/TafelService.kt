object TafelService{
    fun vervolgSpel(tafel:Tafel){
        if (tafel.tafelWinnaar!=null) return
        if (tafel.toeper!=null){
            // verwerk toep
            val alleSpelersHebbenToepkeuzeGemaakt = tafel.spelers.all { it.toepKeuze!=Toepkeuze.GEEN_KEUZE }
            if (alleSpelersHebbenToepkeuzeGemaakt){
                tafel.huidigeSpeler = tafel.toeper
                tafel.toeper = null
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
            }
            else{
                tafel.huidigeSpeler = volgendeSpelerDieMoetSpelen(tafel, tafel.huidigeSpeler)
            }

        }
    }

    fun eindeSlag(tafel:Tafel){
        val laatsteSlag = tafel.spelers.firstOrNull()?.kaarten?.size?:0==0
        if (laatsteSlag){
            tafel.spelers.forEach{
                // als je nog in het spel zat, en niet gepast had en niet de winnaar bent, dan ben je lucifers kwijt!
                if (it.actiefInSpel && !it.gepast && tafel.slagWinnaar!=it){
                    it.totaalLucifers-=it.ingezetteLucifers
                    if (it.totaalLucifers==0) it.actiefInSpel = false
                }
            }
            val eindeSpel = tafel.spelers.filter { it.actiefInSpel }.size==1
            if (eindeSpel){
                tafel.tafelWinnaar = tafel.slagWinnaar
                tafel.huidigeSpeler = null
                tafel.slagWinnaar = null
                tafel.toeper = null
            }
            else{// niet einde spel
                tafel.huidigeSpeler = volgendeSpeler(tafel, tafel.slagWinnaar)
                tafel.opkomer = tafel.huidigeSpeler
                tafel.slagWinnaar = null
                tafel.toeper = null
                nieuweRonde(tafel)
            }
        }
        else{ // niet de laatste slag
            tafel.huidigeSpeler = tafel.slagWinnaar
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
        return tafel.spelers.maxBy { it.berekenScore(startKaart) }
    }


    fun volgendeSpeler(tafel: Tafel,speler:Speler?):Speler?{
        if (speler==null) return tafel.spelers.firstOrNull()
        if (!tafel.spelers.contains(speler)) return null
        if (tafel.spelers.last()==speler) return tafel.spelers.firstOrNull()
        val index = tafel.spelers.indexOf(speler)
        return tafel.spelers.get(index+1)
    }

    fun volgendeSpelerDieMoetToepen(tafel: Tafel,speler:Speler?):Speler?{
        val spelersDieMoetenToepen = tafel.spelers.filter { it.toepKeuze==Toepkeuze.GEEN_KEUZE || it==speler}
        if (speler==null) return spelersDieMoetenToepen.firstOrNull()
        if (!spelersDieMoetenToepen.contains(speler)) return null
        if (spelersDieMoetenToepen.last()==speler) return spelersDieMoetenToepen.firstOrNull()
        val index = spelersDieMoetenToepen.indexOf(speler)
        return spelersDieMoetenToepen.get(index+1)
    }

    fun volgendeSpelerDieMoetSpelen(tafel: Tafel,speler:Speler?):Speler?{
        val spelersDieMoetenSpelen = tafel.spelers.filter { it.gespeeldeKaart==null || it.gepast==false || it==speler}
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
    }

    fun nieuwSpel(tafel:Tafel){
        tafel.huidigeSpeler = tafel.spelers.firstOrNull()
        tafel.opkomer = tafel.spelers.firstOrNull()
        tafel.spelers.forEach{SpelerService.nieuweSpel(it)}
        nieuweRonde(tafel)

    }

    fun toep(tafel: Tafel,speler:Speler){
        tafel.toeper = speler
        tafel.spelers.forEach{
            if (!it.actiefInSpel){
                speler.toepKeuze = Toepkeuze.PAS
            }
            if (it.gepast){
                speler.toepKeuze = Toepkeuze.PAS
            }
            if (it.totaalLucifers==it.ingezetteLucifers){
                speler.toepKeuze = Toepkeuze.MEE
            }
        }
        val volgendeSpelerVoorToep = tafel.spelers.firstOrNull { it.toepKeuze==Toepkeuze.GEEN_KEUZE }
        if (volgendeSpelerVoorToep==null){
            tafel.toeper = null
            tafel.huidigeSpeler = speler
        }
        else{
            tafel.huidigeSpeler = volgendeSpelerVoorToep
        }
    }
}
