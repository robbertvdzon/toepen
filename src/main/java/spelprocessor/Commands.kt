package spelprocessor

import com.fasterxml.jackson.annotation.JsonIgnore
import model.*
import spellogica.AdminService
import spellogica.ToepSpel
import util.Util

abstract class Command {
  @JsonIgnore
  val lock = Object()

  @JsonIgnore
  var result: CommandResult? = null
  abstract fun process(): CommandResult
}

class SpeelKaartCommand(val spelerId: String, val kaart: Kaart) : Command() {
  override fun process(): CommandResult {
    val speler = SpelContext.spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.speelKaart(speler, kaart, SpelContext.spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    SpelContext.spelData = result.get()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class PakSlagCommand(val spelerId: String) : Command() {
  override fun process(): CommandResult {
    val speler = SpelContext.spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.pakSlag(speler, SpelContext.spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    SpelContext.spelData = result.get()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class ToepCommand(val spelerId: String) : Command() {
  override fun process(): CommandResult {
    val speler = SpelContext.spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.toep(speler, SpelContext.spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    SpelContext.spelData = result.get()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class GaMeeMetToepCommand(val spelerId: String) : Command() {
  override fun process(): CommandResult {
    val speler = SpelContext.spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.gaMeeMetToep(speler, SpelContext.spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    SpelContext.spelData = result.get()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class PasCommand(val spelerId: String) : Command() {
  override fun process(): CommandResult {
    val speler = SpelContext.spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.pas(speler, SpelContext.spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    SpelContext.spelData = result.get()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class LoadDataCommand() : Command() {
  override fun process(): CommandResult = AdminService.loadData()
}

class SaveDataCommand() : Command() {
  override fun process(): CommandResult = AdminService.saveData()
}

class MaakNieuweTafelsCommand(val aantal: Int, val startscore: Int) : Command() {
  override fun process(): CommandResult = AdminService.maakNieuweTafels(aantal, startscore)
}

class UpdateGebruikersCommand(val gebruikers: List<Gebruiker>) : Command() {
  override fun process(): CommandResult = AdminService.updateGebruikers(gebruikers)
}

class ClearLog() : Command() {
  override fun process(): CommandResult = AdminService.clearLog()
}

class ResetScore() : Command() {
  override fun process(): CommandResult = AdminService.resetScore()
}

class AllesPauzeren() : Command() {
  override fun process(): CommandResult = AdminService.allesPauzeren()
}

class AllesStarten() : Command() {
  override fun process(): CommandResult = AdminService.allesStarten()
}

class NieuwSpel(val startscore: Int, val tafelNr: Int?) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    return AdminService.nieuwSpel(startscore, tafel)

  }
}

class SchopTafel(val tafelNr: Int) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    return AdminService.schopTafel(tafel)
  }
}

class PauzeerTafel(val tafelNr: Int) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    return AdminService.pauzeerTafel(tafel)
  }
}

class StartTafel(val tafelNr: Int) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    return AdminService.startTafel(tafel)
  }
}

class SetRandomSeed(val seed: Long) : Command() {
  override fun process(): CommandResult {
    Util.setSeed(seed)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}


