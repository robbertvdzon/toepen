import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.concurrent.thread

object Monkey {
    val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        const val DELAY:Long=100
//    const val DELAY: Long = 10
    var echteSpelData = SpelContext.spelData

    fun start() {
        thread(start = true) {
            while (true) {
                Thread.sleep(DELAY)

                echteSpelData = SpelContext.spelData
                val spelData = objectMapper.readValue<SpelData>(CommandQueue.getLastSpeldataJson(), SpelData::class.java)

                val alleTafelsKlaar = spelData.tafels.all { it.tafelWinnaar != null }
                if (alleTafelsKlaar) {
                    CommandQueue.addNewCommand(MaakNieuweTafelsCommand(spelData.tafels.size, 5))
                }

                spelData.tafels.filter { !it.gepauzeerd }.forEach {
                    val huidigeSpeler = it.huidigeSpeler
                    if (huidigeSpeler != null && huidigeSpeler.isMonkey) {
                        speelMonkey(it, huidigeSpeler)
                    }
                }

            }

        }
    }

    private fun speelMonkey(tafel: Tafel, speler: Speler) {
        var aantalPogingen = 0
        while (tafel.huidigeSpeler == speler && aantalPogingen < 50) {
            aantalPogingen++
            if (tafel.slagWinnaar == speler) {
                val res = CommandQueue.addNewCommand(PakSlagCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
                    //println(("Pak slag : Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                    Thread.sleep(DELAY)// wacht 2 seconde voordat hij verder gaat
                }

            }
            if ((0..10).random() == 0) {
                val res = CommandQueue.addNewCommand(ToepCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
                    //println(("Toep : Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
            if ((0..2).random() == 0) {
                val kaart = speler.kaarten.getOrNull((0..3).random())
                if (kaart != null) {
                    val res = CommandQueue.addNewCommand(SpeelKaartCommand(zoekEchteSpeler(speler), kaart))
                    if (res.status == CommandStatus.SUCCEDED) {
                        //println(("Speelkaart: Speler ${speler.naam} kaart: $kaart ,  hand:${speler.kaarten}")
                        Toepen.broadcastMessage()
                    }
                }
            }
            if ((0..2).random() == 0) {
                val res = CommandQueue.addNewCommand(GaMeeMetToepCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
                    //println(("Ga mee: Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
            if ((0..2).random() == 0) {
                val res = CommandQueue.addNewCommand(PasCommand(zoekEchteSpeler(speler)))
                if (res.status == CommandStatus.SUCCEDED) {
                    //println(("Pas: Speler ${speler.naam} ,  hand:${speler.kaarten}")
                    Toepen.broadcastMessage()
                }
            }
        }
    }

    private fun zoekEchteSpeler(speler: Speler): Speler {
        return echteSpelData.alleSpelers.firstOrNull{ it.naam == speler.naam }?:speler
    }
}
