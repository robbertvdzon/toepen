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
                        Toepen.broadcastMessage()
                    }
                }

                spelData.tafels.filter { !it.gepauzeerd }.forEach {
                    val huidigeSpeler = it.findHuidigeSpeler()
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
        var spelData = objectMapper.readValue<SpelData>(CommandQueue.getLastSpeldataJson(), SpelData::class.java)
        var tafel = spelData.tafels.firstOrNull() { it.tafelNr == tafel_.tafelNr }
        if (tafel==null) return
        val speler = spelData.alleSpelers.firstOrNull { it.id == speler_.id }?.id
        if (speler==null) return

        var aantalPogingen = 0
        while (tafel?.huidigeSpeler?.equals(speler)?:false && aantalPogingen < 50) {
            aantalPogingen++
            if (tafel?.slagWinnaar == speler) {
                val res = CommandQueue.addNewCommand(PakSlagCommand(speler))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Pak slag : Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }

            }
            if (ToepRandom.nextInt(0,100) == 0) {
                val res = CommandQueue.addNewCommand(ToepCommand(speler))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Toep : Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
            if (ToepRandom.nextInt(0,1) == 0) {
                val kaart = zoekSpeler(speler)?.kaarten?.getOrNull(ToepRandom.nextInt(0,3))
                if (kaart != null) {
                    val res = CommandQueue.addNewCommand(SpeelKaartCommand(speler, kaart))
                    if (res.status == CommandStatus.SUCCEDED) {
//                        println("Speelkaart: Speler ${speler.naam} kaart: $kaart ,  hand:${speler.kaarten}")
                        Toepen.broadcastMessage()
                    }
                }
            }
            if (ToepRandom.nextInt(0,1) == 0) {
                val res = CommandQueue.addNewCommand(GaMeeMetToepCommand(speler))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Ga mee: Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
            if (ToepRandom.nextInt(0,1) == 0) {
                val res = CommandQueue.addNewCommand(PasCommand(speler))
                if (res.status == CommandStatus.SUCCEDED) {
//                    println("Pas: Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }

            // update speldata
            spelData = objectMapper.readValue<SpelData>(CommandQueue.getLastSpeldataJson(), SpelData::class.java)
            tafel = spelData.tafels.firstOrNull() { it.tafelNr == tafel_.tafelNr }
        }
        if (aantalPogingen == 50){
//            println("Geen actie: ${speler.naam}")
        }
        removeWaitingSpeler(speler)
    }


    private fun zoekSpeler(id:String): Speler? {
        return echteSpelData.alleSpelers.firstOrNull{ it.id == id }
    }


    class TimerSchedulePeriod(val tafel: Tafel, val speler: Speler) : TimerTask() {
        override fun run() {
            speelMonkey(tafel, speler)
        }
    }

}


