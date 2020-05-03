import java.util.*

object SpelerService {
    fun nieuwSpel(speler:Speler, startscore:Int){
        speler.actiefInSpel = true
        speler.kaarten = emptyList<Kaart>().toMutableList()
        speler.gespeeldeKaart = null
        speler.totaalLucifers = startscore
        speler.ingezetteLucifers = 0
        speler.gepast = false
        speler.toepKeuze = Toepkeuze.GEEN_KEUZE
    }

    fun nieuweRonde(speler:Speler, kaarten: List<Kaart>){
        if (speler.actiefInSpel) {
            speler.kaarten = kaarten.toMutableList()
            speler.gespeeldeKaart = null
            speler.ingezetteLucifers = 1
            speler.gepast = false
            speler.toepKeuze = Toepkeuze.GEEN_KEUZE
        }
    }

    fun nieuweSlag(speler:Speler){
        if (speler.actiefInSpel) {
            speler.gespeeldeKaart = null
            speler.toepKeuze = Toepkeuze.GEEN_KEUZE
        }
    }

    fun speelKaart(speler:Speler, kaart:Kaart):CommandResult{
        val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
        if (tafel==null) return CommandResult(CommandStatus.FAILED,"Je zit niet aan een tafel")
        if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED,"Je bent af")
        if (tafel.toeper!=null) return CommandResult(CommandStatus.FAILED,"Nog niet iedereen heeft zijn toep keuze opgegeven")
        if (tafel.huidigeSpeler!=speler) return CommandResult(CommandStatus.FAILED,"Je bent nog niet aan de beurt om een kaart te spelen")
        if (speler.gespeeldeKaart!=null) return CommandResult(CommandStatus.FAILED,"Je hebt al een kaart gespeeld")
        if (!speler.kaarten.contains(kaart)) return CommandResult(CommandStatus.FAILED,"Deze kaart zit niet in de hand")

        if (tafel.opkomer!=speler){
            val kanBekennen = speler.kaarten.any { it.symbool ==  tafel.opkomer?.gespeeldeKaart?.symbool}
            val zelfdeSymbool = tafel.opkomer?.gespeeldeKaart?.symbool==kaart.symbool
            if (kanBekennen && !zelfdeSymbool){
                return CommandResult(CommandStatus.FAILED,"Je moet bekennen")
            }
        }

        speler.gespeeldeKaart = kaart
        speler.kaarten.remove(kaart)
        TafelService.vervolgSpel(tafel)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun pakSlag(speler:Speler):CommandResult{
        val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
        if (tafel==null) return CommandResult(CommandStatus.FAILED,"Je zit niet aan een tafel")
        if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED,"Je bent af")
        if (tafel.slagWinnaar!=speler) return CommandResult(CommandStatus.FAILED,"Je hebt deze slag niet gewonnen")
        TafelService.eindeSlag(tafel)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun toep(speler:Speler):CommandResult{
        val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
        if (tafel==null) return CommandResult(CommandStatus.FAILED,"Je zit niet aan een tafel")
        if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED,"Je bent af")
        if (tafel.huidigeSpeler!=speler) return CommandResult(CommandStatus.FAILED,"Je bent nog niet aan de beurt om te toepen")
        if (speler.toepKeuze==Toepkeuze.TOEP) return CommandResult(CommandStatus.FAILED,"Je hebt al getoept")
        if (speler.totaalLucifers<=tafel.inzet) return CommandResult(CommandStatus.FAILED,"Je hebt te weinig lucifers om te toepen")
        tafel.inzet++
        speler.ingezetteLucifers = tafel.inzet
        speler.toepKeuze = Toepkeuze.TOEP
        TafelService.toep(tafel, speler)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun gaMeeMetToep(speler:Speler):CommandResult{
        val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
        if (tafel==null) return CommandResult(CommandStatus.FAILED,"Je zit niet aan een tafel")
        if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED,"Je bent af")
        if (tafel.toeper==null) return CommandResult(CommandStatus.FAILED,"Er is niet getoept")
        if (tafel.huidigeSpeler!=speler) return CommandResult(CommandStatus.FAILED,"Je bent nog niet aan de beurt om mee te gaan")
        if (speler.toepKeuze!=Toepkeuze.GEEN_KEUZE) return CommandResult(CommandStatus.FAILED,"Je hebt al een toepkeuze doorgegeven")
        speler.ingezetteLucifers = tafel.inzet
        if (speler.ingezetteLucifers>speler.totaalLucifers) speler.ingezetteLucifers=speler.totaalLucifers
        speler.toepKeuze = Toepkeuze.MEE
        TafelService.vervolgSpel(tafel)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun pas(speler:Speler):CommandResult{
        val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
        if (tafel==null) return CommandResult(CommandStatus.FAILED,"Je zit niet aan een tafel")
        if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED,"Je bent af")
        if (tafel.toeper==null) return CommandResult(CommandStatus.FAILED,"Er is niet getoept")
        if (tafel.huidigeSpeler!=speler) return CommandResult(CommandStatus.FAILED,"Je bent nog niet aan de beurt om te passen")
        if (speler.toepKeuze!=Toepkeuze.GEEN_KEUZE) return CommandResult(CommandStatus.FAILED,"Je hebt al een toepkeuze doorgegeven")
        speler.totaalLucifers -= speler.ingezetteLucifers
        speler.ingezetteLucifers = 0
        speler.toepKeuze = Toepkeuze.PAS
        speler.gepast = true
        TafelService.vervolgSpel(tafel)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun nieuweOpmerking(speler:Speler, opmerking: String):CommandResult{
        val opmerking = Opmerking(speler.naam, Date().toString(),opmerking)
        SpelContext.spelData.opmerkingen.add(opmerking)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

}
