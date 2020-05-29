package spellogica

import model.*
import util.Util
import util.Util.bind

object ToepSpel {

  // TODO: Hier overal een Either<String, Tafel> terug geven, met een aangepaste tafel terug als goed
  // TODO: Hier overal een spelData meegeven
  fun speelKaart(speler: Speler, kaart: Kaart): CommandResult {

    val tafel = SpelContext.spelData.tafels.find { it.spelers.contains(speler) }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")

    val result = Util.eitherBlock<String, CommandResult> {
      val updatedSpeler = SpelerService.speelKaart(speler, kaart, tafel).bind()
      tafel.updateSpeler(updatedSpeler)
      TafelService.vervolgSpel(tafel)
      tafel.log.add("assertThat(SpelerService.speelKaart(speler${speler.id}, model.Kaart(${kaart.symbool},${kaart.waarde}))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
      CommandResult(CommandStatus.SUCCEDED, "")
    }

    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    else return result.get()
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
    val nieuweInzet = tafel.inzet+1

    val result = Util.eitherBlock<String, CommandResult> {
      val updatedSpeler = SpelerService.toep(speler, tafel, nieuweInzet).bind()
      tafel.updateSpeler(updatedSpeler)
      tafel.inzet = nieuweInzet
      TafelService.toep(tafel, updatedSpeler)
      tafel.log.add("assertThat(SpelerService.toep(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
      CommandResult(CommandStatus.SUCCEDED, "")
    }

    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    else return result.get()
  }

  fun gaMeeMetToep(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")

    val result = Util.eitherBlock<String, CommandResult> {
      val updatedSpeler = SpelerService.gaMeeMetToep(speler, tafel).bind()
      tafel.updateSpeler(updatedSpeler)
      TafelService.vervolgSpel(tafel)
      tafel.log.add("assertThat(SpelerService.gaMeeMetToep(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
      CommandResult(CommandStatus.SUCCEDED, "")
    }
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    else return result.get()

  }

  fun pas(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (tafel.toeper == null) return CommandResult(CommandStatus.FAILED, "Er is niet getoept")
    if (tafel.huidigeSpeler != speler.id) return CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te passen")


    val result = Util.eitherBlock<String, CommandResult> {
      val updatedSpeler = SpelerService.pas(speler, tafel).bind()
      tafel.updateSpeler(updatedSpeler)
      TafelService.vervolgSpel(tafel)
      tafel.log.add("assertThat(SpelerService.pas(speler${speler.id})).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, \"\"))")
      CommandResult(CommandStatus.SUCCEDED, "")
    }
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    else return result.get()
  }


}
