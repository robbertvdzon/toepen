import WINNAAR_TYPE.*
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.plugin.rendering.vue.VueComponent
import io.javalin.websocket.WsCloseContext
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsHandler
import io.javalin.websocket.WsMessageContext
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap


object Toepen {
    private val log = LoggerFactory.getLogger("Toepen")
    private val userUsernameMap: MutableMap<WsConnectContext, String> = ConcurrentHashMap()// dit kan ook een list zijn!
    private val winnaarUsernameMap: MutableMap<WsConnectContext, String> = ConcurrentHashMap()// dit kan ook een list zijn!
    private var nextUserNumber = 1 // Assign to username for next connecting user


    @JvmStatic
    fun main(args: Array<String>) {
        CommandQueue.processCommands()
        Administrator.loadData()
        Monkey.start()

//        val spelers = (1..40).map { maakInitieleSpeler(""+(10000..99999).random()) }
//        val command = UpdateGebruikersCommand(spelers)
//        val res = CommandQueue.addNewCommand(command)
//        log.info(res.toString())


        val app = Javalin.create { config ->
            config.enableWebjars()
            config.addStaticFiles("/html")

        }.start(7000)

        app.get("/", VueComponent("<home></home>"))
        app.get("/speler/:id", VueComponent("<speler></speler>"))
        app.get("/toepking", VueComponent("<admin></admin>"))
        app.get("/toepkingspelers", VueComponent("<spelers></spelers>"))
        app.get("/overzicht", VueComponent("<overzicht></overzicht>"))
        app.get("/api/speldata", { this.getSpeldata(it) })
        app.post("/api/load", { this.loadData(it) })
        app.post("/api/save", { this.saveData(it) })
        app.post("/api/maaktafels/:aantaltafels/:startscore", { this.maakTafels(it) })
        app.post("/api/speelkaart/:id", { this.speelkaart(it) })
        app.post("/api/pakslag/:id", { this.pakSlag(it) })
        app.post("/api/toep/:id", { this.toep(it) })
        app.post("/api/gamee/:id", { this.gaMee(it) })
        app.post("/api/pas/:id", { this.pas(it) })
        app.post("/api/pauzeer/:tafelnr", { this.pauzeer(it) })
        app.post("/api/gadoor/:tafelnr", { this.gadoor(it) })
        app.post("/api/nieuwspel/:tafelnr/:lucifers", { this.nieuwSpel(it) })
        app.post("/api/schop/:tafelnr", { this.schopTafelAan(it) })
        app.post("/api/dump/:tafelnr", { this.dump(it) })
        app.post("/api/allespauzeren", { this.allesPauzeren(it) })
        app.post("/api/allesstarten", { this.allesStarten(it) })
        app.post("/api/clearlog", { this.clearlog(it) })
        app.post("/api/resetscore", { this.resetScore(it) })

        app.ws("/game") { ws: WsHandler ->
            ws.onConnect { ctx: WsConnectContext ->
                val username = "User" + nextUserNumber++
                userUsernameMap.put(ctx, username)
            }
            ws.onClose { ctx: WsCloseContext -> }
            ws.onMessage { ctx: WsMessageContext ->  }
        }
        app.ws("/winnaar") { ws: WsHandler ->
            ws.onConnect { ctx: WsConnectContext ->
                val username = "User" + nextUserNumber++
                winnaarUsernameMap.put(ctx, username)
            }
            ws.onClose { ctx: WsCloseContext ->
            }
            ws.onMessage { ctx: WsMessageContext ->  }
        }
    }

    fun broadcastMessage() {
        userUsernameMap.keys.stream().filter { it.session.isOpen() }.forEach { session: WsConnectContext ->
            session.send(CommandQueue.getLastSpeldataJson())
        }
    }

    fun broadcastSpelWinnaar(tafel: Tafel) {
        winnaarUsernameMap.keys.stream().filter { it.session.isOpen() }.forEach { session: WsConnectContext ->
            session.send(Winnaar(SPEL, tafel.tafelNr, tafel.slagWinnaar?.naam?:"?"))
        }
    }
    fun broadcastRondeWinnaar(tafel: Tafel) {
        winnaarUsernameMap.keys.stream().filter { it.session.isOpen() }.forEach { session: WsConnectContext ->
            session.send(Winnaar(RONDE, tafel.tafelNr, tafel.slagWinnaar?.naam?:"?"))
        }
    }
    fun broadcastSlagWinnaar(tafel: Tafel) {
        winnaarUsernameMap.keys.stream().filter { it.session.isOpen() }.forEach { session: WsConnectContext ->
            session.send(Winnaar(SLAG, tafel.tafelNr, tafel.slagWinnaar?.naam?:"?"))
        }
    }


    private fun loadData(ctx: Context) {
        ctx.json(CommandQueue.addNewCommand(LoadDataCommand()))
        broadcastMessage()
    }

    private fun saveData(ctx: Context) {
        val updatedSpelData = ctx.body<SpelData>()
        val command = UpdateGebruikersCommand(updatedSpelData.alleSpelers)
        val res = CommandQueue.addNewCommand(command)
        if (res.status==CommandStatus.FAILED){
            ctx.json(res)
            return
        }
        ctx.json(CommandQueue.addNewCommand(SaveDataCommand()))
        broadcastMessage()
    }

    private fun clearlog(ctx: Context){
        ctx.json(CommandQueue.addNewCommand(ClearLog()))
        broadcastMessage()
    }

    private fun resetScore(ctx: Context){
        ctx.json(CommandQueue.addNewCommand(ResetScore()))
        broadcastMessage()
    }

    private fun allesPauzeren(ctx: Context) {
        ctx.json(CommandQueue.addNewCommand(AllesPauzeren()))
        broadcastMessage()
    }

    private fun allesStarten(ctx: Context) {
        ctx.json(CommandQueue.addNewCommand(AllesStarten()))
        broadcastMessage()
    }

    private fun maakTafels(ctx: Context) {
        val aantaltafels = ctx.pathParam("aantaltafels").toInt()
        val startscore = ctx.pathParam("startscore").toInt()
        ctx.json(CommandQueue.addNewCommand(MaakNieuweTafelsCommand(aantaltafels, startscore)))
        broadcastMessage()
    }

    private fun pauzeer(ctx: Context) {
        val id = ctx.pathParam("tafelnr")
        val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr.toString() == id }
        ctx.json(CommandQueue.addNewCommand(PauzeerTafel(tafel)))
        broadcastMessage()
    }

    private fun gadoor(ctx: Context) {
        val id = ctx.pathParam("tafelnr")
        val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr.toString() == id }
        ctx.json(CommandQueue.addNewCommand(StartTafel(tafel)))
        broadcastMessage()
    }

    private fun nieuwSpel(ctx: Context) {
        val id = ctx.pathParam("tafelnr")
        val lucifers = ctx.pathParam("lucifers").toInt()
        val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr.toString() == id }
        ctx.json(CommandQueue.addNewCommand(NieuwSpel(lucifers, tafel)))
        broadcastMessage()
    }

    private fun schopTafelAan(ctx: Context) {
        val id = ctx.pathParam("tafelnr")
        val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr.toString() == id }
        ctx.json(CommandQueue.addNewCommand(SchopTafel(tafel)))
        broadcastMessage()
    }

    private fun dump(ctx: Context) {
        val id = ctx.pathParam("tafelnr")
        val tafel = SpelContext.spelData.tafels.firstOrNull { it.tafelNr.toString() == id }
        tafel?.log?.forEach{
            println(it)
        }

    }

    private fun speelkaart(ctx: Context) {
        val id = ctx.pathParam("id")
        val kaart = ctx.body<Kaart>()
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(SpeelKaartCommand(speler, kaart)))
        broadcastMessage()
    }

    private fun pakSlag(ctx: Context) {
        val id = ctx.pathParam("id")
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(PakSlagCommand(speler)))
        broadcastMessage()
    }

    private fun toep(ctx: Context) {
        val id = ctx.pathParam("id")
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(ToepCommand(speler)))
        broadcastMessage()
    }

    private fun pas(ctx: Context) {
        val id = ctx.pathParam("id")
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(PasCommand(speler)))
        broadcastMessage()
    }

    private fun gaMee(ctx: Context) {
        val id = ctx.pathParam("id")
        val speler = SpelContext.spelData.alleSpelers.firstOrNull { it.id == id }
        if (speler==null){
            ctx.json(CommandResult(CommandStatus.FAILED,"Speler niet gevonden"))
            return
        }
        ctx.json(CommandQueue.addNewCommand(GaMeeMetToepCommand(speler)))
        broadcastMessage()
    }

    private fun getSpeldata(ctx: Context) {
        ctx.result(CommandQueue.getLastSpeldataJson())
        }

    fun maakInitieleSpeler(id: String): Speler {
        val speler = Speler()
        speler.id = id
        return speler
    }



}
