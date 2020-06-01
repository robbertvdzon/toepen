package spellogica

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vavr.control.Either
import model.*
import spelprocessor.CommandQueue
import util.Util
import java.io.File

object AdminService {
  val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  fun loadData(): Either<String, SpelData> {
    val json = File("speldata.dat").readText(Charsets.UTF_8)
    val spelData = objectMapper.readValue<SpelData>(json, SpelData::class.java)
    return Either.right(spelData)
  }

  fun saveData(spelData:SpelData) {
    val json = objectMapper.writeValueAsString(spelData)
    File("speldata.dat").writeText(json)
  }

  fun maakNieuweTafels(aantalTafels: Int, startscore: Int, spelDataX:SpelData): SpelData {
    var spelData = spelDataX
    val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
    Util.shuffleSpelers(spelersDieMeedoen)
    var tafels = (1..aantalTafels).map { Tafel(
      tafelNr = it,
      gepauzeerd = spelData.nieuweTafelAutoPause == true
    ) }
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

    spelData = spelData.copy(
        tafels = tafels.toMutableList()
    )
    spelData.tafels.forEach {
      val newSpelData = TafelService.nieuwSpel(spelData, it, startscore)
      spelData = newSpelData
    }
    return spelData
  }

  fun updateGebruikers(gebruikers: List<Gebruiker>, spelDataX:SpelData): SpelData  {
    var spelData = spelDataX
    val gebruikersMap = gebruikers.map { it.id to it }.toMap()
    val mutableGebruikersList = gebruikers.toMutableList()
    spelData.alleSpelers.forEach {
      val nieuweSpelerData = gebruikersMap[it.id]
      if (nieuweSpelerData != null) {
        mutableGebruikersList.remove(nieuweSpelerData)
        val (updatedSpelData, _) = spelData.updateGebruiker(
          it.copy(
            naam = nieuweSpelerData.naam,
            score = nieuweSpelerData.score,
            isMonkey = nieuweSpelerData.isMonkey,
            wilMeedoen = nieuweSpelerData.wilMeedoen
          )
        )
       spelData = updatedSpelData
      }
    }
    val nieuweSpelers = spelData.alleSpelers.plus(mutableGebruikersList)
    spelData = spelData.copy(
        alleSpelers = nieuweSpelers
      )

    return spelData
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
      val newSpelData =  TafelService.nieuwSpel(SpelContext.spelData, tafel, startscore)
      val pauzedTafel = newSpelData.findTafel(tafel.tafelNr).copy(
        gepauzeerd = newSpelData.nieuweTafelAutoPause == true
      )
      val (newSpelData2,_ ) = SpelContext.spelData.updateTafel(pauzedTafel)
      SpelContext.spelData = newSpelData2
    }
    return CommandResult(CommandStatus.SUCCEDED, "")
  }

  fun schopTafel(tafel: Tafel?): CommandResult {
    if (tafel != null) {
      val newnewSpelData = TafelService.vervolgSpel(tafel, SpelContext.spelData)
      SpelContext.spelData = newnewSpelData

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
