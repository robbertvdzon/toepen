import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


object CommandQueue {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    var lastSpelDataJson:String = objectMapper.writeValueAsString(SpelContext.spelData)
    val unProcessedCommands: BlockingQueue<Command> = LinkedBlockingDeque<Command>(100)

    fun addNewCommand(command:Command):CommandResult{
        println("New command, queue size="+unProcessedCommands.size)
        val added = unProcessedCommands.offer(command, 10, TimeUnit.SECONDS)
        if (!added) return CommandResult(CommandStatus.FAILED, "Timeout")
        synchronized(command.lock) {
            command.lock.wait(10)
        }
        while (command.result==null){ Thread.sleep(10)}
        println("command finished:"+command.result)
        return command.result?:CommandResult(CommandStatus.FAILED, "Unknown error")
    }

    fun processCommands(){
        thread(start = true) {
            while(true) {
                val command = unProcessedCommands.take()
                println("process "+command)
                try {
                    command.result = command.process()
                }
                catch (e:Exception){
                    command.result = CommandResult(CommandStatus.FAILED, e.message?:"Unknown error")
                }
                lastSpelDataJson = objectMapper.writeValueAsString(SpelContext.spelData)
                println("process command finished")
                synchronized(command.lock) {
                    command.lock.notify()
                }
            }
        }
    }

}


