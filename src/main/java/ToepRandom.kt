import kotlin.random.Random

object ToepRandom {
    var random:Random = Random(0)

    fun setSeed(seed:Long){
        random = Random(seed)
    }

    fun nextInt(min:Int, max:Int) = (min..max).random(random)
}
