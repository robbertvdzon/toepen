package spellogica

import io.vavr.control.Either
import model.*
import util.Util
import util.Util.bind

object ToepSpel {

  fun speelKaart(speler: Speler, kaart: Kaart, spelData: SpelData): Either<String, SpelData> {

    val tafel = spelData.tafels.find { it.spelers.contains(speler) }
    if (tafel == null) return Either.left( "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left( "De tafel is gepauzeerd")
    val tafelNr = tafel.tafelNr

    val result = Util.eitherBlock<String, SpelData> {
      val newSpelData = SpelerService.speelKaart(speler, kaart, tafel, spelData).bind()
      val newnewSpelData = TafelService.vervolgSpel(spelData.findTafel(tafelNr),newSpelData)
      newnewSpelData
    }
    return result
  }

  fun pakSlag(speler: Speler, spelData: SpelData): Either<String, SpelData>  {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left(  "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left(  "De tafel is gepauzeerd")
    if (!speler.actiefInSpel) return Either.left(  "Je bent af")
    if (tafel.slagWinnaar != speler.id) return Either.left(  "Je hebt deze slag niet gewonnen")
    val newnewSpelData = TafelService.eindeSlag(tafel, spelData)
    return Either.right(newnewSpelData)
  }

  fun toep(speler: Speler, spelData: SpelData): Either<String, SpelData>  {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left(  "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left(  "De tafel is gepauzeerd")
    val nieuweInzet = tafel.inzet+1

    val result = Util.eitherBlock<String, SpelData> {
      val updatedSpeler = SpelerService.toep(speler, tafel, nieuweInzet).bind()
      val updatedTafel = tafel.changeSpeler(updatedSpeler).copy(inzet = nieuweInzet)
      val (newSpelData,newTafel ) = spelData.changeTafel(updatedTafel)
      val newnewSpelData = TafelService.toep(newSpelData, newTafel, updatedSpeler)
      newnewSpelData
    }

    return result
  }

  fun gaMeeMetToep(speler: Speler, spelData: SpelData): Either<String, SpelData>  {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left(  "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left(  "De tafel is gepauzeerd")

    val result = Util.eitherBlock<String, SpelData> {
      val updatedSpeler = SpelerService.gaMeeMetToep(speler, tafel).bind()
      val updatedTafel = tafel.changeSpeler(updatedSpeler)
      val (newSpelData,newTafel ) = spelData.changeTafel(updatedTafel)
      val newnewSpelData = TafelService.vervolgSpel(updatedTafel, newSpelData)
      newnewSpelData
    }
    return result
  }

  fun pas(speler: Speler, spelData: SpelData): Either<String, SpelData>  {
    val tafel = spelData.tafels.find { it.spelers.any { it.id == speler.id } }
    if (tafel == null) return Either.left(  "Je zit niet aan een tafel")
    if (tafel.gepauzeerd) return Either.left(  "De tafel is gepauzeerd")

    val result = Util.eitherBlock<String, SpelData> {
      val updatedSpeler = SpelerService.pas(speler, tafel).bind()
      val updatedTafel = tafel.changeSpeler(updatedSpeler)
      val (newSpelData,newTafel ) = spelData.changeTafel(updatedTafel)
      val newnewSpelData = TafelService.vervolgSpel(updatedTafel, newSpelData)
      newnewSpelData
    }
    return result
  }


}
