object Context {
    var spelData = SpelData()
}

data class SpelData (
        var alleSpelers: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var aantalTafels: Int = 1,
        var aantalKaartenInDeck: Int = 20,
        var tafels: MutableList<Tafel> = emptyList<Tafel>().toMutableList(),
        val opmerkingen: MutableList<Opmerking> = emptyList<Opmerking>().toMutableList()
)

data class Speler(
        var id: String,
        var naam: String,
        var kaarten: MutableList<Kaart> = emptyList<Kaart>().toMutableList(),
        var gespeeldeKaart: Kaart?,
        var totaalLucifers:Int,
        var ingezetteLucifers: Int,
        var gepast: Boolean, // gepast na een toep
        var toepKeuze: Toepkeuze,
        var actiefInSpel: Boolean, // zit nog in het spel (is niet af)
        var wilMeedoen: Boolean // bij nieuwe tafel indeling, deze speler mee laten doen
) {
    fun berekenScore(startKaart: Kaart):Int {
        if (gespeeldeKaart==null) return 0
        if (startKaart.symbool!=gespeeldeKaart?.symbool) return 0
        return gespeeldeKaart?.waarde?:0
    }
}

data class Tafel(
        var alleKaarten: MutableList<Kaart> = emptyList<Kaart>().toMutableList(),
        var spelers: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var opkomer: Speler? = null,
        var huidigeSpeler: Speler? = null,
        var toeper: Speler? = null,
        var slagWinnaar: Speler? = null,
        var tafelWinnaar:Speler? = null
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

data class Opmerking(
        val speler:String,
        val timestamp: String,
        val opmerking: String
)

data class CommandResult(
        var status:CommandStatus,
        var errorMessage:String
)

enum class CommandStatus {
    SUCCEDED,
    FAILED
}
