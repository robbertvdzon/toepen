package spelprocessor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.CommandResult
import model.CommandStatus
import model.SpelContext
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread


object CommandQueue {
  val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  val logFile = File("log.txt")
  private val lock = ReentrantLock()
  private var logCommands = true

  fun disableLog() {
    logCommands = false
  }

  fun initLogfile() {
    logFile.writeText("")
  }

  private var lastSpelDataJson: String = objectMapper.writeValueAsString(SpelContext.spelData)
  val unProcessedCommands: BlockingQueue<Command> = LinkedBlockingDeque<Command>(100)

  fun addNewCommand(command: Command): CommandResult {
    val added = unProcessedCommands.offer(command, 10, TimeUnit.SECONDS)
    if (!added) return CommandResult(CommandStatus.FAILED, "Timeout")
    synchronized(command.lock) {
      command.lock.wait(10)
    }
    while (command.result == null) {
      Thread.sleep(10)
    }
    return command.result ?: CommandResult(CommandStatus.FAILED, "Unknown error")
  }

  fun processCommands() {
    thread(start = true) {
      while (true) {
        val command = unProcessedCommands.take()
        try {
          val before = objectMapper.writeValueAsString(SpelContext.spelData)
          command.result = command.process()
          if (command.result?.status?.equals(CommandStatus.SUCCEDED) ?: false && logCommands) {
            logFile.appendText("model.SpelData:" + before + "\n")
            logFile.appendText(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command) + "\n")
            println(command.javaClass.simpleName + ":" + jacksonObjectMapper().writeValueAsString(command))
          }
          else{
//            println("ongeldig command "+command.result)
          }
        } catch (e: Exception) {
          command.result = CommandResult(CommandStatus.FAILED, e.message
            ?: "Unknown error")
        }
        setLastSpeldataJson(objectMapper.writeValueAsString(SpelContext.spelData))
        synchronized(command.lock) {
          command.lock.notify()
        }
      }
    }
  }

  fun setLastSpeldataJson(json: String) {
    lock.lock()
    try {
      lastSpelDataJson = json
    } finally {
      lock.unlock()
    }
  }

  fun getLastSpeldataJson(): String {
    lock.lock()
    try {
      return lastSpelDataJson
    } finally {
      lock.unlock()
    }
  }


}


