import io.vavr.control.Either
import kotlinx.coroutines.runBlocking

object VavrTest {

    @JvmStatic
    fun main(args: Array<String>) {
        test()

    }

    fun test() {
//        val r: Either<Fout, Goed> = Either.right(Goed("mijn waarde1"))
        val r: Either<Fout, Goed> = Either.left(Fout(2, "mijn fout"))
        val g: Either<Fout, Goed> = Either.right(Goed(4, "mijn waarde"))

//        g.bind()

        val d = eitherBlock<Fout, Goed> {
            val y = g.bind()
            val w = r.bind()
            val q = g.bind()
            w
        }
        println(d)

        val dasd = "d" before "3" after ""
        dasd after ""


    }


}

infix fun String.before(s: String): Test2 {
    return Test2()
}


class Test2 {
    infix fun and(s:String): Test2 {
        return Test2()
    }

    infix fun after(s: String): Test2 {
        return Test2()
    }
}

fun <A, B> eitherBlock(c: suspend () -> B): Either<A, B> {
    try {
        return Either.right(runBlocking { c.invoke() })
    } catch (e: EitherBlockException) {
        return Either.left(e.left as A)
    }
}

suspend fun Either<Fout, Goed>.bind(): Goed = this.getOrElseThrow { (e) -> EitherBlockException(this.left) }  // suspend, om te forceren dat hij alleen maar binnen de eitherBlock draait

class EitherBlockException(val left: Any) : Exception()

data class Fout(
        val uuid: Long,
        val fout: String
)


data class Goed(
        val uuid: Long,
        val waarde: String
)
