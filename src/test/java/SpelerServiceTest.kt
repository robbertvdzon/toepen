//import model.KaartSymbool.*
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//
//class SpelerServiceTest {
//
//    @Test
//    fun testNieuweTafels() {
//        val speler1 = maakSpeler("Speler1", "001")
//        val speler2 = maakSpeler("Speler2", "002")
//        val speler3 = maakSpeler("Speler3", "003")
//        val speler4 = maakSpeler("Speler4", "004")
//        val speler5 = maakSpeler("Speler5", "005")
//        spellogica.Administrator.updateGebruikers(listOf(speler1, speler2, speler3, speler4, speler5))
//        spellogica.Administrator.maakNieuweTafels(2, 15)
//        val tafels = model.SpelContext.spelData.tafels
//        assertThat(tafels).hasSize(2);
//        assertThat(tafels[0].spelers).hasSize(3)
//        assertThat(tafels[0].opkomer).isNotNull()
//        assertThat(tafels[0].opkomer).isEqualTo(tafels[0].huidigeSpeler)
//        assertThat(tafels[0].toeper).isNull()
//        assertThat(tafels[0].slagWinnaar).isNull()
//        assertThat(tafels[0].tafelWinnaar).isNull()
//
//        assertThat(tafels[1].spelers).hasSize(2)
//        assertThat(tafels[1].opkomer).isNotNull()
//        assertThat(tafels[1].opkomer).isEqualTo(tafels[1].huidigeSpeler)
//        assertThat(tafels[1].toeper).isNull()
//        assertThat(tafels[1].slagWinnaar).isNull()
//        assertThat(tafels[1].tafelWinnaar).isNull()
//    }
//
//    @Test
//    fun testSpel1() {
//        val speler1 = maakSpeler("Speler1", "001")
//        val speler2 = maakSpeler("Speler2", "002")
//        val speler3 = maakSpeler("Speler3", "003")
//        val speler4 = maakSpeler("Speler4", "004")
//
//        val speler1Kaart1 = model.Kaart(HARTEN, 7)
//        val speler1Kaart2 = model.Kaart(HARTEN, 9)
//        val speler1Kaart2b = model.Kaart(KLAVER, 6)
//        val speler1Kaart3 = model.Kaart(KLAVER, 7)
//        val speler1Kaart4 = model.Kaart(KLAVER, 10)
//
//        val speler2Kaart1 = model.Kaart(HARTEN, 6)
//        val speler2Kaart1b = model.Kaart(HARTEN, 11)
//        val speler2Kaart2 = model.Kaart(KLAVER, 9)
//        val speler2Kaart3 = model.Kaart(KLAVER, 11)
//        val speler2Kaart4 = model.Kaart(HARTEN, 10)
//
//        val speler3Kaart1 = model.Kaart(RUITEN, 7)
//        val speler3Kaart2 = model.Kaart(RUITEN, 9)
//        val speler3Kaart3 = model.Kaart(RUITEN, 11)
//        val speler3Kaart4 = model.Kaart(SCHOPPEN, 10)
//
//        val speler4Kaart1 = model.Kaart(SCHOPPEN, 7)
//        val speler4Kaart1b = model.Kaart(RUITEN, 6)
//        val speler4Kaart2 = model.Kaart(SCHOPPEN, 9)
//        val speler4Kaart3 = model.Kaart(SCHOPPEN, 11)
//        val speler4Kaart4 = model.Kaart(RUITEN, 8)
//
//        SpelerService.nieuwSpel(speler1, 15)
//        SpelerService.nieuwSpel(speler2, 15)
//        SpelerService.nieuwSpel(speler3, 15)
//        SpelerService.nieuwSpel(speler4, 15)
//
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        val tafel = model.Tafel(1)
//
//        tafel.spelers = listOf(speler1, speler2, speler3, speler4).toMutableList()
//        tafel.opkomer = speler1
//        tafel.huidigeSpeler = speler1
//        tafel.inzet = 1
//        model.SpelContext.spelData.tafels = listOf(tafel).toMutableList()
//
//        // in deze test: iedereen begint met 6 punten
//        speler1.totaalLucifers = 7
//        speler2.totaalLucifers = 7
//        speler3.totaalLucifers = 7
//        speler4.totaalLucifers = 7
//
//        // eerste ronde, eerste slag
//        // ongeldige acties
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.toep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
//        assertThat(SpelerService.toep(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
//        assertThat(SpelerService.toep(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
//        assertThat(SpelerService.gaMeeMetToep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.gaMeeMetToep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.gaMeeMetToep(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.gaMeeMetToep(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.pas(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.pas(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.pas(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.pas(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.speelKaart(speler1, speler2Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Deze kaart zit niet in de hand"))
//
//        // geldig actie
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // ongeldig acties na eerste uitgelegde kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.toep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
//
//        // tweede speler kan spelen
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 3 en 4 kunnen ook spelen
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // ronde afgelopen, niemand mag meer inzetten
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt al een kaart gespeeld"))
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//
//        // speler 1 heeft gewonnen, en alleen hij mag de slag pakken
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // check score, iedereen staat nog op 15
//        assertThat(speler1.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(7)
//
//        // en iedereen heeft nog 1 lucifer ingezet
//        assertThat(speler1.ingezetteLucifers).isEqualTo(1)
//        assertThat(speler2.ingezetteLucifers).isEqualTo(1)
//        assertThat(speler3.ingezetteLucifers).isEqualTo(1)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(1)
//
//        // speler 1 moet beginnen, de rest mag niet
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
//
//        // de vorige kaart kun je niet meer spelen
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Deze kaart zit niet in de hand"))
//
//        // een nieuwe kaart nieuwe wel
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // controleer de punten en ingezette lucifers
//        assertThat(speler1.ingezetteLucifers).isEqualTo(2)
//        assertThat(speler2.ingezetteLucifers).isEqualTo(2)
//        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(2)
//
//        assertThat(speler1.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(7)
//        assertThat(speler3.totaalLucifers).isEqualTo(6)
//        assertThat(speler4.totaalLucifers).isEqualTo(7)
//
//        // speler 2 heeft gewonnen, dus alleen die kan de slag pakken
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // controleer de punten en ingezette lucifers, die moeten nog hetzelfde zijn
//        assertThat(speler1.ingezetteLucifers).isEqualTo(2)
//        assertThat(speler2.ingezetteLucifers).isEqualTo(2)
//        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(2)
//
//        assertThat(speler1.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(7)
//        assertThat(speler3.totaalLucifers).isEqualTo(6)
//        assertThat(speler4.totaalLucifers).isEqualTo(7)
//
//        // RONDE 3
//        // speler 2 toept meteen
//        assertThat(SpelerService.toep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 3 toept over
//        assertThat(SpelerService.toep(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // spel 1 toep nog een keer over
//        assertThat(SpelerService.toep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 2 gaat mee
//        assertThat(SpelerService.gaMeeMetToep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 4 past (en verliest 4 lucifers)
//        assertThat(SpelerService.pas(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // controleer de punten en ingezette lucifers, die moeten nog hetzelfde zijn
//        assertThat(speler1.ingezetteLucifers).isEqualTo(5)
//        assertThat(speler2.ingezetteLucifers).isEqualTo(5)
//        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(0)
//
//        assertThat(speler1.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(7)
//        assertThat(speler3.totaalLucifers).isEqualTo(6)
//        assertThat(speler4.totaalLucifers).isEqualTo(3)
//
//        // speler 1 en 2 doen nog mee, speler 2 moet nog zn kaart spelen
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // laatste slag
//        assertThat(speler1.kaarten).hasSize(1)
//        assertThat(speler2.kaarten).hasSize(1)
//        assertThat(speler3.kaarten).hasSize(2)
//        assertThat(speler4.kaarten).hasSize(2)
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(speler1.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(2)
//        assertThat(speler3.totaalLucifers).isEqualTo(6)
//        assertThat(speler4.totaalLucifers).isEqualTo(3)
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        // speler 1 heeft gewonnen, dus speler 2 moet beginnen
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 3 en 4 spelen een kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 1 toept
//        assertThat(SpelerService.toep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 1,2 en 3 passen
//        assertThat(SpelerService.pas(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 1 wint direct de slag
//        assertThat(speler1.totaalLucifers).isEqualTo(7)
//        assertThat(speler2.totaalLucifers).isEqualTo(1)
//        assertThat(speler3.totaalLucifers).isEqualTo(5)
//        assertThat(speler4.totaalLucifers).isEqualTo(2)
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2b, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1b, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        // speler 1 heeft gewonnen, dus speler 2 moet beginnen
//        // speler 1 toept, speler 2 gaat automatisch mee, en speler 3 en 4 passen
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1b)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 2 wint de slag, met toep.
//        assertThat(speler1.ingezetteLucifers).isEqualTo(2)
//        assertThat(speler2.ingezetteLucifers).isEqualTo(1) // had nog maar 1 lucifer
//        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(0)
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2b)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // slag 3
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // slag 4
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(tafel.slagWinnaar).isEqualTo(speler2)
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 2 wint deze ronde
//        assertThat(speler1.totaalLucifers).isEqualTo(5)
//        assertThat(speler2.totaalLucifers).isEqualTo(1)
//        assertThat(speler3.totaalLucifers).isEqualTo(4)
//        assertThat(speler4.totaalLucifers).isEqualTo(1)
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1b, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        // speler 2 heeft gewonnen, dus speler 3 moet beginnen
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1b)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //2e kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //3e kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //4e kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 3 wint deze ronde
//        assertThat(speler1.totaalLucifers).isEqualTo(4)
//        assertThat(speler2.totaalLucifers).isEqualTo(0)
//        assertThat(speler3.totaalLucifers).isEqualTo(4)
//        assertThat(speler4.totaalLucifers).isEqualTo(0)
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        assertThat(speler1.actiefInSpel).isTrue()
//        assertThat(speler2.actiefInSpel).isFalse()
//        assertThat(speler3.actiefInSpel).isTrue()
//        assertThat(speler4.actiefInSpel).isFalse()
//
//        // speler 3 had gewonnen, speler 4 is af, dus speler 1 moet beginnen
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //2e kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //3e kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //4e kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 1 wint deze ronde
//        assertThat(speler1.totaalLucifers).isEqualTo(4)
//        assertThat(speler2.totaalLucifers).isEqualTo(0)
//        assertThat(speler3.totaalLucifers).isEqualTo(3)
//        assertThat(speler4.totaalLucifers).isEqualTo(0)
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        // speler 1 had gewonnen, speler 3 moet beginnen
//        assertThat(SpelerService.toep(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        // speler 1 wint meteen
//
//        assertThat(speler1.totaalLucifers).isEqualTo(4)
//        assertThat(speler2.totaalLucifers).isEqualTo(0)
//        assertThat(speler3.totaalLucifers).isEqualTo(1)
//        assertThat(speler4.totaalLucifers).isEqualTo(0)
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//
//        // speler 3 begint
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //2e kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //3e kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //4e kaart
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 3 wint
//        assertThat(speler1.totaalLucifers).isEqualTo(3)
//        assertThat(speler2.totaalLucifers).isEqualTo(0)
//        assertThat(speler3.totaalLucifers).isEqualTo(1)
//        assertThat(speler4.totaalLucifers).isEqualTo(0)
//
//        assertThat(speler2.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(0)
//
//
//        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        // speler 1 begint
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //2e kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //3e kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        //4e kaart
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        // speler 1 wint
//        assertThat(speler1.totaalLucifers).isEqualTo(3)
//        assertThat(speler2.totaalLucifers).isEqualTo(0)
//        assertThat(speler3.totaalLucifers).isEqualTo(0)
//        assertThat(speler4.totaalLucifers).isEqualTo(0)
//
//        assertThat(speler1.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler2.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
//        assertThat(speler4.ingezetteLucifers).isEqualTo(0)
//
//        assertThat(tafel.tafelWinnaar).isEqualTo(speler1)
//        assertThat(tafel.huidigeSpeler).isNull()
//    }
//
//
//    @Test
//    fun testSpelDoorgaanAlsIedereenAutomatischMeegaatMetToep() {
//        val speler1 = maakSpeler("Speler1", "001")
//        val speler2 = maakSpeler("Speler2", "002")
//        val speler3 = maakSpeler("Speler3", "003")
//        val speler4 = maakSpeler("Speler4", "004")
//
//        val speler1Kaart1 = model.Kaart(HARTEN, 7)
//        val speler1Kaart2 = model.Kaart(HARTEN, 9)
//        val speler1Kaart3 = model.Kaart(KLAVER, 7)
//        val speler1Kaart4 = model.Kaart(KLAVER, 10)
//
//        val speler2Kaart1 = model.Kaart(HARTEN, 6)
//        val speler2Kaart2 = model.Kaart(KLAVER, 9)
//        val speler2Kaart3 = model.Kaart(KLAVER, 11)
//        val speler2Kaart4 = model.Kaart(HARTEN, 10)
//
//        val speler3Kaart1 = model.Kaart(RUITEN, 7)
//        val speler3Kaart2 = model.Kaart(RUITEN, 9)
//        val speler3Kaart3 = model.Kaart(RUITEN, 11)
//        val speler3Kaart4 = model.Kaart(SCHOPPEN, 10)
//
//        val speler4Kaart1 = model.Kaart(SCHOPPEN, 7)
//        val speler4Kaart2 = model.Kaart(SCHOPPEN, 9)
//        val speler4Kaart3 = model.Kaart(SCHOPPEN, 11)
//        val speler4Kaart4 = model.Kaart(RUITEN, 8)
//
//        SpelerService.nieuwSpel(speler1, 2)
//        SpelerService.nieuwSpel(speler2, 1)
//        SpelerService.nieuwSpel(speler3, 1)
//        SpelerService.nieuwSpel(speler4, 1)
//
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        val tafel = model.Tafel(1)
//        tafel.spelers = listOf(speler1, speler2, speler3, speler4).toMutableList()
//        tafel.opkomer = speler1
//        tafel.huidigeSpeler = speler1
//        tafel.inzet = 1
//        model.SpelContext.spelData.tafels = listOf(tafel).toMutableList()
//
//
//        // 1e speler toept, de rest gaam automatisch mee
//        assertThat(SpelerService.toep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.gaMeeMetToep(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//        assertThat(SpelerService.gaMeeMetToep(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.FAILED, "Er is niet getoept"))
//
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//    }
//
//    @Test
//    @DisplayName("Als af, dan geen gespeelde kaart mee, en laatste 2 spelers moeten het wel uit kunnen spelen")
//    fun testBug1en2() {
//        val speler1 = maakSpeler("Karen", "001")
//        val speler2 = maakSpeler("Ferry", "002")
//        val speler3 = maakSpeler("Robbert", "003")
//        val speler4 = maakSpeler("Marieke", "004")
//
//        val speler1Kaart1 = model.Kaart(HARTEN, 7)
//        val speler1Kaart2 = model.Kaart(HARTEN, 9)
//        val speler1Kaart3 = model.Kaart(HARTEN, 7)
//        val speler1Kaart4 = model.Kaart(HARTEN, 10)
//
//        val speler2Kaart1 = model.Kaart(SCHOPPEN, 13)
//        val speler2Kaart2 = model.Kaart(KLAVER, 9)
//        val speler2Kaart3 = model.Kaart(KLAVER, 11)
//        val speler2Kaart4 = model.Kaart(KLAVER, 10)
//
//        val speler3Kaart1 = model.Kaart(RUITEN, 7)
//        val speler3Kaart2 = model.Kaart(RUITEN, 9)
//        val speler3Kaart3 = model.Kaart(RUITEN, 11)
//        val speler3Kaart4 = model.Kaart(RUITEN, 10)
//
//        val speler4Kaart1 = model.Kaart(SCHOPPEN, 8)
//        val speler4Kaart2 = model.Kaart(SCHOPPEN, 9)
//        val speler4Kaart3 = model.Kaart(SCHOPPEN, 11)
//        val speler4Kaart4 = model.Kaart(SCHOPPEN, 8)
//
//        SpelerService.nieuwSpel(speler1, 2)
//        SpelerService.nieuwSpel(speler2, 4)
//        SpelerService.nieuwSpel(speler3, 2)
//        SpelerService.nieuwSpel(speler4, 3)
//
//        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
//        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())
//
//        val tafel = model.Tafel(1)
//        tafel.spelers = listOf(speler1, speler2, speler3, speler4).toMutableList()
//        tafel.opkomer = speler2
//        tafel.huidigeSpeler = speler2
//        tafel.inzet = 1
//        model.SpelContext.spelData.tafels = listOf(tafel).toMutableList()
//
//
//        // 1e speler toept, de rest gaam automatisch mee
//        assertThat(SpelerService.toep(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler4, speler4Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        assertThat(speler1.gespeeldeKaart).isEqualTo(null)
//        assertThat(speler2.gespeeldeKaart).isEqualTo(null)
//        assertThat(speler3.gespeeldeKaart).isEqualTo(null)
//        assertThat(speler4.gespeeldeKaart).isEqualTo(null)
//
//
//        // spel moet doorgaan
//        assertThat(speler4.actiefInSpel).isEqualTo(true)
//
//    }
//
//    @Test
//    fun test1(){
//        val speler80785 = maakSpeler("speler.naam", "1")
//        val speler1 =  speler80785
//        var speler80785kaarten: MutableList<model.Kaart> = emptyList<model.Kaart>().toMutableList()
//        speler80785kaarten.add(model.Kaart(KLAVER, 7))
//        speler80785kaarten.add(model.Kaart(SCHOPPEN, 8))
//        speler80785kaarten.add(model.Kaart(KLAVER, 12))
//        speler80785kaarten.add(model.Kaart(RUITEN, 14))
//        SpelerService.nieuwSpel(speler1, 5)
//        SpelerService.nieuweRonde(speler1, speler80785kaarten)
//        val speler58137 = maakSpeler("speler.naam", "2")
//        val speler2 =  speler58137
//        var speler58137kaarten: MutableList<model.Kaart> = emptyList<model.Kaart>().toMutableList()
//        speler58137kaarten.add(model.Kaart(KLAVER, 8))
//        speler58137kaarten.add(model.Kaart(RUITEN, 8))
//        speler58137kaarten.add(model.Kaart(HARTEN, 7))
//        speler58137kaarten.add(model.Kaart(KLAVER, 13))
//        SpelerService.nieuwSpel(speler2, 5)
//        SpelerService.nieuweRonde(speler2, speler58137kaarten)
//        val speler23912 = maakSpeler("speler.naam", "3")
//        val speler3 =  speler23912
//        var speler23912kaarten: MutableList<model.Kaart> = emptyList<model.Kaart>().toMutableList()
//        speler23912kaarten.add(model.Kaart(HARTEN, 9))
//        speler23912kaarten.add(model.Kaart(RUITEN, 13))
//        speler23912kaarten.add(model.Kaart(SCHOPPEN, 7))
//        speler23912kaarten.add(model.Kaart(KLAVER, 10))
//        SpelerService.nieuwSpel(speler3, 5)
//        SpelerService.nieuweRonde(speler3, speler23912kaarten)
//        val speler71976 = maakSpeler("speler.naam", "4")
//        val speler4 =  speler71976
//        var speler71976kaarten: MutableList<model.Kaart> = emptyList<model.Kaart>().toMutableList()
//        speler71976kaarten.add(model.Kaart(KLAVER, 9))
//        speler71976kaarten.add(model.Kaart(RUITEN, 10))
//        speler71976kaarten.add(model.Kaart(SCHOPPEN, 10))
//        speler71976kaarten.add(model.Kaart(HARTEN, 14))
//        SpelerService.nieuwSpel(speler4, 5)
//        SpelerService.nieuweRonde(speler4, speler71976kaarten)
//        val tafel = model.Tafel(1)
//        tafel.spelers = listOf(speler1, speler2, speler3, speler4).toMutableList()
//        tafel.opkomer = speler1
//        tafel.huidigeSpeler = speler1
//        tafel.inzet = 1
//        model.SpelContext.spelData.tafels = listOf(tafel).toMutableList()
//        assertThat(SpelerService.toep(speler80785)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler58137)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler23912)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler71976)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler80785, model.Kaart(RUITEN,14))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler58137, model.Kaart(RUITEN,8))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler23912)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler80785)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler58137)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler23912, model.Kaart(RUITEN,13))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pakSlag(speler80785)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler80785, model.Kaart(SCHOPPEN,8))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler58137)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.gaMeeMetToep(speler23912)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler80785)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.speelKaart(speler58137, model.Kaart(KLAVER,13))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler23912)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler58137)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        speler80785.kaarten.clear()
//        speler80785.kaarten.add(model.Kaart(RUITEN, 10))
//        speler80785.kaarten.add(model.Kaart(RUITEN, 8))
//        speler80785.kaarten.add(model.Kaart(HARTEN, 8))
//        speler80785.kaarten.add(model.Kaart(SCHOPPEN, 14))
//        speler58137.kaarten.clear()
//        speler58137.kaarten.add(model.Kaart(SCHOPPEN, 12))
//        speler58137.kaarten.add(model.Kaart(KLAVER, 9))
//        speler58137.kaarten.add(model.Kaart(KLAVER, 10))
//        speler58137.kaarten.add(model.Kaart(RUITEN, 13))
//        speler23912.kaarten.clear()
//        speler23912.kaarten.add(model.Kaart(RUITEN, 12))
//        speler23912.kaarten.add(model.Kaart(KLAVER, 14))
//        speler23912.kaarten.add(model.Kaart(KLAVER, 12))
//        speler23912.kaarten.add(model.Kaart(HARTEN, 14))
//        speler71976.kaarten.clear()
//        speler71976.kaarten.add(model.Kaart(SCHOPPEN, 7))
//        speler71976.kaarten.add(model.Kaart(SCHOPPEN, 8))
//        speler71976.kaarten.add(model.Kaart(SCHOPPEN, 11))
//        speler71976.kaarten.add(model.Kaart(HARTEN, 13))
//        assertThat(SpelerService.speelKaart(speler71976, model.Kaart(SCHOPPEN,11))).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler80785)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.pas(speler23912)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//        assertThat(SpelerService.toep(speler71976)).isEqualTo(model.CommandResult(model.CommandStatus.SUCCEDED, ""))
//
//        println(tafel.tafelWinnaar)
//
//
//    }
//
//    /*
//
//
//NIEUW SPEL
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=HARTEN, waarde=12) ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=11), model.Kaart(symbool=KLAVER, waarde=7), model.Kaart(symbool=HARTEN, waarde=10)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=KLAVER, waarde=10) ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=RUITEN, waarde=13), model.Kaart(symbool=RUITEN, waarde=10)]
//Pak slag : model.Speler Karen ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=11), model.Kaart(symbool=KLAVER, waarde=7), model.Kaart(symbool=HARTEN, waarde=10)]
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=HARTEN, waarde=10) ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=11), model.Kaart(symbool=KLAVER, waarde=7)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=RUITEN, waarde=13) ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=RUITEN, waarde=10)]
//Pak slag : model.Speler Karen ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=11), model.Kaart(symbool=KLAVER, waarde=7)]
//Toep : model.Speler Karen ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=11), model.Kaart(symbool=KLAVER, waarde=7)]
//NIEUWE RONDE, tafel: 1
//NIEUWE RONDE, actief, speler: Karen :[model.Kaart(symbool=SCHOPPEN, waarde=12), model.Kaart(symbool=HARTEN, waarde=10), model.Kaart(symbool=SCHOPPEN, waarde=8), model.Kaart(symbool=RUITEN, waarde=13)]
//NIEUWE RONDE, actief, speler: Ferry :[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=KLAVER, waarde=7), model.Kaart(symbool=SCHOPPEN, waarde=13)]
//Pas: model.Speler Ferry ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=KLAVER, waarde=7), model.Kaart(symbool=SCHOPPEN, waarde=13)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=SCHOPPEN, waarde=13) ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=KLAVER, waarde=7)]
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=SCHOPPEN, waarde=8) ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=12), model.Kaart(symbool=HARTEN, waarde=10), model.Kaart(symbool=RUITEN, waarde=13)]
//Pak slag : model.Speler Ferry ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=KLAVER, waarde=7)]
//Toep : model.Speler Ferry ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=KLAVER, waarde=7)]
//Ga mee: model.Speler Karen ,  hand:[model.Kaart(symbool=SCHOPPEN, waarde=12), model.Kaart(symbool=HARTEN, waarde=10), model.Kaart(symbool=RUITEN, waarde=13)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=SCHOPPEN, waarde=7) ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=KLAVER, waarde=7)]
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=SCHOPPEN, waarde=12) ,  hand:[model.Kaart(symbool=HARTEN, waarde=10), model.Kaart(symbool=RUITEN, waarde=13)]
//Pak slag : model.Speler Karen ,  hand:[model.Kaart(symbool=HARTEN, waarde=10), model.Kaart(symbool=RUITEN, waarde=13)]
//Toep : model.Speler Karen ,  hand:[model.Kaart(symbool=HARTEN, waarde=10), model.Kaart(symbool=RUITEN, waarde=13)]
//Ga mee: model.Speler Ferry ,  hand:[model.Kaart(symbool=RUITEN, waarde=8), model.Kaart(symbool=KLAVER, waarde=7)]
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=HARTEN, waarde=10) ,  hand:[model.Kaart(symbool=RUITEN, waarde=13)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=KLAVER, waarde=7) ,  hand:[model.Kaart(symbool=RUITEN, waarde=8)]
//Pak slag : model.Speler Karen ,  hand:[model.Kaart(symbool=RUITEN, waarde=13)]
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=RUITEN, waarde=13) ,  hand:[]
//Toep : model.Speler Ferry ,  hand:[model.Kaart(symbool=RUITEN, waarde=8)]
//NIEUWE RONDE, tafel: 1
//NIEUWE RONDE, actief, speler: Karen :[model.Kaart(symbool=RUITEN, waarde=12), model.Kaart(symbool=SCHOPPEN, waarde=9), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=RUITEN, waarde=8)]
//NIEUWE RONDE, actief, speler: Ferry :[model.Kaart(symbool=HARTEN, waarde=13), model.Kaart(symbool=HARTEN, waarde=14), model.Kaart(symbool=SCHOPPEN, waarde=10), model.Kaart(symbool=RUITEN, waarde=9)]
//Pas: model.Speler Karen ,  hand:[model.Kaart(symbool=RUITEN, waarde=12), model.Kaart(symbool=SCHOPPEN, waarde=9), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=RUITEN, waarde=8)]
//Speelkaart: model.Speler Karen kaart: model.Kaart(symbool=SCHOPPEN, waarde=9) ,  hand:[model.Kaart(symbool=RUITEN, waarde=12), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=RUITEN, waarde=8)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=SCHOPPEN, waarde=10) ,  hand:[model.Kaart(symbool=HARTEN, waarde=13), model.Kaart(symbool=HARTEN, waarde=14), model.Kaart(symbool=RUITEN, waarde=9)]
//Pak slag : model.Speler Ferry ,  hand:[model.Kaart(symbool=HARTEN, waarde=13), model.Kaart(symbool=HARTEN, waarde=14), model.Kaart(symbool=RUITEN, waarde=9)]
//Speelkaart: model.Speler Ferry kaart: model.Kaart(symbool=HARTEN, waarde=13) ,  hand:[model.Kaart(symbool=HARTEN, waarde=14), model.Kaart(symbool=RUITEN, waarde=9)]
//Toep : model.Speler Karen ,  hand:[model.Kaart(symbool=RUITEN, waarde=12), model.Kaart(symbool=SCHOPPEN, waarde=7), model.Kaart(symbool=RUITEN, waarde=8)]
//Toep : model.Speler Ferry ,  hand:[model.Kaart(symbool=HARTEN, waarde=14), model.Kaart(symbool=RUITEN, waarde=9)]
//
//    */
//
//    fun maakSpeler(naam: String, id: String): model.Speler {
//        val speler = model.Speler()
//        speler.naam = naam
//        speler.id = id
//        speler.wilMeedoen = true
//        return speler
//    }
//
//
//}
