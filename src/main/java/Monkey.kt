import kotlin.concurrent.thread

object Monkey {
    fun start() {
        thread(start = true) {
            while (true) {
                Thread.sleep(2000)
                SpelContext.spelData.tafels.forEach{
                    val huidigeSpeler = it.huidigeSpeler
                    if (huidigeSpeler!=null && huidigeSpeler.id.startsWith("monkey")){
                        speelMonkey(it, huidigeSpeler)
                    }
                }

            }

        }
    }

    private fun speelMonkey(tafel: Tafel, speler: Speler) {
        println("Speel monkey:"+speler.naam)
        while (tafel.huidigeSpeler==speler){
            if (tafel.slagWinnaar==speler){
                CommandQueue.addNewCommand(PakSlagCommand(speler))
                Toepen.broadcastMessage()
                Thread.sleep(2000)// wacht 2 seconde voordat hij verder gaat

            }
            if ((0..2).random()==0){
                val kaart = speler.kaarten.getOrNull((0..3).random())
                if (kaart!=null) {
                    val res = CommandQueue.addNewCommand(SpeelKaartCommand(speler, kaart))
                    println(res)
                }
            }
            if ((0..2).random()==0){
                CommandQueue.addNewCommand(GaMeeMetToepCommand(speler))
            }
            if ((0..2).random()==0){
                CommandQueue.addNewCommand(PasCommand(speler))
            }
        }
        Toepen.broadcastMessage()
    }
}
