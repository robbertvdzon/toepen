import KaartSymbool.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class ReplayTest {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


    @Test
    fun testFromLog() {
        CommandQueue.processCommands()
        CommandQueue.disableLog()
        var line:Int = 0
        File("log.txt").forEachLine { regel->
//        File("test1.txt").forEachLine { regel->
            println("command "+line++)
            val splitPos = regel.indexOfFirst{c->c==':'}
            if (splitPos>0) {
                val command = regel.substring(0, splitPos)
                val json = regel.substring(splitPos + 1)
                if (command == "SpelData") {
                    val spelData = objectMapper.readValue(json, SpelData::class.java)
                    SpelContext.spelData = spelData
                    // replace alle spelers uit de tafels naar de speldata.spelers
                    spelData.tafels.forEach{
                        tafel:Tafel -> tafel.findSpelers().forEach {speler:Speler->
                        val spelerUitSpel = spelData.alleSpelers.filter { it.id==speler.id }.firstOrNull()
                        if (spelerUitSpel!=null){
                            val index = spelData.alleSpelers.indexOf(spelerUitSpel)
                            spelData.alleSpelers.set(index, speler)
                        }
                    }
//
//                        // fix huidige speler
//                        if (tafel.huidigeSpeler!=null){
//                            tafel.huidigeSpeler = spelData.alleSpelers.filter { it.id==tafel.huidigeSpeler?.id }.firstOrNull()
//                        }
//                        // fix toeper
//                        if (tafel.toeper!=null){
//                            tafel.toeper = spelData.alleSpelers.filter { it.id==tafel.toeper?.id }.firstOrNull()
//                        }
//                        // fix opkomer
//                        if (tafel.opkomer!=null){
//                            tafel.opkomer = spelData.alleSpelers.filter { it.id==tafel.opkomer?.id }.firstOrNull()
//                        }
//                        // fix slagWinnaar
//                        if (tafel.slagWinnaar!=null){
//                            tafel.slagWinnaar = spelData.alleSpelers.filter { it.id==tafel.slagWinnaar?.id }.firstOrNull()
//                        }
//                        // fix tafelWinnaar
//                        if (tafel.tafelWinnaar!=null){
//                            tafel.tafelWinnaar = spelData.alleSpelers.filter { it.id==tafel.tafelWinnaar?.id }.firstOrNull()
//                        }
                    }
                }
                if (command == "SpeelKaartCommand") {
                    val command = objectMapper.readValue(json, SpeelKaartCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).describedAs("error:"+res.errorMessage).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "ToepCommand") {
                    val command = objectMapper.readValue(json, ToepCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "PakSlagCommand") {
                    val command = objectMapper.readValue(json, PakSlagCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "GaMeeMetToepCommand") {
                    val command = objectMapper.readValue(json, GaMeeMetToepCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "PasCommand") {
                    val command = objectMapper.readValue(json, PasCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "MaakNieuweTafelsCommand") {
                    val command = objectMapper.readValue(json, MaakNieuweTafelsCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "UpdateGebruikersCommand") {
                    val command = objectMapper.readValue(json, UpdateGebruikersCommand::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "NieuwSpel") {
                    val command = objectMapper.readValue(json, NieuwSpel::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "AllesStarten") {
                    val command = objectMapper.readValue(json, AllesStarten::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "SetRandomSeed") {
                    val command = objectMapper.readValue(json, SetRandomSeed::class.java)
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
            }
        }
    }




}
