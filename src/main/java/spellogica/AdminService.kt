package spellogica

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.*
import spelprocessor.CommandQueue
import util.Util
import java.io.File

object AdminService {
  val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  fun loadData(): CommandResult {
    val json = File("speldata.dat").readText(Charsets.UTF_8)
    val spelData = objectMapper.readValue<SpelData>(json, SpelData::class.java)
    SpelContext.spelData = spelData
    CommandQueue.setLastSpeldataJson(objectMapper.writeValueAsString(SpelContext.spelData))
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun saveData(): CommandResult {
    val json = objectMapper.writeValueAsString(SpelContext.spelData)
    File("speldata.dat").writeText(json)
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun maakNieuweTafels(aantalTafels: Int, startscore: Int): CommandResult {
    val spelData = SpelContext.spelData
    val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
    Util.shuffleSpelers(spelersDieMeedoen)
    var tafels = (1..aantalTafels).map { Tafel(it) }
    while (spelersDieMeedoen.isNotEmpty()) {
      tafels = tafels.map {
        if (spelersDieMeedoen.isNotEmpty()) {
          val gebruiker = spelersDieMeedoen.removeAt(0)
          val spelers = it.spelers.plus(Speler(id = gebruiker.id, naam = gebruiker.naam))
          val tafel = it.copy(spelers = spelers.toMutableList())
          tafel
        }
        else {
          it
        }
      }
    }

    spelData.tafels = tafels.toMutableList()
    spelData.tafels.forEach {
      val updatedTafel = TafelService.nieuwSpel(it, startscore)
      SpelContext.spelData.updateTafel(updatedTafel)
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun updateGebruikers(gebruikers: List<Gebruiker>): CommandResult {
    val gebruikersMap = gebruikers.map { it.id to it }.toMap()
    val mutableGebruikersList = gebruikers.toMutableList()
    SpelContext.spelData.alleSpelers.forEach {
      val nieuweSpelerData = gebruikersMap[it.id]
      if (nieuweSpelerData != null) {
        mutableGebruikersList.remove(nieuweSpelerData)
        it.naam = nieuweSpelerData.naam
        it.score = nieuweSpelerData.score
        it.isMonkey = nieuweSpelerData.isMonkey
        it.wilMeedoen = nieuweSpelerData.wilMeedoen
      }
    }
    mutableGebruikersList.forEach {
      SpelContext.spelData.alleSpelers.add(it)
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun clearLog(): CommandResult {
    SpelContext.spelData.uitslagen = emptyList<Uitslag>().toMutableList()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun resetScore(): CommandResult {
    SpelContext.spelData.alleSpelers.forEach {
      it.score = 0
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }


  fun allesPauzeren(): CommandResult {
    SpelContext.spelData.tafels = SpelContext.spelData.tafels.map { it.copy(gepauzeerd = true) }.toMutableList()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun allesStarten(): CommandResult {
    SpelContext.spelData.tafels = SpelContext.spelData.tafels.map { it.copy(gepauzeerd = false) }.toMutableList()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun nieuwSpel(startscore: Int, tafel: Tafel?): CommandResult {
    if (tafel != null) {
      val updatedTafel = TafelService.nieuwSpel(tafel, startscore)
      val pauzedTafel = updatedTafel.copy(
        gepauzeerd = SpelContext.spelData.nieuweTafelAutoPause == true
      )
      SpelContext.spelData.updateTafel(pauzedTafel)
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun schopTafel(tafel: Tafel?): CommandResult {
    if (tafel != null) {
      TafelService.vervolgSpel(tafel)
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun pauzeerTafel(tafel: Tafel?): CommandResult {
    SpelContext.spelData.tafels = SpelContext.spelData.tafels.map {
      if (it.tafelNr==tafel?.tafelNr) it.copy(gepauzeerd = true) else it
    }.toMutableList()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun startTafel(tafel: Tafel?): CommandResult {
    SpelContext.spelData.tafels = SpelContext.spelData.tafels.map {
      if (it.tafelNr==tafel?.tafelNr) it.copy(gepauzeerd = false) else it
    }.toMutableList()
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

}
