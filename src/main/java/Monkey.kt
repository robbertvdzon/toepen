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
            if ((0..2).random()==0){
                CommandQueue.addNewCommand(PakSlagCommand(speler))
            }
            if ((0..2).random()==0){
                val kaart = speler.kaarten.firstOrNull()
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
