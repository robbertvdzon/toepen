package util

import io.vavr.control.Either
import kotlinx.coroutines.runBlocking
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


  fun setSeed(seed: Long) {
    random = Random(seed)
  }

  fun nextInt(min: Int, max: Int) = (min..max).random(random)

  fun <A, B> eitherBlock(c: suspend () -> B): Either<A, B> {
    try {
      return Either.right(runBlocking { c.invoke() })
    } catch (e: EitherBlockException) {
      return Either.left(e.left as A)
    }
  }

  suspend fun <A, B> Either<A, B>.bind(): B =
    this.getOrElseThrow { e -> EitherBlockException(this.left as Any) }  // suspend, om te forceren dat hij alleen maar binnen de eitherBlock draait

  class EitherBlockException(val left: Any) : Exception()

}


