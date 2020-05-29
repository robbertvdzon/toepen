import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.CommandStatus
import model.SpelContext
import model.SpelData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import spelprocessor.*
import java.io.File

class ReplayTest {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


    @Test
    fun testFromLog() {
        CommandQueue.processCommands()
        CommandQueue.disableLog()
        var line:Int = 0
        File("testlog.txt").forEachLine { regel->
//        File("test1.txt").forEachLine { regel->
//            println("command "+line++)
            line++
            val splitPos = regel.indexOfFirst{c->c==':'}
            if (splitPos>0) {
                val command = regel.substring(0, splitPos)
                val json = regel.substring(splitPos + 1)
                if (command == "model.SpelData") {
                    val ownSpelData = objectMapper.writeValueAsString(SpelContext.spelData)
                    if (ownSpelData!=json){
                        println("speldata differs!")
                    }
                    val spelData = objectMapper.readValue(json, SpelData::class.java)
                    SpelContext.spelData = spelData
                }
                if (command == "SpeelKaartCommand") {
                    val command = objectMapper.readValue(json, SpeelKaartCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).describedAs("error:"+res.errorMessage).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "ToepCommand") {
                    val command = objectMapper.readValue(json, ToepCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "PakSlagCommand") {
                    val command = objectMapper.readValue(json, PakSlagCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "GaMeeMetToepCommand") {
                    val command = objectMapper.readValue(json, GaMeeMetToepCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "PasCommand") {
                    val command = objectMapper.readValue(json, PasCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "MaakNieuweTafelsCommand") {
                    val command = objectMapper.readValue(json, MaakNieuweTafelsCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "UpdateGebruikersCommand") {
                    val command = objectMapper.readValue(json, UpdateGebruikersCommand::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "NieuwSpel") {
                    val command = objectMapper.readValue(json, NieuwSpel::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "AllesStarten") {
                    val command = objectMapper.readValue(json, AllesStarten::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
                if (command == "SetRandomSeed") {
                    val command = objectMapper.readValue(json, SetRandomSeed::class.java)
                    println(command.javaClass.simpleName+":"+jacksonObjectMapper().writeValueAsString(command))
                    val res = CommandQueue.addNewCommand(command)
                    assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
                }
            }
        }
    }




}
