object SpelContext {
    var spelData = SpelData()
}

data class SpelData (
        var alleSpelers: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var tafels: MutableList<Tafel> = emptyList<Tafel>().toMutableList(),
        var uitslagen: MutableList<Uitslag> = emptyList<Uitslag>().toMutableList()

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
        var spelers: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var spelersDieAfZijn: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var opkomer: Speler? = null,
        var huidigeSpeler: Speler? = null,
        var toeper: Speler? = null,
        var inzet: Int = 0,
        var slagWinnaar: Speler? = null,
        var tafelWinnaar:Speler? = null,
        var gepauzeerd: Boolean = false
)

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
