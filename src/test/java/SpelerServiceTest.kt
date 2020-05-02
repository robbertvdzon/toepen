import KaartSymbool.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SpelerServiceTest {

    @Test
    fun testNieuweTafels() {
        val speler1 = maakSpeler("Speler1", "001")
        val speler2 = maakSpeler("Speler2", "002")
        val speler3 = maakSpeler("Speler3", "003")
        val speler4 = maakSpeler("Speler4", "004")
        val speler5 = maakSpeler("Speler5", "005")
        Administrator.updateGebruikers(listOf(speler1, speler2, speler3, speler4, speler5))
        Administrator.maakNieuweTafels(2)
        val tafels = SpelContext.spelData.tafels
        assertThat(tafels).hasSize(2);
        assertThat(tafels[0].spelers).hasSize(3)
        assertThat(tafels[0].opkomer).isNotNull()
        assertThat(tafels[0].opkomer).isEqualTo(tafels[0].huidigeSpeler)
        assertThat(tafels[0].toeper).isNull()
        assertThat(tafels[0].slagWinnaar).isNull()
        assertThat(tafels[0].tafelWinnaar).isNull()

        assertThat(tafels[1].spelers).hasSize(2)
        assertThat(tafels[1].opkomer).isNotNull()
        assertThat(tafels[1].opkomer).isEqualTo(tafels[1].huidigeSpeler)
        assertThat(tafels[1].toeper).isNull()
        assertThat(tafels[1].slagWinnaar).isNull()
        assertThat(tafels[1].tafelWinnaar).isNull()
    }

    @Test
    fun testSpel1() {
        val speler1 = maakSpeler("Speler1", "001")
        val speler2 = maakSpeler("Speler2", "002")
        val speler3 = maakSpeler("Speler3", "003")
        val speler4 = maakSpeler("Speler4", "004")

        val speler1Kaart1 = Kaart(HARTEN, 7)
        val speler1Kaart2 = Kaart(HARTEN, 9)
        val speler1Kaart3 = Kaart(HARTEN, 11)
        val speler1Kaart4 = Kaart(KLAVER, 10)

        val speler2Kaart1 = Kaart(KLAVER, 7)
        val speler2Kaart2 = Kaart(KLAVER, 9)
        val speler2Kaart3 = Kaart(KLAVER, 11)
        val speler2Kaart4 = Kaart(HARTEN, 10)

        val speler3Kaart1 = Kaart(RUITEN, 7)
        val speler3Kaart2 = Kaart(RUITEN, 9)
        val speler3Kaart3 = Kaart(RUITEN, 11)
        val speler3Kaart4 = Kaart(SCHOPPEN, 10)

        val speler4Kaart1 = Kaart(SCHOPPEN, 7)
        val speler4Kaart2 = Kaart(SCHOPPEN, 9)
        val speler4Kaart3 = Kaart(SCHOPPEN, 11)
        val speler4Kaart4 = Kaart(RUITEN, 10)

        SpelerService.nieuwSpel(speler1)
        SpelerService.nieuwSpel(speler2)
        SpelerService.nieuwSpel(speler3)
        SpelerService.nieuwSpel(speler4)

        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        val tafel = Tafel(1)
        tafel.spelers = listOf(speler1, speler2, speler3, speler4).toMutableList()
        tafel.opkomer = speler1
        tafel.huidigeSpeler = speler1
        tafel.inzet = 1
        SpelContext.spelData.tafels = listOf(tafel).toMutableList()

        // in deze test: iedereen begint met 6 punten
        speler1.totaalLucifers = 7
        speler2.totaalLucifers = 7
        speler3.totaalLucifers = 7
        speler4.totaalLucifers = 7

        // eerste ronde, eerste slag
        // ongeldige acties
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.toep(speler2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
        assertThat(SpelerService.toep(speler3)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
        assertThat(SpelerService.toep(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))
        assertThat(SpelerService.gaMeeMetToep(speler1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.gaMeeMetToep(speler2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.gaMeeMetToep(speler3)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.gaMeeMetToep(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.pas(speler1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.pas(speler2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.pas(speler3)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.pas(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Er is niet getoept"))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.speelKaart(speler1, speler2Kaart1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Deze kaart zit niet in de hand"))

        // geldig actie
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // ongeldig acties na eerste uitgelegde kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.toep(speler1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om te toepen"))

        // tweede speler kan spelen
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 3 en 4 ook spelen
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // rond afgelopen, niemand mag meer inzetten
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt al een kaart gespeeld"))

        // speler 1 heeft gewonnen, en alleen hij mag de slag pakken
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // check score, iedereen staat nog op 15
        assertThat(speler1.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(7)

        // en iedereen heeft nog 1 lucifer ingezet
        assertThat(speler1.ingezetteLucifers).isEqualTo(1)
        assertThat(speler2.ingezetteLucifers).isEqualTo(1)
        assertThat(speler3.ingezetteLucifers).isEqualTo(1)
        assertThat(speler4.ingezetteLucifers).isEqualTo(1)

        // speler 1 moet beginnen, de rest mag niet
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om een kaart te spelen"))

        // de vorige kaart kun je niet meer spelen
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Deze kaart zit niet in de hand"))

        // een nieuwe kaart nieuwe wel
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 2 moet nu toepen
        assertThat(SpelerService.toep(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 4 moet wachten op zijn beurt om mee te gaan
        assertThat(SpelerService.gaMeeMetToep(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je bent nog niet aan de beurt om mee te gaan"))

        // speler 3 moet mee gaan, passen of overtoepen, hij mag niet een kaart spelen
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.FAILED, "Nog niet iedereen heeft zijn toep keuze opgegeven"))
        assertThat(SpelerService.pas(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 4 en 1 gaan mee
        assertThat(SpelerService.gaMeeMetToep(speler4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.gaMeeMetToep(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // alleen speler 1, 2 en 4 doen nog mee, en speler 2 had getoept
        // speler 2 en 4 moeten nog een kaart spelen
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // controleer de punten en ingezette lucifers
        assertThat(speler1.ingezetteLucifers).isEqualTo(2)
        assertThat(speler2.ingezetteLucifers).isEqualTo(2)
        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
        assertThat(speler4.ingezetteLucifers).isEqualTo(2)

        assertThat(speler1.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(7)
        assertThat(speler3.totaalLucifers).isEqualTo(6)
        assertThat(speler4.totaalLucifers).isEqualTo(7)

        // speler 2 heeft gewonnen, dus alleen die kan de slag pakken
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler4)).isEqualTo(CommandResult(CommandStatus.FAILED, "Je hebt deze slag niet gewonnen"))
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // controleer de punten en ingezette lucifers, die moeten nog hetzelfde zijn
        assertThat(speler1.ingezetteLucifers).isEqualTo(2)
        assertThat(speler2.ingezetteLucifers).isEqualTo(2)
        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
        assertThat(speler4.ingezetteLucifers).isEqualTo(2)

        assertThat(speler1.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(7)
        assertThat(speler3.totaalLucifers).isEqualTo(6)
        assertThat(speler4.totaalLucifers).isEqualTo(7)

        // RONDE 3
        // speler 2 toept meteen
        assertThat(SpelerService.toep(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 3 toept over
        assertThat(SpelerService.toep(speler4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // spel 1 toep nog een keer over
        assertThat(SpelerService.toep(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 2 gaat mee
        assertThat(SpelerService.gaMeeMetToep(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 4 past (en verliest 4 lucifers)
        assertThat(SpelerService.pas(speler4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // controleer de punten en ingezette lucifers, die moeten nog hetzelfde zijn
        assertThat(speler1.ingezetteLucifers).isEqualTo(5)
        assertThat(speler2.ingezetteLucifers).isEqualTo(5)
        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
        assertThat(speler4.ingezetteLucifers).isEqualTo(0)

        assertThat(speler1.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(7)
        assertThat(speler3.totaalLucifers).isEqualTo(6)
        assertThat(speler4.totaalLucifers).isEqualTo(3)

        // speler 1 en 2 doen nog mee, speler 2 moet nog zn kaart spelen
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // laatste slag
        assertThat(speler1.kaarten).hasSize(1)
        assertThat(speler2.kaarten).hasSize(1)
        assertThat(speler3.kaarten).hasSize(3)
        assertThat(speler4.kaarten).hasSize(2)

        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        assertThat(speler1.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(2)
        assertThat(speler3.totaalLucifers).isEqualTo(6)
        assertThat(speler4.totaalLucifers).isEqualTo(3)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        // speler 1 heeft gewonnen, dus speler 2 moet beginnen
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 3 en 4 spelen een kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 1 toept
        assertThat(SpelerService.toep(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 1,2 en 3 passen
        assertThat(SpelerService.pas(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pas(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pas(speler4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 1 wint direct de slag
        assertThat(speler1.totaalLucifers).isEqualTo(7)
        assertThat(speler2.totaalLucifers).isEqualTo(1)
        assertThat(speler3.totaalLucifers).isEqualTo(5)
        assertThat(speler4.totaalLucifers).isEqualTo(2)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        // speler 1 heeft gewonnen, dus speler 2 moet beginnen
        // speler 1 toept, speler 2 gaat automatisch mee, en speler 3 en 4 passen
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.toep(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pas(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pas(speler4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 2 wint de slag, met toep.
        assertThat(speler1.ingezetteLucifers).isEqualTo(2)
        assertThat(speler2.ingezetteLucifers).isEqualTo(1) // had nog maar 1 lucifer
        assertThat(speler3.ingezetteLucifers).isEqualTo(0)
        assertThat(speler4.ingezetteLucifers).isEqualTo(0)

        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // slag 3
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // slag 4
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(tafel.slagWinnaar).isEqualTo(speler2)
        assertThat(SpelerService.pakSlag(speler2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 2 wint deze ronde
        assertThat(speler1.totaalLucifers).isEqualTo(5)
        assertThat(speler2.totaalLucifers).isEqualTo(1)
        assertThat(speler3.totaalLucifers).isEqualTo(4)
        assertThat(speler4.totaalLucifers).isEqualTo(1)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        // speler 2 heeft gewonnen, dus speler 3 moet beginnen
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //2e kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //3e kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //4e kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler4, speler4Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler2, speler2Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 3 wint deze ronde
        assertThat(speler1.totaalLucifers).isEqualTo(4)
        assertThat(speler2.totaalLucifers).isEqualTo(0)
        assertThat(speler3.totaalLucifers).isEqualTo(4)
        assertThat(speler4.totaalLucifers).isEqualTo(0)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        assertThat(speler1.actiefInSpel).isTrue()
        assertThat(speler2.actiefInSpel).isFalse()
        assertThat(speler3.actiefInSpel).isTrue()
        assertThat(speler4.actiefInSpel).isFalse()

        // speler 3 had gewonnen, speler 4 is af, dus speler 1 moet beginnen
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //2e kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //3e kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //4e kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 1 wint deze ronde
        assertThat(speler1.totaalLucifers).isEqualTo(4)
        assertThat(speler2.totaalLucifers).isEqualTo(0)
        assertThat(speler3.totaalLucifers).isEqualTo(3)
        assertThat(speler4.totaalLucifers).isEqualTo(0)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        // speler 1 had gewonnen, speler 3 moet beginnen
        assertThat(SpelerService.toep(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.toep(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pas(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        // speler 1 wint meteen

        assertThat(speler1.totaalLucifers).isEqualTo(4)
        assertThat(speler2.totaalLucifers).isEqualTo(0)
        assertThat(speler3.totaalLucifers).isEqualTo(1)
        assertThat(speler4.totaalLucifers).isEqualTo(0)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())


        // speler 3 begint
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //2e kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //3e kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //4e kaart
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 3 wint
        assertThat(speler1.totaalLucifers).isEqualTo(3)
        assertThat(speler2.totaalLucifers).isEqualTo(0)
        assertThat(speler3.totaalLucifers).isEqualTo(1)
        assertThat(speler4.totaalLucifers).isEqualTo(0)

        // NIEUWE RONDE, geeft iedereen weer dezelfde kaarten
        SpelerService.nieuweRonde(speler1, listOf(speler1Kaart1, speler1Kaart2, speler1Kaart3, speler1Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler2, listOf(speler2Kaart1, speler2Kaart2, speler2Kaart3, speler2Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler3, listOf(speler3Kaart1, speler3Kaart2, speler3Kaart3, speler3Kaart4).toMutableList())
        SpelerService.nieuweRonde(speler4, listOf(speler4Kaart1, speler4Kaart2, speler4Kaart3, speler4Kaart4).toMutableList())

        // speler 1 begint
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //2e kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart2)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //3e kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart3)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        //4e kaart
        assertThat(SpelerService.speelKaart(speler1, speler1Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.speelKaart(speler3, speler3Kaart4)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))
        assertThat(SpelerService.pakSlag(speler1)).isEqualTo(CommandResult(CommandStatus.SUCCEDED, ""))

        // speler 1 wint
        assertThat(speler1.totaalLucifers).isEqualTo(3)
        assertThat(speler2.totaalLucifers).isEqualTo(0)
        assertThat(speler3.totaalLucifers).isEqualTo(0)
        assertThat(speler4.totaalLucifers).isEqualTo(0)

        assertThat(tafel.tafelWinnaar).isEqualTo(speler1)
        assertThat(tafel.huidigeSpeler).isNull()


    }

    fun maakSpeler(naam: String, id: String): Speler {
        val speler = Speler()
        speler.naam = naam
        speler.id = id
        speler.wilMeedoen = true
        return speler
    }


}
