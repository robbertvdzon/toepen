import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.plugin.rendering.vue.VueComponent
import org.slf4j.LoggerFactory

object Toepen {
    private val log = LoggerFactory.getLogger("Toepen")


    @JvmStatic
    fun main(args: Array<String>) {
        CommandQueue.processCommands()

        val spelers = (1..40).map { maakInitieleSpeler(""+(10000..99999).random()) }
        val command = UpdateGebruikersCommand(spelers)
        val res = CommandQueue.addNewCommand(command)
        log.info(res.toString())


        val app = Javalin.create { config ->
            config.enableWebjars()
            config.addStaticFiles("/html")

        }.start(7000)

        app.get("/", VueComponent("<home></home>"))
        app.get("/speler/:id", VueComponent("<speler></speler>"))
        app.get("/toepking", VueComponent("<admin></admin>"))
        app.get("/api/speldata", { this.getSpeldata(it) })
        app.post("/api/load", { this.loadData(it) })
        app.post("/api/save", { this.saveData(it) })
        app.post("/api/maaktafels", { this.maakTafels(it) })
        app.post("/api/speelkaart/:id", { this.speelkaart(it) })
        app.post("/api/pakslag/:id", { this.pakSlag(it) })
        app.post("/api/toep/:id", { this.toep(it) })
        app.post("/api/gamee/:id", { this.gaMee(it) })
        app.post("/api/pas/:id", { this.pas(it) })

    }

    private fun loadData(ctx: Context) {
        log.info("load")
        ctx.json(CommandQueue.addNewCommand(LoadDataCommand()))
    }

    private fun saveData(ctx: Context) {
        log.info("save")
        val updatedSpelData = ctx.body<SpelData>()
        val command = UpdateGebruikersCommand(updatedSpelData.alleSpelers)
        val res = CommandQueue.addNewCommand(command)
        if (res.status==CommandStatus.FAILED){
            ctx.json(res)
            return
        }
        ctx.json(CommandQueue.addNewCommand(SaveDataCommand()))
    }

    private fun maakTafels(ctx: Context) {
        log.info("maakTafels")
        val aantalTafels = ctx.body<Int>()
        ctx.json(CommandQueue.addNewCommand(MaakNieuweTafelsCommand(aantalTafels)))
    }

    private fun speelkaart(ctx: Context) {
        log.info("speelkaart")
        val id = ctx.pathParam("id")
        val kaart = ctx.body<Kaart>()
        log.info("id="+id)
        log.info("kaart="+kaart)
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(SpeelKaartCommand(speler, kaart)))
    }

    private fun pakSlag(ctx: Context) {
        log.info("pak slag")
        val id = ctx.pathParam("id")
        log.info("id="+id)
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(PakSlagCommand(speler)))
    }

    private fun toep(ctx: Context) {
        log.info("toep")
        val id = ctx.pathParam("id")
        log.info("id="+id)
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(ToepCommand(speler)))
    }

    private fun pas(ctx: Context) {
        log.info("pas")
        val id = ctx.pathParam("id")
        log.info("id="+id)
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(PasCommand(speler)))
    }

    private fun gaMee(ctx: Context) {
        log.info("ga mee")
        val id = ctx.pathParam("id")
        log.info("id="+id)
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(GaMeeMetToepCommand(speler)))
    }

    private fun getSpeldata(ctx: Context) {
        ctx.result(CommandQueue.lastSpelDataJson)
        }

    fun maakInitieleSpeler(id: String): Speler {
        val speler = Speler()
        speler.id = id
        return speler
    }


}
