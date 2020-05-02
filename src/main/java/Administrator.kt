import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

object Administrator {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun loadData():CommandResult {
        val json = File("speldata.dat").readText(Charsets.UTF_8)
        val spelData = objectMapper.readValue<SpelData>(json, SpelData::class.java)
        SpelContext.spelData = spelData
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun saveData():CommandResult{
        val json = objectMapper.writeValueAsString(SpelContext.spelData)
        File("speldata.dat").writeText(json)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun maakNieuweTafels(aantalTafels:Int):CommandResult{
        val spelData = SpelContext.spelData
        val spelersDieMeedoen = spelData.alleSpelers.filter { it.wilMeedoen }.toMutableList()
        spelersDieMeedoen.shuffle()
        val tafels = (1..aantalTafels).map{Tafel(it)}
        while (spelersDieMeedoen.isNotEmpty()){
            tafels.forEach{
                if (spelersDieMeedoen.isNotEmpty()){
                    it.spelers.add(spelersDieMeedoen.removeAt(0))
                }
            }
        }
        spelData.tafels = tafels.toMutableList()
        spelData.tafels.forEach{TafelService.nieuwSpel(it)}
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun updateGebruikers(gebruikers:List<Speler>):CommandResult{
        val gebruikersMap =  gebruikers.map{it.id to it}.toMap()
        val mutableGebruikersList = gebruikers.toMutableList()
        SpelContext.spelData.alleSpelers.forEach{
            val nieuweSpelerData = gebruikersMap[it.id]
            if (nieuweSpelerData!=null) {
                mutableGebruikersList.remove(nieuweSpelerData)
                it.totaalLucifers = nieuweSpelerData.totaalLucifers
                it.naam = nieuweSpelerData.naam
                it.actiefInSpel = nieuweSpelerData.actiefInSpel
                it.wilMeedoen = nieuweSpelerData.wilMeedoen
            }
        }
        mutableGebruikersList.forEach{
            SpelContext.spelData.alleSpelers.add(it)
        }
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun clearOpmerkingen():CommandResult{
        SpelContext.spelData.opmerkingen.clear()
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

}
