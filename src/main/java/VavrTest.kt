import io.vavr.control.Either

object VavrTest {

    @JvmStatic
    fun main(args: Array<String>) {
        test()
    }

    fun test() {
        val e: Either<Fout2, Goed> = Either.left(Fout2("error"))
//        val r: Either<Fout, Goed> = Either.right(Goed("mijn waarde1"))
        val r: Either<Fout, Goed> = Either.left(Fout("mijn waarde1"))
        val g: Either<Fout, Goed> = Either.right(Goed("mijn waarde"))

        val y = g.bind() // moet niet hier mogen


        val d = fx<Fout, Goed> {
//                val z = e.bind()
            val y = g.bind()
            val w = r.bind()
            val q = g.bind()
            w
        }
        println(d)


    }
}

fun <A,B> fx(c: () -> B): Either<A, B> {
    try {
        return Either.right(c.invoke())
    } catch (e: MyEx) {
        return Either.left(e.fout as A)
    }
}

fun Either<Fout, Goed>.bind(): Goed {
    if (this.isLeft) {
        throw MyEx(this.left)
    }
    return this.get()
}

class MyEx(val fout: Any) : java.lang.Exception()

data class Fout(
        val fout: String
)

data class Fout2(
        val fout: String
)

data class Goed(
        val waarde: String
)
