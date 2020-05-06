import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread


object Monkey {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    const val DELAY: Long = 10
    var echteSpelData = SpelContext.spelData
    var waitingSpelers : MutableSet<String> = HashSet<String>()
    val lock = Object()

    fun addWaitingSpeler(spelerId:String){
        synchronized(lock) {
            waitingSpelers.add(spelerId)
        }
    }

    fun removeWaitingSpeler(spelerId:String){
        synchronized(lock) {
            waitingSpelers.remove(spelerId)
        }
    }

    fun hasWaitingSpeler(spelerId:String): Boolean{
        synchronized(lock) {
            return waitingSpelers.contains(spelerId)
        }
    }

    fun start() {
        val timer = Timer()

        thread(start = true) {
            while (true) {
                Thread.sleep(DELAY)
                echteSpelData = SpelContext.spelData
                val spelData = objectMapper.readValue<SpelData>(CommandQueue.getLastSpeldataJson(), SpelData::class.java)

                if (echteSpelData.automatischNieuweTafels==true) {
                    val alleTafelsKlaar = spelData.tafels.all { it.tafelWinnaar != null }
                    if (alleTafelsKlaar) {
                        val startScore = echteSpelData.aantalFishesNieuweTafels?:15
                        val aantalTafels = echteSpelData.aantalAutomatischeNieuweTafels?:spelData.tafels.size
                        CommandQueue.addNewCommand(MaakNieuweTafelsCommand(aantalTafels, startScore))
                    }
                }

                spelData.tafels.filter { !it.gepauzeerd }.forEach {
                    val huidigeSpeler = it.huidigeSpeler
                    if (huidigeSpeler != null && huidigeSpeler.isMonkey && !hasWaitingSpeler(huidigeSpeler.id)) {
                        addWaitingSpeler(huidigeSpeler.id)
                        val tasknew: TimerTask = TimerSchedulePeriod(it, huidigeSpeler)
                        val delay:Long = echteSpelData.monkeyDelayMsec?:3000
                        timer.schedule(tasknew, delay)
                    }
                }
            }
        }
    }


    private fun speelMonkey(tafel_: Tafel, speler_: Speler) {
//        println("Monkey "+speler.naam)
        // Haal de recente tafel en speler data op
        val spelData = objectMapper.readValue<SpelData>(CommandQueue.getLastSpeldataJson(), SpelData::class.java)
        val tafel = spelData.tafels.firstOrNull() { it.tafelNr == tafel_.tafelNr }
        if (tafel==null) return
        val speler = spelData.alleSpelers.firstOrNull { it.id == speler_.id }
        if (speler==null) return

        var aantalPogingen = 0
        while (tafel.huidigeSpeler == speler && aantalPogingen < 50) {
            aantalPogingen++
            if (tafel.slagWinnaar == speler) {
                val res = CommandQueue.addNewCommand(PakSlagCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Pak slag : Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }

            }
            if ((0..100).random() == 0) {
                val res = CommandQueue.addNewCommand(ToepCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Toep : Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
            if ((0..2).random() == 0) {
                val kaart = speler.kaarten.getOrNull((0..3).random())
                if (kaart != null) {
                    val res = CommandQueue.addNewCommand(SpeelKaartCommand(zoekEchteSpeler(speler), kaart))
                    if (res.status == CommandStatus.SUCCEDED) {
//                        println("Speelkaart: Speler ${speler.naam} kaart: $kaart ,  hand:${speler.kaarten}")
                        Toepen.broadcastMessage()
                    }
                }
            }
            if ((0..2).random() == 0) {
                val res = CommandQueue.addNewCommand(GaMeeMetToepCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Ga mee: Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
            if ((0..2).random() == 0) {
                val res = CommandQueue.addNewCommand(PasCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Pas: Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
        }
        if (aantalPogingen == 50){
//            println("Geen actie: ${speler.naam}")
        }
        removeWaitingSpeler(speler.id)
    }

    private fun zoekEchteSpeler(speler: Speler): Speler {
        return echteSpelData.alleSpelers.firstOrNull{ it.naam == speler.naam }?:speler
    }

    class TimerSchedulePeriod(val tafel: Tafel, val speler: Speler) : TimerTask() {
        override fun run() {
            speelMonkey(tafel, speler)
        }
    }

}


