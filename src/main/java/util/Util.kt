package util

import io.vavr.control.Either
import kotlinx.coroutines.runBlocking
import model.Gebruiker
import model.Kaart
import model.KaartSymbool
import kotlin.random.Random

object Util {
  private var random: Random = Random(0)// vaste seed, zodat de tests voorspelbaar zijn
  private var kaartenDeckRandom: Random = Random(0)// vaste seed, zodat de tests voorspelbaar zijn

  fun getGeschutKaartenDeck(): MutableList<Kaart> {
    val kaarten: MutableList<Kaart> = getKaartenDeck().toMutableList()
    kaarten.shuffle(kaartenDeckRandom)
    return kaarten
  }

  fun shuffleSpelers(spelersDieMeedoen: List<Gebruiker>): List<Gebruiker> {
    val result: MutableList<Gebruiker> = spelersDieMeedoen.toMutableList()
    result.shuffle(kaartenDeckRandom)
    return result.toList()
  }


  private fun getKaartenDeck(): List<Kaart> =
    (7..14).flatMap {
      listOf(
        Kaart(KaartSymbool.HARTEN, it),
        Kaart(KaartSymbool.RUITEN, it),
        Kaart(KaartSymbool.SCHOPPEN, it),
        Kaart(KaartSymbool.KLAVER, it)
      )
    }

  fun setSeed(seed: Long) {
    random = Random(seed)
  }

  fun nextInt(min: Int, max: Int) = (min..max).random(random)

  fun <A, B> eitherBlock(c: suspend () -> B): Either<A, B> =
    try {
      Either.right(runBlocking { c.invoke() })
    } catch (e: EitherBlockException) {
      Either.left(e.left as A)
    }

  suspend fun <A, B> Either<A, B>.bind(): B = this.getOrElseThrow { _ -> EitherBlockException(this.left as Any) }  // suspend, om te forceren dat hij alleen maar binnen de eitherBlock draait

  class EitherBlockException(val left: Any) : Exception()

}


