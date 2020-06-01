package spellogica

import io.vavr.control.Either
import model.*
import util.Util
import util.Util.bind

object ToepSpel {

  // TODO: Hier overal een Either<String, SpelData> terug geven, met een aangepaste SpelData terug als goed
  // TODO: Hier overal een spelData meegeven


  fun speelKaart(speler: Speler, kaart: Kaart, spelData: SpelData): Either<String, SpelData> {

    val tafel = spelData.tafels.find { it.spelers.contains(speler) }
    if (tafel == null) return Either.left( "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left( "De tafel is gepauzeerd")
    val tafelNr = tafel.tafelNr

    val result = Util.eitherBlock<String, SpelData> {
      val newSpelData = SpelerService.speelKaart(speler, kaart, tafel, spelData).bind()
      SpelContext.spelData = newSpelData
      val newnewSpelData = TafelService.vervolgSpel(spelData.findTafel(tafelNr),newSpelData)
      SpelContext.spelData = newnewSpelData
      newnewSpelData
    }
    return result
  }

  fun pakSlag(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return CommandResult(CommandStatus.FAILED, "Je bent af")
    if (tafel.slagWinnaar != speler.id) return CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen")
    val newnewSpelData = TafelService.eindeSlag(tafel, SpelContext.spelData)
    SpelContext.spelData = newnewSpelData

    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun toep(speler: Speler): CommandResult {
    val tafel = SpelContext.spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return CommandResult(CommandStatus.FAILED, "De tafel is gepauzeerd")
    val nieuweInzet = tafel.inzet+1

    val result = Util.eitherBlock<String, CommandResult> {
      val updatedSpeler = SpelerService.toep(speler, tafel, nieuweInzet).bind()
      val updatedTafel = tafel.updateSpeler(updatedSpeler).copy(inzet = nieuweInzet)
      val (newSpelData,newTafel ) = SpelContext.spelData.updateTafel(updatedTafel)
      SpelContext.spelData = newSpelData
      val newnewSpelData = TafelService.toep(newSpelData, newTafel, updatedSpeler)
      SpelContext.spelData = newnewSpelData
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
      val updatedTafel = tafel.updateSpeler(updatedSpeler)
      val (newSpelData,newTafel ) = SpelContext.spelData.updateTafel(updatedTafel)
      SpelContext.spelData = newSpelData
      val newnewSpelData = TafelService.vervolgSpel(updatedTafel, newSpelData)
      SpelContext.spelData = newnewSpelData

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

    val result = Util.eitherBlock<String, CommandResult> {
      val updatedSpeler = SpelerService.pas(speler, tafel).bind()
      val updatedTafel = tafel.updateSpeler(updatedSpeler)
      val (newSpelData,newTafel ) = SpelContext.spelData.updateTafel(updatedTafel)
      val newnewSpelData = TafelService.vervolgSpel(updatedTafel, newSpelData)
      SpelContext.spelData = newnewSpelData
      CommandResult(CommandStatus.SUCCEDED, "")
    }
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    else return result.get()
  }


}
