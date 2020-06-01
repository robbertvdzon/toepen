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
  override fun process(): CommandResult {
    val result = AdminService.loadData()
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    SpelContext.spelData = result.get()
    CommandQueue.setLastSpeldataJson(AdminService.objectMapper.writeValueAsString(SpelContext.spelData))
    return CommandResult(CommandStatus.SUCCEDED, "")

  }
}

class SaveDataCommand() : Command() {
  override fun process(): CommandResult {
    AdminService.saveData(SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class MaakNieuweTafelsCommand(val aantal: Int, val startscore: Int) : Command() {
  override fun process(): CommandResult {
    SpelContext.spelData = AdminService.maakNieuweTafels(aantal, startscore, SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class UpdateGebruikersCommand(val gebruikers: List<Gebruiker>) : Command() {
  override fun process(): CommandResult {
    SpelContext.spelData = AdminService.updateGebruikers(gebruikers, SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class ClearLog() : Command() {
  override fun process(): CommandResult {
    SpelContext.spelData = AdminService.clearLog(SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class ResetScore() : Command() {
  override fun process(): CommandResult {
    SpelContext.spelData = AdminService.resetScore(SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class AllesPauzeren() : Command() {
  override fun process(): CommandResult {
    SpelContext.spelData = AdminService.allesPauzeren(SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class AllesStarten() : Command() {
  override fun process(): CommandResult {
    SpelContext.spelData = AdminService.allesStarten(SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class NieuwSpel(val startscore: Int, val tafelNr: Int?) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    SpelContext.spelData = AdminService.nieuwSpel(startscore, tafel, SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class SchopTafel(val tafelNr: Int) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    SpelContext.spelData = AdminService.schopTafel(tafel, SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class PauzeerTafel(val tafelNr: Int) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    SpelContext.spelData = AdminService.pauzeerTafel(tafel, SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class StartTafel(val tafelNr: Int) : Command() {
  override fun process(): CommandResult {
    val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    SpelContext.spelData = AdminService.startTafel(tafel, SpelContext.spelData)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}

class SetRandomSeed(val seed: Long) : Command() {
  override fun process(): CommandResult {
    Util.setSeed(seed)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }
}


