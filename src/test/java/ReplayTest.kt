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
  fun test1Tafel() {
    testFromLog("log_1_tafel.txt")
  }

  @Test
  fun test2Tafels() {
    testFromLog("log_2_tafels.txt")
  }


  fun testFromLog(logfile:String) {
    CommandQueue.processCommands()
    CommandQueue.disableLog()
    var playSynced = false
    File(logfile).forEachLine { regel ->
      val splitPos = regel.indexOfFirst { c -> c == ':' }
      if (splitPos > 0) {
        val cmd = regel.substring(0, splitPos)
        val json = regel.substring(splitPos + 1)
        if (cmd == "model.SpelData") {
          val spelData = objectMapper.readValue(json, SpelData::class.java)
          if (playSynced){
            testSpeldata(spelData)
          }

          SpelContext.spelData = spelData
          playSynced = true
        }
        if (cmd == "SpeelKaartCommand") {
          val command = objectMapper.readValue(json, SpeelKaartCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).describedAs("error:" + res.errorMessage).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "ToepCommand") {
          val command = objectMapper.readValue(json, ToepCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "PakSlagCommand") {
          val command = objectMapper.readValue(json, PakSlagCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "GaMeeMetToepCommand") {
          val command = objectMapper.readValue(json, GaMeeMetToepCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "PasCommand") {
          val command = objectMapper.readValue(json, PasCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "MaakNieuweTafelsCommand") {
          val command = objectMapper.readValue(json, MaakNieuweTafelsCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "UpdateGebruikersCommand") {
          val command = objectMapper.readValue(json, UpdateGebruikersCommand::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "NieuwSpel") {
          val command = objectMapper.readValue(json, NieuwSpel::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "AllesStarten") {
          val command = objectMapper.readValue(json, AllesStarten::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
        if (cmd == "SetRandomSeed") {
          val command = objectMapper.readValue(json, SetRandomSeed::class.java)
          println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          val res = CommandQueue.addNewCommand(command)
          assertThat(res.status).isEqualTo(CommandStatus.SUCCEDED)
        }
      }
    }
  }

  private fun testSpeldata(spelData: SpelData) {
    // remove uitslagen van de check
//    spelData.uitslagen = mutableListOf<Uitslag>()
//    SpelContext.spelData.uitslagen = mutableListOf<Uitslag>()
    // complare 2 data
    val ownSpelData = objectMapper.writeValueAsString(SpelContext.spelData)
    val correctSpelData = objectMapper.writeValueAsString(spelData)
    if (ownSpelData != correctSpelData) {
      println("spel differs")
    }
    assertThat(ownSpelData).isEqualTo(correctSpelData)
  }


}
