package util

import model.Kaart
import model.KaartSymbool
import kotlin.random.Random

object Util {
    private var random: Random = Random(0)

    fun getGeschutKaartenDeck(): MutableList<Kaart> {
        val kaarten: MutableList<Kaart> = getKaartenDeck().toMutableList()
        kaarten.shuffle()
        return kaarten
    }

    fun getKaartenDeck(): List<Kaart> =
            (7..14).flatMap {
                listOf(
                        Kaart(KaartSymbool.HARTEN, it),
                        Kaart(KaartSymbool.RUITEN, it),
                        Kaart(KaartSymbool.SCHOPPEN, it),
                        Kaart(KaartSymbool.KLAVER, it)
                )
            }


    fun setSeed(seed:Long){
        random = Random(seed)
    }

    fun nextInt(min:Int, max:Int) = (min..max).random(random)

}


