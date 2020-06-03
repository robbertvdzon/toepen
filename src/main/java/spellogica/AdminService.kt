package spellogica

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.Gebruiker
import model.SpelData
import model.Tafel
import spellogica.HelperFunctions.maakLegeTafels
import spellogica.HelperFunctions.startNieuwSpelAlleTafels
import spellogica.HelperFunctions.verdeelSpelersOverTafels
import java.io.File

object AdminService {
  val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  private const val DATA_FILE = "speldata.dat"

  fun loadData(): SpelData = objectMapper.readValue(File(DATA_FILE).readText(Charsets.UTF_8), SpelData::class.java)

  fun saveData(spelData: SpelData) = File(DATA_FILE).writeText(objectMapper.writeValueAsString(spelData))

  fun maakNieuweTafels(aantalTafels: Int, startscore: Int, spelData: SpelData): SpelData {
    val legeTafels = maakLegeTafels(aantalTafels, spelData)
    val tafelsMetSpelers = verdeelSpelersOverTafels(spelData, legeTafels)
    val gestarteTafels = startNieuwSpelAlleTafels(tafelsMetSpelers, startscore)
    return spelData.copy(tafels = gestarteTafels)
  }

  fun updateGebruikers(gebruikers: List<Gebruiker>, spelData: SpelData): SpelData {
    val gebruikersMap = gebruikers.map { it.id to it }.toMap()
    val remainingGebruikers = gebruikers.toMutableList()
    val aangepasteGebruikers = spelData.alleSpelers.map{
      val nieuweSpelerData = gebruikersMap[it.id]
      if (nieuweSpelerData != null) {
        remainingGebruikers.remove(nieuweSpelerData)
        it.copy(
          naam = nieuweSpelerData.naam,
          score = nieuweSpelerData.score,
          isMonkey = nieuweSpelerData.isMonkey,
          wilMeedoen = nieuweSpelerData.wilMeedoen
        )
      }
      else it
    }
    val alleGebruikers = aangepasteGebruikers.plus(remainingGebruikers)
    return spelData.copy(alleSpelers = alleGebruikers)
  }

  fun clearLog(spelData: SpelData): SpelData = spelData.copy(uitslagen = emptyList())

  fun resetScore(spelData: SpelData): SpelData {
    val gebruikers = spelData.alleSpelers.map{
      it.copy(score = 0)
    }
    return spelData.copy(alleSpelers = gebruikers)
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
    val gepauzeerdeTafel = tafel.copy(gepauzeerd = spelData.nieuweTafelAutoPause)
    val gestarteTafel = TafelService.nieuwSpel(gepauzeerdeTafel, startscore)
    return spelData.changeTafel(gestarteTafel).first
  }

  fun pauzeerTafel(tafel: Tafel, spelData: SpelData): SpelData = spelData.changeTafel(tafel.copy(gepauzeerd = true)).first

  fun startTafel(tafel: Tafel, spelData: SpelData): SpelData = spelData.changeTafel(tafel.copy(gepauzeerd = false)).first

}
