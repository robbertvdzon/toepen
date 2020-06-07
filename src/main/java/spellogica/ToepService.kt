package spellogica

import model.SpelData
import model.Speler
import model.Tafel
import model.Toepkeuze

object ToepService {

  fun toep(spelData: SpelData, tafel: Tafel, speler: Speler): SpelData {
    // set automatische toep keuze voor alle spelers die geen keuze hebben
    val spelersMetToepKeuze = tafel.spelers.map {
      val toepKeuze =
        if (it.id == speler.id) {
          Toepkeuze.TOEP
        } else if (it.totaalLucifers == it.ingezetteLucifers) {
          Toepkeuze.MEE
        } else if (it.gepast) {
          Toepkeuze.PAS
        } else if (!it.actiefInSpel) {
          Toepkeuze.PAS
        } else Toepkeuze.GEEN_KEUZE
      it.copy(toepKeuze = toepKeuze)
    }

    val volgendeSpelerVoorToep = HelperFunctions.volgendeSpelerDieMoetToepen(spelersMetToepKeuze, spelersMetToepKeuze.first { it.id == speler.id })
    return if (volgendeSpelerVoorToep == null) {
      spelData.changeTafel(tafel.copy(
        huidigeSpeler = tafel.toeper,
        spelers = spelersMetToepKeuze,
        toeper = null
      ))
    } else {
      return spelData.changeTafel(tafel.copy(
        huidigeSpeler = volgendeSpelerVoorToep.id,
        spelers = spelersMetToepKeuze,
        toeper = tafel.toeper ?: speler.id
      ))
    }
  }

}
