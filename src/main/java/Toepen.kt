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
    private val userUsernameMap: MutableMap<WsConnectContext, String> = ConcurrentHashMap()
    private var nextUserNumber = 1 // Assign to username for next connecting user


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

        app.ws("/chat") { ws: WsHandler ->
            ws.onConnect { ctx: WsConnectContext ->
                val username = "User" + nextUserNumber++
                userUsernameMap.put(ctx, username)
                broadcastMessage()
            }
            ws.onClose { ctx: WsCloseContext ->
//                val username: String? = userUsernameMap.get(ctx)
//                userUsernameMap.remove(ctx)
                broadcastMessage()
            }
            ws.onMessage { ctx: WsMessageContext -> broadcastMessage() }
        }

    }

    private fun broadcastMessage() {
        userUsernameMap.keys.stream().filter { it.session.isOpen() }.forEach { session: WsConnectContext ->
            session.send(CommandQueue.lastSpelDataJson)
        }
    }

    private fun loadData(ctx: Context) {
        log.info("load")
        ctx.json(CommandQueue.addNewCommand(LoadDataCommand()))
        broadcastMessage()
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
        broadcastMessage()
    }

    private fun maakTafels(ctx: Context) {
        log.info("maakTafels")
        val aantalTafels = ctx.body<Int>()
        ctx.json(CommandQueue.addNewCommand(MaakNieuweTafelsCommand(aantalTafels)))
        broadcastMessage()
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
        broadcastMessage()
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
        broadcastMessage()
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
        broadcastMessage()
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
        broadcastMessage()
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
        broadcastMessage()
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
