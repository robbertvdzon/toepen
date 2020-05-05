abstract class Command{
    val lock = Object()
    var result:CommandResult? = null
    abstract fun process():CommandResult
}

class SpeelKaartCommand(val speler:Speler,val kaart:Kaart): Command(){
    override fun process():CommandResult = SpelerService.speelKaart(speler, kaart)
}

class PakSlagCommand(val speler:Speler): Command(){
    override fun process():CommandResult = SpelerService.pakSlag(speler)
}

class ToepCommand(val speler:Speler): Command(){
    override fun process():CommandResult = SpelerService.toep(speler)
}

class GaMeeMetToepCommand(val speler:Speler): Command(){
    override fun process():CommandResult = SpelerService.gaMeeMetToep(speler)
}

class PasCommand(val speler:Speler): Command(){
    override fun process():CommandResult = SpelerService.pas(speler)
}

class LoadDataCommand(): Command(){
    override fun process():CommandResult = Administrator.loadData()
}

class SaveDataCommand(): Command(){
    override fun process():CommandResult = Administrator.saveData()
}

class MaakNieuweTafelsCommand(private val aantal:Int, val startscore:Int): Command(){
    override fun process():CommandResult = Administrator.maakNieuweTafels(aantal, startscore)
}

class UpdateGebruikersCommand(val gebruikers:List<Speler>): Command(){
    override fun process():CommandResult = Administrator.updateGebruikers(gebruikers)
}

class ClearLog(): Command(){
    override fun process():CommandResult = Administrator.clearLog()
}

class ResetScore(): Command(){
    override fun process():CommandResult = Administrator.resetScore()
}
class AllesPauzeren(): Command(){
    override fun process():CommandResult = Administrator.allesPauzeren()
}
class AllesStarten(): Command(){
    override fun process():CommandResult = Administrator.allesStarten()
}
class NieuwSpel(val startscore:Int, val tafel:Tafel?): Command(){
    override fun process():CommandResult = Administrator.nieuwSpel(startscore, tafel)
}
class SchopTafel(val tafel:Tafel?): Command(){
    override fun process():CommandResult = Administrator.schopTafel(tafel)
}
class PauzeerTafel(val tafel:Tafel?): Command(){
    override fun process():CommandResult = Administrator.pauzeerTafel(tafel)
}
class StartTafel(val tafel:Tafel?): Command(){
    override fun process():CommandResult = Administrator.startTafel(tafel)
}


