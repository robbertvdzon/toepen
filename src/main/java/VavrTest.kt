import io.vavr.control.Either
import kotlinx.coroutines.*

object VavrTest {

    @JvmStatic
    fun main(args: Array<String>) {
        test()

    }

    fun test() {
        val r: Either<Fout, Goed> = Either.right(Goed("mijn waarde1"))
//        val r: Either<Fout, Goed> = Either.left(Fout("mijn waarde1"))
        val g: Either<Fout, Goed> = Either.right(Goed("mijn waarde"))

//        g.bind()

        val d = eitherBlock<Fout, Goed> {
            val y = g.bind()
            val w = r.bind()
            val q = g.bind()
            w
        }
        println(d)


    }
}

fun <A,B> eitherBlock(c: suspend  () -> B): Either<A, B> {
    try {
        val r = runBlocking<B> {
            c.invoke()
        }
        return Either.right(r)

    } catch (e: MyEx) {
        return Either.left(e.fout as A)
    }
}

suspend fun Either<Fout, Goed>.bind(): Goed { // suspend, om te forceren dat hij alleen maar binnen de eitherBlock draait
    if (this.isLeft) {
        throw MyEx(this.left)
    }
    return this.get()
}

class MyEx(val fout: Any) : java.lang.Exception()

data class Fout(
        val fout: String
)


data class Goed(
        val waarde: String
)
