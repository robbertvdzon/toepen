package spellogica

import io.vavr.control.Either
import model.*

object SpelerService {

  fun nieuwSpel(speler: Speler, startscore: Int) = speler.copy(
    actiefInSpel = true,
    kaarten = mutableListOf(),
    gespeeldeKaart = null,
    totaalLucifers = startscore,
    ingezetteLucifers = 0,
    scoreDezeRonde = 0,
    gepast = false,
    toepKeuze = Toepkeuze.GEEN_KEUZE
  )

  fun nieuweRonde(speler: Speler, kaarten: List<Kaart>) = speler.copy(
    gespeeldeKaart = null,
    gepast = false,
    toepKeuze = Toepkeuze.GEEN_KEUZE,
    kaarten = if (speler.actiefInSpel) kaarten.toMutableList() else speler.kaarten,
    ingezetteLucifers = if (speler.actiefInSpel) 1 else speler.ingezetteLucifers
  )

  fun nieuweSlag(speler: Speler) = speler.copy(
    gespeeldeKaart = if (speler.actiefInSpel) null else speler.gespeeldeKaart
  )

  fun speelKaart(speler: Speler, kaart: Kaart, tafel: Tafel, spelData: SpelData): Either<String, SpelData> {
    if (!speler.actiefInSpel) return Either.left("Je bent af")
    if (tafel.toeper != null) return Either.left("Nog niet iedereen heeft zijn toep keuze opgegeven")
    if (tafel.huidigeSpeler != speler.id) return Either.left("Je bent nog niet aan de beurt om een kaart te spelen")
    if (speler.gespeeldeKaart != null) return Either.left("Je hebt al een kaart gespeeld")
    if (!speler.kaarten.contains(kaart)) return Either.left("Deze kaart zit niet in de hand")

    if (tafel.opkomer != speler.id) {
      val kanBekennen = speler.kaarten.any { it.symbool == tafel.findOpkomer(spelData)?.gespeeldeKaart?.symbool }
      val zelfdeSymbool = tafel.findOpkomer(spelData)?.gespeeldeKaart?.symbool == kaart.symbool
      if (kanBekennen && !zelfdeSymbool) {
        return Either.left("Je moet bekennen")
      }
    }

    val newSpeler = speler.copy(
      gespeeldeKaart = kaart,
      kaarten = speler.kaarten.filter { it != kaart }
    )
    val newTafel = tafel.updateSpeler(newSpeler)
    val newSpelData = spelData.updateTafel(newTafel).first
    return Either.right(newSpelData)
  }

  fun toep(speler: Speler, tafel: Tafel, inzet: Int): Either<String, Speler> {
    if (!speler.actiefInSpel) return Either.left("Je bent af")
    if (tafel.huidigeSpeler != speler.id) return Either.left("Je bent nog niet aan de beurt om te toepen")
    if (speler.toepKeuze == Toepkeuze.TOEP) return Either.left("Je hebt al getoept")
    if (speler.totaalLucifers <= tafel.inzet) return Either.left("Je hebt te weinig lucifers om te toepen")
    return Either.right(
      speler.copy(
        ingezetteLucifers = inzet,
        toepKeuze = Toepkeuze.TOEP
      )
    )

  }

  fun gaMeeMetToep(speler: Speler, tafel: Tafel): Either<String, Speler> {
    if (!speler.actiefInSpel) return Either.left("Je bent af")
    if (tafel.toeper == null) return Either.left("Er is niet getoept")
    if (tafel.huidigeSpeler != speler.id) return Either.left("Je bent nog niet aan de beurt om mee te gaan")
    if (speler.toepKeuze != Toepkeuze.GEEN_KEUZE) return Either.left("Je hebt al een toepkeuze doorgegeven")

    return Either.right(
      speler.copy(
        ingezetteLucifers = if (tafel.inzet > speler.totaalLucifers) speler.totaalLucifers else tafel.inzet,
        toepKeuze = Toepkeuze.MEE
      )
    )

  }

  fun pas(speler: Speler, tafel: Tafel): Either<String, Speler> {
    if (!speler.actiefInSpel) return Either.left("Je bent af")
    if (tafel.toeper == null) return Either.left( "Er is niet getoept")
    if (tafel.huidigeSpeler != speler.id) return Either.left( "Je bent nog niet aan de beurt om te passen")
    if (speler.toepKeuze != Toepkeuze.GEEN_KEUZE) return Either.left("Je hebt al een toepkeuze doorgegeven")

    return Either.right(
      speler.copy(
        totaalLucifers  = speler.totaalLucifers-speler.ingezetteLucifers,
        ingezetteLucifers = 0,
        toepKeuze = Toepkeuze.PAS,
        gepast = true
      )
    )

  }


}
