package spellogica

import model.SpelData
import model.Tafel

object ScoreService {

  fun werkScoreBij(tafel: Tafel, spelData: SpelData): SpelData {
    val spelersDieAfZijn = tafel.spelers.filter { !it.actiefInSpel }
    val aantalSpelersDieInSpelZitten = tafel.spelers.filter { it.actiefInSpel }
    val aantalSpelersDieInSpelZittenCount = aantalSpelersDieInSpelZitten.size
    val nieuweSpelersDieAfZijn = spelersDieAfZijn.filter { !tafel.spelersDieAfZijn.contains(it.id) }
    val score1 = 4 - aantalSpelersDieInSpelZittenCount
    val score = if (score1 > 0) score1 else 0
    val spelersMetNieuweScore = tafel.spelers.map {
      if (nieuweSpelersDieAfZijn.contains(it)) {
        it.copy(scoreDezeRonde = score)
      } else if (aantalSpelersDieInSpelZittenCount == 1 && aantalSpelersDieInSpelZitten.contains(it)) {
        // winnaar
        it.copy(scoreDezeRonde = 5)
      } else {
        it
      }
    }
    val spelersDieAfZijn1 = tafel.spelersDieAfZijn.plus(nieuweSpelersDieAfZijn.map { it.id })
    return spelData.changeTafel(
      tafel.copy(
        spelers = spelersMetNieuweScore,
        spelersDieAfZijn = spelersDieAfZijn1
      )
    )
  }

}
