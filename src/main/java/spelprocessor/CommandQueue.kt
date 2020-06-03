package spelprocessor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.CommandResult
import model.CommandStatus
import model.SpelData
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


object CommandQueue {
  private val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  private val logFile = File("log.txt")
  private var logCommands = true
  private val unProcessedCommands: BlockingQueue<Command> = LinkedBlockingDeque<Command>(100)
  var lastSpelData: SpelData = SpelData()

  fun disableLog() {
    logCommands = false
  }

  fun initLogfile() {
    logFile.writeText("")
  }


  fun addNewCommand(command: Command): CommandResult {
    val added = unProcessedCommands.offer(command, 10, TimeUnit.SECONDS)
    if (!added) return CommandResult(CommandStatus.FAILED, "Timeout")
    synchronized(command.lock) {
      command.lock.wait(10)
    }
    while (command.result == null) { // TODO: await oid gebruiken
      Thread.sleep(10)
    }
    return command.result ?: CommandResult(CommandStatus.FAILED, "Unknown error")
  }

  fun processCommands() { // TODO: met coroutines
    thread(start = true) {
      while (true) {
        val command = unProcessedCommands.take()
        try {
          val spelDataBefore = objectMapper.writeValueAsString(lastSpelData)
          command.result = command.process(lastSpelData)
          logCommand(command, spelDataBefore)
          val commandResult = command.result
          if (commandResult?.newSpelData != null) {
            lastSpelData = commandResult.newSpelData
          }
        } catch (e: Exception) { // TODO: try/catch verwijderen
          command.result = CommandResult(CommandStatus.FAILED, e.message ?: "Unknown error")
        }
        synchronized(command.lock) {
          command.lock.notify()
        }
      }
    }
  }

  private fun logCommand(command: Command, spelDataBefore: String?) {
    if (command.result?.status?.equals(CommandStatus.SUCCEDED) ?: false && logCommands) {
      logFile.appendText("model.SpelData:" + spelDataBefore + "\n")
      logFile.appendText(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command) + "\n")
      println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
    }
  }


}


