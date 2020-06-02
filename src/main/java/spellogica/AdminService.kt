package spellogica

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.*
import util.Util
import java.io.File

object AdminService {
  val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  fun loadData(): SpelData = objectMapper.readValue(File("speldata.dat").readText(Charsets.UTF_8), SpelData::class.java)

  fun saveData(spelData: SpelData) = File("speldata.dat").writeText(objectMapper.writeValueAsString(spelData))

  fun maakNieuweTafels(aantalTafels: Int, startscore: Int, spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
    Util.shuffleSpelers(spelersDieMeedoen)
    var tafels = (1..aantalTafels).map {
      Tafel(
        tafelNr = it,
        gepauzeerd = spelData.nieuweTafelAutoPause == true
      )
    }
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

  fun updateGebruikers(gebruikers: List<Gebruiker>, spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    val gebruikersMap = gebruikers.map { it.id to it }.toMap()
    val mutableGebruikersList = gebruikers.toMutableList()
    spelData.alleSpelers.forEach {
      val nieuweSpelerData = gebruikersMap[it.id]
      if (nieuweSpelerData != null) {
        mutableGebruikersList.remove(nieuweSpelerData)
        val (updatedSpelData, _) = spelData.changeGebruiker(
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

  fun clearLog(spelData: SpelData): SpelData = spelData.copy(uitslagen = emptyList<Uitslag>())

  fun resetScore(spelData: SpelData): SpelData {
    var newSpelData = spelData
    spelData.alleSpelers.forEach { gebruiker ->
      val (updatedSpelData, _) = newSpelData.changeGebruiker(gebruiker.copy(score = 0))
      newSpelData = updatedSpelData
    }
    return newSpelData
  }

  fun allesPauzeren(spelData: SpelData) =
    spelData.copy(
      tafels = spelData.tafels.map { it.copy(gepauzeerd = true) }
    )

  fun allesStarten(spelData: SpelData): SpelData =
     spelData.copy(
      tafels = spelData.tafels.map { it.copy(gepauzeerd = false) }
    )

  fun nieuwSpel(startscore: Int, tafel: Tafel?, spelData: SpelData): SpelData {
    var newSpelData = spelData
    if (tafel != null) {
      val gepauzeerdeTafel = tafel.copy(
        gepauzeerd = newSpelData.nieuweTafelAutoPause == true
      )
      newSpelData = TafelService.nieuwSpel(newSpelData, gepauzeerdeTafel, startscore)
    }
    return newSpelData
  }

  fun pauzeerTafel(tafel: Tafel?, spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    return spelData.copy(
      tafels = spelData.tafels.map {
        if (it.tafelNr == tafel?.tafelNr) it.copy(gepauzeerd = true) else it
      }.toMutableList()
    )
  }

  fun startTafel(tafel: Tafel?, spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    return spelData.copy(
      tafels = spelData.tafels.map {
        if (it.tafelNr == tafel?.tafelNr) it.copy(gepauzeerd = false) else it
      }.toMutableList()
    )
  }

}
