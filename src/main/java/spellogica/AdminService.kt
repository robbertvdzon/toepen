package spellogica

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vavr.control.Either
import model.*
import util.Util
import java.io.File

object AdminService {
  val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  fun loadData(): Either<String, SpelData> {
    val json = File("speldata.dat").readText(Charsets.UTF_8)
    val spelData = objectMapper.readValue<SpelData>(json, SpelData::class.java)
    return Either.right(spelData)
  }

  fun saveData(spelData: SpelData) {
    val json = objectMapper.writeValueAsString(spelData)
    File("speldata.dat").writeText(json)
  }

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

  fun clearLog(spelDataX: SpelData): SpelData {
    return spelDataX.copy(
      uitslagen = emptyList<Uitslag>().toMutableList()
    )
  }

  fun resetScore(spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    spelData.alleSpelers.forEach {
      val (updatedSpelData, _) = spelData.updateGebruiker(
        it.copy(
          score = 0
        )
      )
      spelData = updatedSpelData
    }
    return spelData
  }


  fun allesPauzeren(spelDataX: SpelData): SpelData {
    var spelData = spelDataX.copy(
      tafels = spelDataX.tafels.map { it.copy(gepauzeerd = true) }.toMutableList()
    )

    return spelData
  }

  fun allesStarten(spelDataX: SpelData): SpelData {
    return spelDataX.copy(
        tafels = spelDataX.tafels.map { it.copy(gepauzeerd = false) }.toMutableList()
      )
  }

  fun nieuwSpel(startscore: Int, tafel: Tafel?,spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    if (tafel != null) {
      val newSpelData = TafelService.nieuwSpel(spelData, tafel, startscore)
      val pauzedTafel = newSpelData.findTafel(tafel.tafelNr).copy(
        gepauzeerd = newSpelData.nieuweTafelAutoPause == true
      )
      val (newSpelData2, _) = spelData.updateTafel(pauzedTafel)
      spelData = newSpelData2
    }
    return spelData
  }

  fun schopTafel(tafel: Tafel?,spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    if (tafel != null) {
      val newnewSpelData = TafelService.vervolgSpel(tafel, spelData)
      spelData = newnewSpelData

    }
    return spelData
  }

  fun pauzeerTafel(tafel: Tafel?,spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    spelData.copy(
        tafels = spelData.tafels.map {
          if (it.tafelNr == tafel?.tafelNr) it.copy(gepauzeerd = true) else it
        }.toMutableList()
    )
    return spelData
  }

  fun startTafel(tafel: Tafel?,spelDataX: SpelData): SpelData {
    var spelData = spelDataX
    spelData.copy(
        tafels = spelData.tafels.map {
          if (it.tafelNr == tafel?.tafelNr) it.copy(gepauzeerd = false) else it
        }.toMutableList()
      )
    return spelData
  }

}
