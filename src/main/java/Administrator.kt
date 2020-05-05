import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File

object Administrator {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun loadData():CommandResult {
        val json = File("speldata.dat").readText(Charsets.UTF_8)
        val spelData = objectMapper.readValue<SpelData>(json, SpelData::class.java)
        // replace alle spelers uit de tafels naar de speldata.spelers
        spelData.tafels.forEach{
            tafel:Tafel -> tafel.spelers.forEach {speler:Speler->
              val spelerUitSpel = spelData.alleSpelers.filter { it.id==speler.id }.firstOrNull()
              if (spelerUitSpel!=null){
                  val index = spelData.alleSpelers.indexOf(spelerUitSpel)
                  spelData.alleSpelers.set(index, speler)
              }
            }
            // fix huidige speler
            if (tafel.huidigeSpeler!=null){
                tafel.huidigeSpeler = spelData.alleSpelers.filter { it.id==tafel.huidigeSpeler?.id }.firstOrNull()
            }
            // fix toeper
            if (tafel.toeper!=null){
                tafel.toeper = spelData.alleSpelers.filter { it.id==tafel.toeper?.id }.firstOrNull()
            }
            // fix opkomer
            if (tafel.opkomer!=null){
                tafel.opkomer = spelData.alleSpelers.filter { it.id==tafel.opkomer?.id }.firstOrNull()
            }
            // fix slagWinnaar
            if (tafel.slagWinnaar!=null){
                tafel.slagWinnaar = spelData.alleSpelers.filter { it.id==tafel.slagWinnaar?.id }.firstOrNull()
            }
            // fix tafelWinnaar
            if (tafel.tafelWinnaar!=null){
                tafel.tafelWinnaar = spelData.alleSpelers.filter { it.id==tafel.tafelWinnaar?.id }.firstOrNull()
            }
        }

        SpelContext.spelData = spelData
        CommandQueue.lastSpelDataJson = objectMapper.writeValueAsString(SpelContext.spelData)
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun saveData():CommandResult{
        val json = objectMapper.writeValueAsString(SpelContext.spelData)
        File("speldata.dat").writeText(json)
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun maakNieuweTafels(aantalTafels:Int, startscore:Int):CommandResult{
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
        spelData.tafels.forEach { TafelService.nieuwSpel(it, startscore) }
        return CommandResult(CommandStatus.SUCCEDED, "")
    }

    fun updateGebruikers(gebruikers:List<Speler>):CommandResult{
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
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

    fun clearOpmerkingen():CommandResult{
        SpelContext.spelData.opmerkingen.clear()
        return CommandResult(CommandStatus.SUCCEDED,"")
    }

}
