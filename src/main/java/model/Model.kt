package model

object SpelContext {
    var spelData = SpelData()

    fun findGebruiker(spelerId: String?) = spelData
            .alleSpelers
            .firstOrNull { it.id == spelerId }

    fun findSpeler(spelerId: String?) = spelData
            .tafels
            .firstOrNull { containsSpeler(it, spelerId ?: "") }
            ?.spelers
            ?.firstOrNull { it.id == spelerId }

    private fun containsSpeler(it: Tafel, spelerId: String?) =
            it.spelers.filter { speler -> speler.id == spelerId?:"" }.firstOrNull() != null
}

data class SpelData(
        var alleSpelers: MutableList<Gebruiker> = emptyList<Gebruiker>().toMutableList(),
        var tafels: MutableList<Tafel> = emptyList<Tafel>().toMutableList(),
        var uitslagen: MutableList<Uitslag> = emptyList<Uitslag>().toMutableList(),
        var automatischNieuweTafels: Boolean? = true,
        var nieuweTafelAutoPause: Boolean? = false,
        var aantalAutomatischeNieuweTafels: Int? = 3,
        var aantalFishesNieuweTafels: Int? = 10,
        var monkeyDelayMsec: Long? = 5000
) {
    var scorelijst: MutableList<Gebruiker>
        get() = alleSpelers.filter { it.wilMeedoen }.sortedBy { it.score }.reversed().toMutableList()
        set(list) {}

}

data class Speler(
        val id: String = "",
        var naam: String = "",
        var kaarten: MutableList<Kaart> = emptyList<Kaart>().toMutableList(),
        var gespeeldeKaart: Kaart? = null,
        var totaalLucifers: Int = 0,
        var ingezetteLucifers: Int = 0,
        var gepast: Boolean = false, // gepast na een toep
        var toepKeuze: Toepkeuze = Toepkeuze.GEEN_KEUZE,
        var actiefInSpel: Boolean = true, // zit nog in het spel (is niet af)
        var scoreDezeRonde: Int = 0
) {
    fun berekenScore(startKaart: Kaart): Int {
        if (gespeeldeKaart == null) return 0
        if (startKaart.symbool != gespeeldeKaart?.symbool) return 0
        return gespeeldeKaart?.waarde ?: 0
    }
}

data class Gebruiker(
        var id: String = "",
        var naam: String = "",
        var wilMeedoen: Boolean = false, // bij nieuwe tafel indeling, deze speler mee laten doen
        var isMonkey: Boolean = false,
        var score: Int = 0
)

data class Tafel(
        val tafelNr: Int,
        var log: MutableList<String> = emptyList<String>().toMutableList(),
        var spelers: MutableList<Speler> = emptyList<Speler>().toMutableList(),
        var spelersDieAfZijn: MutableList<String> = mutableListOf(),
        var opkomer: String? = null,
        var huidigeSpeler: String? = null,
        var toeper: String? = null,
        var inzet: Int = 0,
        var slagWinnaar: String? = null,
        var tafelWinnaar: String? = null,
        var gepauzeerd: Boolean = SpelContext.spelData.nieuweTafelAutoPause == true
) {
    fun findSpelersDieAfZijn() = spelersDieAfZijn.map { SpelContext.findSpeler(it) }.filter{it!=null}.map{it as Speler }
    fun findOpkomer() = SpelContext.findSpeler(opkomer)
    fun findHuidigeSpeler() = SpelContext.findSpeler(huidigeSpeler)
    fun findToeper() = SpelContext.findSpeler(toeper)
    fun findSlagWinnaar() = SpelContext.findSpeler(slagWinnaar)
    fun findTafelWinnaar() = SpelContext.findSpeler(tafelWinnaar)

}

data class Kaart(
        val symbool: KaartSymbool,
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
        val tafel: Int,
        val logregels: List<SpelerScore>
)

data class SpelerScore(
        val naam: String,
        val score: Int
)

data class CommandResult(
        var status: CommandStatus,
        var errorMessage: String
)

enum class CommandStatus {
    SUCCEDED,
    FAILED
}

data class Winnaar(
        val winnaarType: WINNAAR_TYPE,
        val tafelNr: Int,
        val winnaar: String
)

enum class WINNAAR_TYPE {
    SLAG, RONDE, SPEL
}
