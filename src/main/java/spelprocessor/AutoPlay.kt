package spelprocessor

import Toepen
import kotlin.concurrent.thread


object AutoPlay {
  private const val DELAY: Long = 10

  fun start() {
    thread(start = true) {
      while (true) {
        Thread.sleep(DELAY)
        val spelData = CommandQueue.lastSpelData
        if (spelData.automatischNieuweTafels) {
          val alleTafelsKlaar = spelData.tafels.all { it.tafelWinnaar != null }
          if (alleTafelsKlaar) {
            val startScore = spelData.aantalFishesNieuweTafels
            val aantalTafels = spelData.aantalAutomatischeNieuweTafels
            CommandQueue.addNewCommand(MaakNieuweTafelsCommand(aantalTafels, startScore))
            Toepen.broadcastMessage()
          }
        }
      }
    }
  }


}


