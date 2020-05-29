package spelprocessor

import spellogica.Administrator
import model.CommandResult
import model.CommandStatus
import model.Gebruiker
import model.Kaart
import model.SpelContext
import util.ToepRandom
import com.fasterxml.jackson.annotation.JsonIgnore
import spellogica.ToepSpel

abstract class Command{
    @JsonIgnore
    val lock = Object()
    @JsonIgnore
    var result: CommandResult? = null
    abstract fun process(): CommandResult
}

class SpeelKaartCommand(val spelerId:String,val kaart: Kaart): Command(){
    override fun process(): CommandResult {
        val speler = SpelContext.findSpeler(spelerId)
        if (speler==null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
        return ToepSpel.speelKaart(speler, kaart)
    }
}

class PakSlagCommand(val spelerId:String): Command(){
    override fun process(): CommandResult {
        val speler = SpelContext.findSpeler(spelerId)
        if (speler==null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
        return ToepSpel.pakSlag(speler)
    }
}

class ToepCommand(val spelerId:String): Command(){
    override fun process(): CommandResult {
        val speler = SpelContext.findSpeler(spelerId)
        if (speler==null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
        return ToepSpel.toep(speler)
    }
}

class GaMeeMetToepCommand(val spelerId:String): Command(){
    override fun process(): CommandResult {
        val speler = SpelContext.findSpeler(spelerId)
        if (speler==null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
        return ToepSpel.gaMeeMetToep(speler)
    }
}

class PasCommand(val spelerId:String): Command(){
    override fun process(): CommandResult {
        val speler = SpelContext.findSpeler(spelerId)
        if (speler==null) return CommandResult(CommandStatus.FAILED, "Je zit niet aan een tafel")
        return ToepSpel.pas(speler)
    }
}

class LoadDataCommand(): Command(){
    override fun process(): CommandResult = Administrator.loadData()
}

class SaveDataCommand(): Command(){
    override fun process(): CommandResult = Administrator.saveData()
}

class MaakNieuweTafelsCommand(val aantal:Int, val startscore:Int): Command(){
    override fun process(): CommandResult = Administrator.maakNieuweTafels(aantal, startscore)
}

class UpdateGebruikersCommand(val gebruikers:List<Gebruiker>): Command(){
    override fun process(): CommandResult = Administrator.updateGebruikers(gebruikers)
}

class ClearLog(): Command(){
    override fun process(): CommandResult = Administrator.clearLog()
}

class ResetScore(): Command(){
    override fun process(): CommandResult = Administrator.resetScore()
}
class AllesPauzeren(): Command(){
    override fun process(): CommandResult = Administrator.allesPauzeren()
}
class AllesStarten(): Command(){
    override fun process(): CommandResult = Administrator.allesStarten()
}
class NieuwSpel(val startscore:Int, val tafelNr:Int?): Command(){
    override fun process(): CommandResult {
        val tafel = SpelContext.spelData.tafels.firstOrNull{it.tafelNr==tafelNr}
        return Administrator.nieuwSpel(startscore, tafel)

    }
}
class SchopTafel(val tafelNr:Int): Command(){
    override fun process(): CommandResult {
        val tafel = SpelContext.spelData.tafels.firstOrNull{it.tafelNr==tafelNr}
        return Administrator.schopTafel(tafel)
    }
}
class PauzeerTafel(val tafelNr:Int): Command(){
    override fun process(): CommandResult {
        val tafel = SpelContext.spelData.tafels.firstOrNull{it.tafelNr==tafelNr}
        return Administrator.pauzeerTafel(tafel)
    }
}
class StartTafel(val tafelNr:Int): Command(){
    override fun process(): CommandResult {
        val tafel = SpelContext.spelData.tafels.firstOrNull{it.tafelNr==tafelNr}
        return Administrator.startTafel(tafel)
    }
}
class SetRandomSeed(val seed:Long): Command(){
    override fun process(): CommandResult {
        ToepRandom.setSeed(seed)
        return CommandResult(CommandStatus.SUCCEDED, "")
    }
}


