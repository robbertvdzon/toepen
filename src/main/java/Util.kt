object Util {

    fun getGeschutKaartenDeck(): MutableList<Kaart> {
        val kaarten: MutableList<Kaart> = getKaartenDeck().toMutableList()
        kaarten.shuffle()
        return kaarten
    }

    fun getKaartenDeck(): List<Kaart> =
            (7..13).flatMap {
                listOf(
                        Kaart(KaartSymbool.HARTEN, it),
                        Kaart(KaartSymbool.RUITEN, it),
                        Kaart(KaartSymbool.SCHOPPEN, it),
                        Kaart(KaartSymbool.KLAVER, it)
                )
            }

}


