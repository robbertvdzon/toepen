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

    // maak nieuwe lege tafels
    var tafels = maakLegeTafels(aantalTafels, spelData)

    // verdeel spelers over tafels
    tafels = verdeelSpelersOverTafels(spelData, tafels)

    // tafels in spelData
    spelData = spelData.copy(
      tafels = tafels
    )

    // start nieuw spel in alle tafels
    spelData = startNieuwSpelAlleTafels(spelData, startscore)
    return spelData
  }

  private fun startNieuwSpelAlleTafels(spelData: SpelData, startscore: Int): SpelData {
    var spelData1 = spelData
    spelData1.tafels.forEach {
      val newSpelData = TafelService.nieuwSpel(spelData1, it, startscore)
      spelData1 = newSpelData
      }
    return spelData1
  }

  private fun verdeelSpelersOverTafels(spelData: SpelData, tafels: List<Tafel>): List<Tafel> {
    var tafels1 = tafels
    val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
    Util.shuffleSpelers(spelersDieMeedoen)
    while (spelersDieMeedoen.isNotEmpty()) {
      tafels1 = tafels1.map {
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
    return tafels1
  }

  private fun maakLegeTafels(aantalTafels: Int, spelData: SpelData): List<Tafel> {
    var tafels = (1..aantalTafels).map {
      Tafel(
        tafelNr = it,
        gepauzeerd = spelData.nieuweTafelAutoPause == true
      )
      }
    return tafels
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

  fun clearLog(spelData: SpelData): SpelData = spelData.copy(uitslagen = emptyList())

  fun resetScore(spelData: SpelData): SpelData {
    var newSpelData = spelData
    spelData.alleSpelers.forEach { gebruiker ->
      val (updatedSpelData, _) = newSpelData.changeGebruiker(gebruiker.copy(score = 0))
      newSpelData = updatedSpelData
    }
    return newSpelData
  }

  fun allesPauzeren(spelData: SpelData): SpelData {
    val gepauzeerdeTafels = spelData.tafels.map { it.copy(gepauzeerd = true) }
    return spelData.copy(tafels = gepauzeerdeTafels)
  }

  fun allesStarten(spelData: SpelData): SpelData {
    val gestartteTafels = spelData.tafels.map { it.copy(gepauzeerd = false) }
    return spelData.copy(tafels = gestartteTafels)
  }

  fun nieuwSpel(startscore: Int, tafel: Tafel, spelData: SpelData): SpelData {
    val gepauzeerdeTafel = tafel.copy(gepauzeerd = spelData.nieuweTafelAutoPause == true)
    return TafelService.nieuwSpel(spelData, gepauzeerdeTafel, startscore)
  }

  fun pauzeerTafel(tafel: Tafel, spelData: SpelData): SpelData = spelData.changeTafel(tafel.copy(gepauzeerd = true)).first

  fun startTafel(tafel: Tafel, spelData: SpelData): SpelData = spelData.changeTafel(tafel.copy(gepauzeerd = false)).first

}
