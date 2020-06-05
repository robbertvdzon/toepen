package spellogica

import model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EindeSlagServiceTest {

  /*
  TODO: meer tests zoals deze maken, ook voor de andere services
   */

  @Test
  fun testLaatsteSlag() {
    val speler1 = spelerMetKaarten("1", 0)
    val speler2 = spelerMetKaarten("2", 0)
    val tafel = buildTafel(
      speler1 = speler1,
      speler2 = speler2,
      opkomer = "1",
      huidigeSpeler = "1",
      toeper = "1",
      slagWinnar = "1",
      tafelWinnaar = null
    )
    val gebruiker1 = Gebruiker(id = "1")
    val gebruiker2 = Gebruiker(id = "2")
    val spelData = SpelData(
      alleSpelers = listOf(gebruiker1, gebruiker2),
      tafels = listOf(tafel)
    )
    val spelDataResult = EindeSlagService.eindeSlag(tafel, spelData)
    assertThat(spelDataResult.tafels.first().huidigeSpeler).isEqualTo("1")
    assertThat(spelDataResult.tafels.first().opkomer).isEqualTo("1")
    assertThat(spelDataResult.tafels.first().slagWinnaar).isEqualTo(null)
    assertThat(spelDataResult.tafels.first().toeper).isEqualTo(null)
    assertThat(spelDataResult.tafels.first().spelers[0].actiefInSpel).isEqualTo(true)
    assertThat(spelDataResult.tafels.first().spelers[0].totaalLucifers).isEqualTo(5)
    assertThat(spelDataResult.tafels.first().spelers[1].actiefInSpel).isEqualTo(true)
    assertThat(spelDataResult.tafels.first().spelers[1].totaalLucifers).isEqualTo(5)
  }

  fun spelerMetKaarten(id: String, aantalKaarten: Int) = Speler(
    id = id,
    naam = "speler" + id,
    kaarten = (0..aantalKaarten).map { Kaart(KaartSymbool.HARTEN, 7 + it) },
    gespeeldeKaart = null,
    totaalLucifers = 5,
    ingezetteLucifers = 1,
    gepast = false,
    toepKeuze = Toepkeuze.GEEN_KEUZE,
    actiefInSpel = true,
    scoreDezeRonde = 0
  )

  fun buildTafel(speler1: Speler, speler2: Speler, opkomer: String, huidigeSpeler: String, toeper: String, slagWinnar: String?, tafelWinnaar: String?) = Tafel(
    tafelNr = 1,
    spelers = listOf(speler1, speler2),
    spelersDieAfZijn = emptyList(),
    opkomer = opkomer,
    huidigeSpeler = huidigeSpeler,
    toeper = toeper,
    inzet = 1,
    slagWinnaar = slagWinnar,
    tafelWinnaar = tafelWinnaar,
    gepauzeerd = false
  )

}

