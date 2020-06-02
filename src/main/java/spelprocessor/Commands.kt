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
  abstract fun process(spelData:SpelData): CommandResult
}

class SpeelKaartCommand(val spelerId: String, val kaart: Kaart) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val speler = spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.speelKaart(speler, kaart, spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    return CommandResult(CommandStatus.SUCCEDED, "", result.get())
  }
}

class PakSlagCommand(val spelerId: String) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val speler = spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.pakSlag(speler, spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    return CommandResult(CommandStatus.SUCCEDED, "", result.get())
  }
}

class ToepCommand(val spelerId: String) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val speler = spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.toep(speler, spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    return CommandResult(CommandStatus.SUCCEDED, "", result.get())
  }
}

class GaMeeMetToepCommand(val spelerId: String) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val speler = spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.gaMeeMetToep(speler, spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    return CommandResult(CommandStatus.SUCCEDED, "", result.get())
  }
}

class PasCommand(val spelerId: String) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val speler = spelData.findSpeler(spelerId)
    if (speler == null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
    val result = ToepSpel.pas(speler, spelData)
    if (result.isLeft){
      return CommandResult(CommandStatus.FAILED, result.left)
    }
    return CommandResult(CommandStatus.SUCCEDED, "", result.get())
  }
}

class LoadDataCommand() : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.loadData()
    CommandQueue.lastSpelData = newSpelData
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)

  }
}

class SaveDataCommand() : Command() {
  override fun process(spelData:SpelData): CommandResult {
    AdminService.saveData(spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", spelData)
  }
}

class MaakNieuweTafelsCommand(val aantal: Int, val startscore: Int) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.maakNieuweTafels(aantal, startscore, spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class UpdateGebruikersCommand(val gebruikers: List<Gebruiker>) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.updateGebruikers(gebruikers, spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class ClearLog() : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.clearLog(spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class ResetScore() : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.resetScore(spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class AllesPauzeren() : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.allesPauzeren(spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class AllesStarten() : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val newSpelData = AdminService.allesStarten(spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class NieuwSpel(val startscore: Int, val tafelNr: Int?) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val tafel = spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    val newSpelData = AdminService.nieuwSpel(startscore, tafel, spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class PauzeerTafel(val tafelNr: Int) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val tafel = spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    val newSpelData = AdminService.pauzeerTafel(tafel, spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class StartTafel(val tafelNr: Int) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    val tafel = spelData.tafels.firstOrNull { it.tafelNr == tafelNr }
    val newSpelData = AdminService.startTafel(tafel, spelData)
    return CommandResult(CommandStatus.SUCCEDED, "", newSpelData)
  }
}

class SetRandomSeed(val seed: Long) : Command() {
  override fun process(spelData:SpelData): CommandResult {
    Util.setSeed(seed)
    return CommandResult(CommandStatus.SUCCEDED, "", spelData)
  }
}


