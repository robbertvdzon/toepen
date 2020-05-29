package spellogica

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.*
import spelprocessor.CommandQueue
import java.io.File

object Administrator {
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

    fun maakNieuweTafels(aantalTafels:Int, startscore:Int): CommandResult {
        val spelData = SpelContext.spelData
        val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
        spelersDieMeedoen.shuffle()
        val tafels = (1..aantalTafels).map{ Tafel(it) }
        while (spelersDieMeedoen.isNotEmpty()){
            tafels.forEach{
                if (spelersDieMeedoen.isNotEmpty()){
                    val gebruiker = spelersDieMeedoen.removeAt(0)
                    it.spelers.add(Speler(id = gebruiker.id, naam = gebruiker.naam))
                }
            }
        }

        spelData.tafels = tafels.toMutableList()
        spelData.tafels.forEach { TafelService.nieuwSpel(it, startscore) }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun updateGebruikers(gebruikers:List<Gebruiker>): CommandResult {
        val gebruikersMap =  gebruikers.map{it.id to it}.toMap()
        val mutableGebruikersList = gebruikers.toMutableList()
        SpelContext.spelData.alleSpelers.forEach{
            val nieuweSpelerData = gebruikersMap[it.id]
            if (nieuweSpelerData!=null) {
                mutableGebruikersList.remove(nieuweSpelerData)
                it.naam = nieuweSpelerData.naam
                it.score = nieuweSpelerData.score
                it.isMonkey = nieuweSpelerData.isMonkey
                it.wilMeedoen = nieuweSpelerData.wilMeedoen
            }
        }
        mutableGebruikersList.forEach{
            SpelContext.spelData.alleSpelers.add(it)
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun clearLog(): CommandResult {
        SpelContext.spelData.uitslagen = emptyList<Uitslag>().toMutableList()
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun resetScore(): CommandResult {
        SpelContext.spelData.alleSpelers.forEach{
            it.score = 0
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }


    fun allesPauzeren(): CommandResult {
        SpelContext.spelData.tafels.forEach{
            it.gepauzeerd = true
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun allesStarten(): CommandResult {
        SpelContext.spelData.tafels.forEach{
            it.gepauzeerd = false
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun nieuwSpel(startscore: Int, tafel: Tafel?): CommandResult {
        if (tafel!=null) {
            TafelService.nieuwSpel(tafel, startscore)
            tafel.gepauzeerd = SpelContext.spelData.nieuweTafelAutoPause==true
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun schopTafel(tafel: Tafel?): CommandResult {
        if (tafel!=null) {
            TafelService.vervolgSpel(tafel)
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun pauzeerTafel(tafel: Tafel?): CommandResult {
        if (tafel!=null) {
            tafel.gepauzeerd = true
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun startTafel(tafel: Tafel?): CommandResult {
        if (tafel!=null) {
            tafel.gepauzeerd = false
        }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

}
