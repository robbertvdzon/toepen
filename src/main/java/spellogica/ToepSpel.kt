package spellogica

import io.vavr.control.Either
import model.Kaart
import model.SpelData
import model.Speler
import util.Util
import util.Util.bind

object ToepSpel {

  fun speelKaart(speler: Speler, kaart: Kaart, spelData: SpelData): Either<String, SpelData> {
    val tafel = spelData.tafels.find { it.spelers.contains(speler) }
    if (tafel == null) return Either.left("Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left("De tafel is gepauzeerd")
    val tafelNr = tafel.tafelNr

    return Util.eitherBlock {
      val newSpelData = SpelerService.speelKaart(speler, kaart, tafel, spelData).bind()
      TafelService.vervolgSpel(spelData.findTafel(tafelNr), newSpelData)
    }
  }

  fun pakSlag(speler: Speler, spelData: SpelData): Either<String, SpelData> {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left("Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left("De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return Either.left("Je bent af")
    if (tafel.slagWinnaar != speler.id) return Either.left("Je hebt deze slag niet gewonnen")
    val updatedSpelData = TafelService.eindeSlag(tafel, spelData)
    return Either.right(updatedSpelData)
  }

  fun toep(speler: Speler, spelData: SpelData): Either<String, SpelData> {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left("Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left("De tafel is gepauzeerd")
    val nieuweInzet = tafel.inzet + 1
    return Util.eitherBlock<String, SpelData> {
      val updatedSpeler = SpelerService.toep(speler, tafel, nieuweInzet).bind()
      val updatedTafel = tafel.changeSpeler(updatedSpeler).copy(inzet = nieuweInzet)
      val (updatedSpelData, _) = spelData.changeTafel(updatedTafel)
      TafelService.toep(updatedSpelData, updatedTafel, updatedSpeler)
    }
  }

  fun gaMeeMetToep(speler: Speler, spelData: SpelData): Either<String, SpelData> {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left("Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left("De tafel is gepauzeerd")
    return Util.eitherBlock<String, SpelData> {
      val updatedSpeler = SpelerService.gaMeeMetToep(speler, tafel).bind()
      val updatedTafel = tafel.changeSpeler(updatedSpeler)
      val (updatedSpelData, _) = spelData.changeTafel(updatedTafel)
      TafelService.vervolgSpel(updatedTafel, updatedSpelData)
    }
  }

  fun pas(speler: Speler, spelData: SpelData): Either<String, SpelData> {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left("Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left("De tafel is gepauzeerd")
    return Util.eitherBlock<String, SpelData> {
      val updatedSpeler = SpelerService.pas(speler, tafel).bind()
      val updatedTafel = tafel.changeSpeler(updatedSpeler)
      val (updatedSpelData, _) = spelData.changeTafel(updatedTafel)
      TafelService.vervolgSpel(updatedTafel, updatedSpelData)
    }
  }

}
