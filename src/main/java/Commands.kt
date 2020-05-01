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

class NewOpmerkingCommand(val speler:Speler, val opmerking:String): Command(){
    override fun process():CommandResult = SpelerService.nieuweOpmerking(speler, opmerking)
}

class LoadDataCommand(): Command(){
    override fun process():CommandResult = Administrator.loadData()
}

class SaveDataCommand(): Command(){
    override fun process():CommandResult = Administrator.saveData()
}

class MaakNieuweTafelsCommand(): Command(){
    override fun process():CommandResult = Administrator.maakNieuweTafels()
}

class UpdateGebruikersCommand(val gebruikers:List<Speler>): Command(){
    override fun process():CommandResult = Administrator.updateGebruikers(gebruikers)
}

class SetAantalTafelsCommand(val aantalTafels:Int): Command(){
    override fun process():CommandResult = Administrator.setAantalTafels(aantalTafels)
}

class SetAantalKaartenInDeckCommand(val aantalKaarten:Int): Command(){
    override fun process():CommandResult = Administrator.setAantalKaartenInDeck(aantalKaarten)
}

class ClearOpmerkingenCommand(val aantalKaarten:Int): Command(){
    override fun process():CommandResult = Administrator.clearOpmerkingen()
}
