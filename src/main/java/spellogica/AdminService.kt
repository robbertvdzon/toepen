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
    val spelersDieMeedoen = SpelContext.spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
    Util.shuffleSpelers(spelersDieMeedoen)
    var tafels = (1..aantalTafels).map { Tafel(it) }
    while (spelersDieMeedoen.isNotEmpty()) {
      tafels = tafels.map {
        if (spelersDieMeedoen.isNotEmpty()) {
          val gebruiker = spelersDieMeedoen.removeAt(0)
          val spelers = it.spelers.plus(Speler(id = gebruiker.id, naam = gebruiker.naam))
          val tafel = it.copy(spelers = spelers.toMutableList())
          tafel
        } else {
          it
        }
      }
    }

    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        tafels = tafels.toMutableList()
      )
    )
    SpelContext.spelData.tafels.forEach {
      val updatedTafel = TafelService.nieuwSpel(it, startscore)
      val (newSpelData,newTafel ) = SpelContext.spelData.updateTafel(updatedTafel)
      SpelContext.spelData = newSpelData
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
        val (updatedSpelData, _) = SpelContext.spelData.updateGebruiker(
          it.copy(
            naam = nieuweSpelerData.naam,
            score = nieuweSpelerData.score,
            isMonkey = nieuweSpelerData.isMonkey,
            wilMeedoen = nieuweSpelerData.wilMeedoen
          )
        )
       SpelContext.spelData = updatedSpelData
      }
    }
    val nieuweSpelers = SpelContext.spelData.alleSpelers.plus(mutableGebruikersList)
    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        alleSpelers = nieuweSpelers
      )
    )
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun clearLog(): CommandResult {
    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        uitslagen = emptyList<Uitslag>().toMutableList()
      )
    )
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun resetScore(): CommandResult {
    SpelContext.spelData.alleSpelers.forEach {
      val (updatedSpelData, _) = SpelContext.spelData.updateGebruiker(
        it.copy(
          score = 0
        )
      )
      SpelContext.spelData = updatedSpelData
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }


  fun allesPauzeren(): CommandResult {
    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        tafels = SpelContext.spelData.tafels.map { it.copy(gepauzeerd = true) }.toMutableList()
      )
    )

    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun allesStarten(): CommandResult {
    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        tafels = SpelContext.spelData.tafels.map { it.copy(gepauzeerd = false) }.toMutableList()
      )
    )

    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun nieuwSpel(startscore: Int, tafel: Tafel?): CommandResult {
    if (tafel != null) {
      val updatedTafel = TafelService.nieuwSpel(tafel, startscore)
      val pauzedTafel = updatedTafel.copy(
        gepauzeerd = SpelContext.spelData.nieuweTafelAutoPause == true
      )
      val (newSpelData,newTafel ) = SpelContext.spelData.updateTafel(pauzedTafel)
      SpelContext.spelData = newSpelData

    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun schopTafel(tafel: Tafel?): CommandResult {
    if (tafel != null) {
      TafelService.vervolgSpel(tafel, SpelContext.spelData)
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun pauzeerTafel(tafel: Tafel?): CommandResult {
    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        tafels = SpelContext.spelData.tafels.map {
          if (it.tafelNr == tafel?.tafelNr) it.copy(gepauzeerd = true) else it
        }.toMutableList()
      )
    )


    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun startTafel(tafel: Tafel?): CommandResult {
    SpelContext.updateSpelData(
      SpelContext.spelData.copy(
        tafels = SpelContext.spelData.tafels.map {
          if (it.tafelNr == tafel?.tafelNr) it.copy(gepauzeerd = false) else it
        }.toMutableList()
      )
    )
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

}
