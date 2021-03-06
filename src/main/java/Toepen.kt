import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.plugin.rendering.vue.VueComponent
import io.javalin.websocket.WsConnectContext
import io.javalin.websocket.WsHandler
import model.*
import model.WinnaarType.*
import org.slf4j.LoggerFactory
import spellogica.AdminService
import spelprocessor.*
import java.util.concurrent.ConcurrentHashMap


object Toepen {
  private val objectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  private val log = LoggerFactory.getLogger("Toepen")
  private val userUsernameMap: MutableMap<WsConnectContext, String> = ConcurrentHashMap()// dit kan ook een list zijn!
  private val winnaarUsernameMap: MutableMap<WsConnectContext, String> = ConcurrentHashMap()// dit kan ook een list zijn!
  private var nextUserNumber = 1 // Assign to username for next connecting user


  @JvmStatic
  fun main(args: Array<String>) {
    CommandQueue.processCommands()
    loadInitialPlayers()
    CommandQueue.initLogfile()
    CommandQueue.addNewCommand(SetRandomSeed(0)) // vaste seed, zodat de tests voorspelbaar zijn
    Monkey.start()
    AutoPlay.start()
    configRest()
  }

  private fun configRest() {
    val app = configJavalin()
    // config vue components
    app.get("/", VueComponent("<home></home>"))
    app.get("/speler/:id", VueComponent("<speler></speler>"))
    app.get("/toepking", VueComponent("<admin></admin>"))
    app.get("/toepkingspelers", VueComponent("<spelers></spelers>"))
    app.get("/overzicht", VueComponent("<overzicht></overzicht>"))

    // config rest call's
    app.get("/api/speldata") { this.getSpeldata(it) }
    app.post("/api/load") { this.loadData(it) }
    app.post("/api/save", this::saveData)
    app.post("/api/savesettings") { this.saveSettings(it) }
    app.post("/api/maaktafels/:aantaltafels/:startscore") { this.maakTafels(it) }
    app.post("/api/speelkaart/:id") { this.speelkaart(it) }
    app.post("/api/pakslag/:id") { this.pakSlag(it) }
    app.post("/api/toep/:id") { this.toep(it) }
    app.post("/api/gamee/:id") { this.gaMee(it) }
    app.post("/api/pas/:id") { this.pas(it) }
    app.post("/api/pauzeer/:tafelnr") { this.pauzeer(it) }
    app.post("/api/gadoor/:tafelnr") { this.gadoor(it) }
    app.post("/api/nieuwspel/:tafelnr/:lucifers") { this.nieuwSpel(it) }
    app.post("/api/allespauzeren") { this.allesPauzeren(it) }
    app.post("/api/allesstarten") { this.allesStarten(it) }
    app.post("/api/clearlog") { this.clearlog(it) }
    app.post("/api/resetscore") { this.resetScore(it) }

    // config websockets
    app.ws("/game") { ws: WsHandler ->
      ws.onConnect { ctx: WsConnectContext ->
        val username = "User" + nextUserNumber++
        userUsernameMap.put(ctx, username)
      }
    }
    app.ws("/winnaar") { ws: WsHandler ->
      ws.onConnect { ctx: WsConnectContext ->
        val username = "User" + nextUserNumber++
        winnaarUsernameMap.put(ctx, username)
      }
    }
  }

  private fun configJavalin(): Javalin {
    return Javalin.create { config ->
      config.enableWebjars()
      config.addStaticFiles("/html")
    }.start(7000)
  }

  private fun loadInitialPlayers() {
    try {
      CommandQueue.lastSpelData = AdminService.loadData()
    } catch (e: Exception) {
      val spelers = listOf(
        maakInitieleGebruiker("27331", "Robbert"),
        maakInitieleGebruiker("80785", "Bol"),
        maakInitieleGebruiker("35113", "Bob"),
        maakInitieleGebruiker("21682", "Dimi"),
        maakInitieleGebruiker("18158", "Ferry"),
        maakInitieleGebruiker("23912", "JeroenA"),
        maakInitieleGebruiker("58137", "Goof"),
        maakInitieleGebruiker("71976", "JGwoud"),
        maakInitieleGebruiker("50601", "Berg"),
        maakInitieleGebruiker("92718", "Joost"),
        maakInitieleGebruiker("87079", "Schaap"),
        maakInitieleGebruiker("85365", "Marco"),
        maakInitieleGebruiker("12505", "Martin"),
        maakInitieleGebruiker("97570", "Mike"),
        maakInitieleGebruiker("72780", "Niels"),
        maakInitieleGebruiker("14174", "Peter"),
        maakInitieleGebruiker("78867", "Erik"),
        maakInitieleGebruiker("175867", "SpelerX"),
        maakInitieleGebruiker("288567", "SpelerY"),
        maakInitieleGebruiker("488367", "SpelerZ")
      )
      val command = UpdateGebruikersCommand(spelers)
      val res = CommandQueue.addNewCommand(command)
      log.info(res.toString())

    }
  }

  fun broadcastMessage() {
    userUsernameMap.keys.stream().filter { it.session.isOpen }.forEach { session: WsConnectContext ->
      session.send(objectMapper.writeValueAsString(CommandQueue.lastSpelData))
    }
  }

  fun broadcastSpelWinnaar(tafel: Tafel) {
    winnaarUsernameMap.keys.stream().filter { it.session.isOpen }.forEach { session: WsConnectContext ->
      session.send(Winnaar(SPEL, tafel.tafelNr, tafel.findSlagWinnaar(CommandQueue.lastSpelData)?.naam ?: "?"))
    }
  }

  fun broadcastRondeWinnaar(tafel: Tafel) {
    winnaarUsernameMap.keys.stream().filter { it.session.isOpen }.forEach { session: WsConnectContext ->
      session.send(Winnaar(RONDE, tafel.tafelNr, tafel.findSlagWinnaar(CommandQueue.lastSpelData)?.naam ?: "?"))
    }
  }

  fun broadcastSlagWinnaar(tafel: Tafel) {
    winnaarUsernameMap.keys.stream().filter { it.session.isOpen }.forEach { session: WsConnectContext ->
      session.send(Winnaar(SLAG, tafel.tafelNr, tafel.findSlagWinnaar(CommandQueue.lastSpelData)?.naam ?: "?"))
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
    if (res.status == CommandStatus.FAILED) {
      ctx.json(res)
      return
    }
    ctx.json(CommandQueue.addNewCommand(SaveDataCommand()))
    broadcastMessage()
  }

  private fun saveSettings(ctx: Context) {
    val updatedSpelData = ctx.body<SpelData>()
    val spelData = CommandQueue.lastSpelData.copy(
      automatischNieuweTafels = updatedSpelData.automatischNieuweTafels,
      nieuweTafelAutoPause = updatedSpelData.nieuweTafelAutoPause,
      aantalAutomatischeNieuweTafels = updatedSpelData.aantalAutomatischeNieuweTafels,
      aantalFishesNieuweTafels = updatedSpelData.aantalFishesNieuweTafels,
      monkeyDelayMsec = updatedSpelData.monkeyDelayMsec
    )
    CommandQueue.lastSpelData = spelData
    ctx.json(CommandQueue.addNewCommand(SaveDataCommand()))

  }

  private fun clearlog(ctx: Context) {
    ctx.json(CommandQueue.addNewCommand(ClearLog()))
    broadcastMessage()
  }

  private fun resetScore(ctx: Context) {
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
    val tafelNr = ctx.pathParam("tafelnr").toInt()
    ctx.json(CommandQueue.addNewCommand(PauzeerTafel(tafelNr)))
    broadcastMessage()
  }

  private fun gadoor(ctx: Context) {
    val tafelNr = ctx.pathParam("tafelnr").toInt()
    ctx.json(CommandQueue.addNewCommand(StartTafel(tafelNr)))
    broadcastMessage()
  }

  private fun nieuwSpel(ctx: Context) {
    val tafelNr = ctx.pathParam("tafelnr").toInt()
    val lucifers = ctx.pathParam("lucifers").toInt()
    ctx.json(CommandQueue.addNewCommand(NieuwSpel(lucifers, tafelNr)))
    broadcastMessage()
  }

  private fun speelkaart(ctx: Context) {
    val id = ctx.pathParam("id")
    val kaart = ctx.body<Kaart>()
    ctx.json(CommandQueue.addNewCommand(SpeelKaartCommand(id, kaart)))
    broadcastMessage()
  }

  private fun pakSlag(ctx: Context) {
    val id = ctx.pathParam("id")
    ctx.json(CommandQueue.addNewCommand(PakSlagCommand(id)))
    broadcastMessage()
  }

  private fun toep(ctx: Context) {
    val id = ctx.pathParam("id")
    ctx.json(CommandQueue.addNewCommand(ToepCommand(id)))
    broadcastMessage()
  }

  private fun pas(ctx: Context) {
    val id = ctx.pathParam("id")
    ctx.json(CommandQueue.addNewCommand(PasCommand(id)))
    broadcastMessage()
  }

  private fun gaMee(ctx: Context) {
    val id = ctx.pathParam("id")
    ctx.json(CommandQueue.addNewCommand(GaMeeMetToepCommand(id)))
    broadcastMessage()
  }

  private fun getSpeldata(ctx: Context) {
    ctx.result(objectMapper.writeValueAsString(CommandQueue.lastSpelData))
  }

  private fun maakInitieleGebruiker(id: String, naam: String): Gebruiker =
    Gebruiker(
      id = id,
      naam = naam,
      isMonkey = true,
      wilMeedoen = true
    )

}
