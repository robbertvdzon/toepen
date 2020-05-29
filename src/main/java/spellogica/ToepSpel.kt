package spellogica

import model.*

object ToepSpel {


  fun speelKaart(speler: Speler, kaart: Kaart): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED, "Je bent af")
    if (tafel.toeper != null) return CommandResult(CommandStatus.FAILED, "Nog niet iedereen heeft zijn toep keuze opgegeven")
    if (tafel.huidigeSpeler != speler.id) return CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen")
    if (speler.gespeeldeKaart != null) return CommandResult(CommandStatus.FAILED, "Je hebt al een kaart gespeeld")
    if (!speler.kaarten.contains(kaart)) return CommandResult(CommandStatus.FAILED, "Deze kaart zit niet in de hand")

    if (tafel.opkomer != speler.id) {
      val kanBekennen = speler.kaarten.any { it.symbool == tafel.findOpkomer()?.gespeeldeKaart?.symbool }
      val zelfdeSymbool = tafel.findOpkomer()?.gespeeldeKaart?.symbool == kaart.symbool
      if (kanBekennen && !zelfdeSymbool) {
        return CommandResult(CommandStatus.FAILED, "Je moet bekennen")
      }
    }

    speler.gespeeldeKaart = kaart
    speler.kaarten.remove(kaart)
    TafelService.vervolgSpel(tafel)
    tafel.log.add("assertThat(SpelerService.speelKaart(speler${speler.id}, model.Kaart(${kaart.symbool},${kaart.waarde}))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun pakSlag(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED, "Je bent af")
    if (tafel.slagWinnaar != speler.id) return CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen")
    TafelService.eindeSlag(tafel)
    tafel.log.add("assertThat(SpelerService.pakSlag(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun toep(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED, "Je bent af")
    if (tafel.huidigeSpeler != speler.id) return CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen")
    if (speler.toepKeuze == Toepkeuze.TOEP) return CommandResult(CommandStatus.FAILED, "Je hebt al getoept")
    if (speler.totaalLucifers <= tafel.inzet) return CommandResult(CommandStatus.FAILED, "Je hebt te weinig lucifers om te toepen")
    tafel.inzet++
    speler.ingezetteLucifers = tafel.inzet
    speler.toepKeuze = Toepkeuze.TOEP
    TafelService.toep(tafel, speler)
    tafel.log.add("assertThat(SpelerService.toep(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun gaMeeMetToep(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED, "Je bent af")
    if (tafel.toeper == null) return CommandResult(CommandStatus.FAILED, "Er is niet getoept")
    if (tafel.huidigeSpeler != speler.id) return CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om mee te gaan")
    if (speler.toepKeuze != Toepkeuze.GEEN_KEUZE) return CommandResult(CommandStatus.FAILED, "Je hebt al een toepkeuze doorgegeven")
    speler.ingezetteLucifers = tafel.inzet
    if (speler.ingezetteLucifers > speler.totaalLucifers) speler.ingezetteLucifers = speler.totaalLucifers
    speler.toepKeuze = Toepkeuze.MEE
    TafelService.vervolgSpel(tafel)
    tafel.log.add("assertThat(SpelerService.gaMeeMetToep(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun pas(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED, "Je bent af")
    if (tafel.toeper == null) return CommandResult(CommandStatus.FAILED, "Er is niet getoept")
    if (tafel.huidigeSpeler != speler.id) return CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te passen")
    if (speler.toepKeuze != Toepkeuze.GEEN_KEUZE) return CommandResult(CommandStatus.FAILED, "Je hebt al een toepkeuze doorgegeven")
    speler.totaalLucifers -= speler.ingezetteLucifers
    speler.ingezetteLucifers = 0
    speler.toepKeuze = Toepkeuze.PAS
    speler.gepast = true
    TafelService.vervolgSpel(tafel)
    tafel.log.add("assertThat(SpelerService.pas(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
    return CommandResult(CommandStatus.SUCCEDED, "")
  }


}
