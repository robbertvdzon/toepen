package spelprocessor

import Toepen
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.CommandStatus
import model.Speler
import model.Tafel
import util.Util
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread


object Monkey {
  const val DELAY: Long = 10
  var echteSpelData = CommandQueue.lastSpelData
  val waitingSpelers: MutableSet<String> = HashSet<String>()
  val lock = Object()

  fun addWaitingSpeler(spelerId: String) {
    synchronized(lock) {
      waitingSpelers.add(spelerId)
    }
  }

  fun removeWaitingSpeler(spelerId: String) {
    synchronized(lock) {
      waitingSpelers.remove(spelerId)
    }
  }

  fun hasWaitingSpeler(spelerId: String): Boolean {
    synchronized(lock) {
      return waitingSpelers.contains(spelerId)
    }
  }

  /*
  TODO: deze functie kan vast mooier
   */
  fun start() {
    val timer = Timer()

    thread(start = true) {
      while (true) {
        Thread.sleep(DELAY)
        echteSpelData = CommandQueue.lastSpelData
        var spelData = CommandQueue.lastSpelData // TODO: maak hier een val

        spelData.tafels.filter { !it.gepauzeerd }.forEach {
          spelData = CommandQueue.lastSpelData
          val huidigeSpeler = it.findHuidigeSpeler(spelData)
          val huidigeGebruiker = spelData.findGebruiker(it.huidigeSpeler)
          if (huidigeSpeler != null && huidigeGebruiker != null && huidigeGebruiker.isMonkey && !hasWaitingSpeler(huidigeSpeler.id)) {
            addWaitingSpeler(huidigeSpeler.id)
            val tasknew: TimerTask = TimerSchedulePeriod(it, huidigeSpeler)
            val delay: Long = echteSpelData.monkeyDelayMsec ?: 3000
            timer.schedule(tasknew, delay)
          }
        }
      }
    }
  }


  /*
  TODO: deze functie kan vast mooier
   */
  private fun speelMonkey(tafel: Tafel, speler: Speler) {

    var aantalPogingen = 0
    while (tafel.huidigeSpeler?.equals(speler.id) ?: false && aantalPogingen < 50) {
      aantalPogingen++
      if (tafel.slagWinnaar == speler.id) {
        val res = CommandQueue.addNewCommand(PakSlagCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
//                    println("Pak slag : model.Speler ${speler.naam} ,  hand:${speler.kaarten}")
          Toepen.broadcastMessage()
        }

      }
      if (Util.nextInt(0, 100) == 0) {
        val res = CommandQueue.addNewCommand(ToepCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
//                    println("Toep : model.Speler ${speler.naam} ,  hand:${speler.kaarten}")
          Toepen.broadcastMessage()
        }
      }
      if (Util.nextInt(0, 1) == 0) {
        val kaart = speler.kaarten?.getOrNull(Util.nextInt(0, 3))
        if (kaart != null) {
          val res = CommandQueue.addNewCommand(SpeelKaartCommand(speler.id, kaart))
          if (res.status == CommandStatus.SUCCEDED) {
//                        println("Speelkaart: model.Speler ${speler.naam} kaart: $kaart ,  hand:${speler.kaarten}")
            Toepen.broadcastMessage()
          }
        }
      }
      if (Util.nextInt(0, 1) == 0) {
        val res = CommandQueue.addNewCommand(GaMeeMetToepCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
//                    println("Ga mee: model.Speler ${speler.naam} ,  hand:${speler.kaarten}")
          Toepen.broadcastMessage()
        }
      }
      if (Util.nextInt(0, 1) == 0) {
        val res = CommandQueue.addNewCommand(PasCommand(speler.id))
        if (res.status == CommandStatus.SUCCEDED) {
//                    println("Pas: model.Speler ${speler.naam} ,  hand:${speler.kaarten}")
          Toepen.broadcastMessage()
        }
      }

    }
    removeWaitingSpeler(speler.id)
  }


  class TimerSchedulePeriod(val tafel: Tafel, val speler: Speler) : TimerTask() {
    override fun run() {
      speelMonkey(tafel, speler)
    }
  }

}


