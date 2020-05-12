
object SpelContext {
    var spelData = SpelData()
}

data class SpelData (
        var alleSpelers: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var tafels: MutableList<Tafel> = emptyList<Tafel>().toMutableList(),
        var uitslagen: MutableList<Uitslag> = emptyList<Uitslag>().toMutableList(),
        var automatischNieuweTafels: Boolean? = true,
        var nieuweTafelAutoPause: Boolean? = false,
        var aantalAutomatischeNieuweTafels: Int? = 3,
        var aantalFishesNieuweTafels: Int? = 10,
        var monkeyDelayMsec: Long? = 5000
){
    var scorelijst: MutableList<Speler>
    get() = alleSpelers.filter { it.wilMeedoen }.sortedBy { it.score }.reversed().toMutableList()
    set(list)  {}

}

data class Speler(
        var id: String = "",
        var naam: String = "",
        var kaarten: MutableList<Kaart> = emptyList<Kaart>().toMutableList(),
        var gespeeldeKaart: Kaart? = null,
        var totaalLucifers:Int = 0,
        var ingezetteLucifers: Int =0,
        var gepast: Boolean = false, // gepast na een toep
        var toepKeuze: Toepkeuze = Toepkeuze.GEEN_KEUZE,
        var actiefInSpel: Boolean = true, // zit nog in het spel (is niet af)
        var wilMeedoen: Boolean = false, // bij nieuwe tafel indeling, deze speler mee laten doen
        var isMonkey: Boolean = false,
        var score:Int = 0,
        var scoreDezeRonde: Int = 0
) {
    fun berekenScore(startKaart: Kaart):Int {
        if (gespeeldeKaart==null) return 0
        if (startKaart.symbool!=gespeeldeKaart?.symbool) return 0
        return gespeeldeKaart?.waarde?:0
    }
}

data class Tafel(
        val tafelNr: Int,
        var log: MutableList<String> = emptyList<String>().toMutableList(),
        var spelers: MutableList<String> = emptyList<String>().toMutableList(),
        var spelersDieAfZijn: MutableList<String> = emptyList<String>().toMutableList(),
        var opkomer: String? = null,
        var huidigeSpeler: String? = null,
        var toeper: String? = null,
        var inzet: Int = 0,
        var slagWinnaar: String? = null,
        var tafelWinnaar:String? = null,
        var gepauzeerd: Boolean = SpelContext.spelData.nieuweTafelAutoPause==true
){
    fun findSpelers() = spelers.map {SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}
    fun findSpelersDieAfZijn() = spelersDieAfZijn.map {SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}
    fun findOpkomer() = opkomer?.let{SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}
    fun findHuidigeSpeler() = huidigeSpeler?.let{SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}
    fun findToeper() = toeper?.let{SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}
    fun findSlagWinnaar() = slagWinnaar?.let{SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}
    fun findTafelWinnaar() = tafelWinnaar?.let{SpelContext.spelData.alleSpelers.first{sp->sp.id.equals(it)}}

}

data class Kaart(
        val symbool:KaartSymbool,
        val waarde: Int
)

enum class KaartSymbool {
    HARTEN, KLAVER, RUITEN, SCHOPPEN
}

enum class Toepkeuze {
    TOEP, MEE, PAS, NVT, GEEN_KEUZE
}

data class Uitslag(
        val timestamp: String,
        val tafel:Int,
        val logregels: List<SpelerScore>
)

data class SpelerScore(
        val naam: String,
        val score: Int
)

data class CommandResult(
        var status:CommandStatus,
        var errorMessage:String
)

enum class CommandStatus {
    SUCCEDED,
    FAILED
}

data class Winnaar(
        val winnaarType: WINNAAR_TYPE,
        val tafelNr:Int,
        val winnaar:String
)

enum class WINNAAR_TYPE{
    SLAG, RONDE, SPEL
}
