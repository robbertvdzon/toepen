package spelprocessor

import Toepen
import model.CommandStatus
import model.Speler
import model.Tafel
import util.Util
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread


object Monkey {
  private const val DELAY: Long = 10
  private val waitingSpelers: MutableSet<String> = HashSet()
  private val lock = Object()

  fun start() {
    val timer = Timer()

    thread(start = true) {
      while (true) {
        Thread.sleep(DELAY)
        CommandQueue.lastSpelData.tafels.filter { !it.gepauzeerd }.forEach {
          val spelData = CommandQueue.lastSpelData
          val huidigeSpeler = it.findHuidigeSpeler(spelData)
          val huidigeGebruiker = spelData.findGebruiker(it.huidigeSpeler)
          if (huidigeSpeler != null && huidigeGebruiker != null && huidigeGebruiker.isMonkey && !hasWaitingSpeler(huidigeSpeler.id)) {
            addWaitingSpeler(huidigeSpeler.id)
            val tasknew: TimerTask = TimerSchedulePeriod(it, huidigeSpeler)
            val delay: Long = spelData.monkeyDelayMsec
            timer.schedule(tasknew, delay)
          }
        }
      }
    }
  }


  private fun speelMonkey(tafel: Tafel, speler: Speler) {
    var aantalPogingen = 0
    while (tafel.huidigeSpeler?.equals(speler.id) == true && aantalPogingen < 50) {
      aantalPogingen++
      if (tafel.slagWinnaar == speler.id) {
        val res = CommandQueue.addNewCommand(PakSlagCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
          Toepen.broadcastMessage()
        }

      }
      if (Util.nextInt(0, 100) == 0) {
        val res = CommandQueue.addNewCommand(ToepCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
          Toepen.broadcastMessage()
        }
      }
      if (Util.nextInt(0, 1) == 0) {
        val kaart = speler.kaarten.getOrNull(Util.nextInt(0, 3))
        if (kaart != null) {
          val res = CommandQueue.addNewCommand(SpeelKaartCommand(speler.id, kaart))
          if (res.status == CommandStatus.SUCCEDED) {
            Toepen.broadcastMessage()
          }
        }
      }
      if (Util.nextInt(0, 1) == 0) {
        val res = CommandQueue.addNewCommand(GaMeeMetToepCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
          Toepen.broadcastMessage()
        }
      }
      if (Util.nextInt(0, 1) == 0) {
        val res = CommandQueue.addNewCommand(PasCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
          Toepen.broadcastMessage()
        }
      }
    }
    removeWaitingSpeler(speler.id)
  }

  private fun addWaitingSpeler(spelerId: String) {
    synchronized(lock) {
      waitingSpelers.add(spelerId)
    }
  }

  private fun removeWaitingSpeler(spelerId: String) {
    synchronized(lock) {
      waitingSpelers.remove(spelerId)
    }
  }

  private fun hasWaitingSpeler(spelerId: String): Boolean {
    synchronized(lock) {
      return waitingSpelers.contains(spelerId)
    }
  }

  class TimerSchedulePeriod(val tafel: Tafel, val speler: Speler) : TimerTask() {
    override fun run() {
      speelMonkey(tafel, speler)
    }
  }

}


