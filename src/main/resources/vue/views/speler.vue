<template id="speler">
        <div id="content">
            <header>
                <img src="/cards.png" height="100px">
                <div class="headerdiv">
                    Vriendjes Toep Toernooi
                    <table>
                        <tr>
                            <td v-if="mytafel">
                                tafel {{mytafel.tafelNr}}, Speler {{myspeler.naam}}
                            </td>
                        </tr>
                    </table>
                </div>
            </header>

            <div v-if="mytafel">
                <table>
                    <tr>
                        <td v-for="speler in mytafel.spelers" width="250px">

                            <div style="z-index: 1; position: relative; height: 70px">
                                <div style="z-index: 1; position: absolute; top: 20px; left: 0px;">
                                    <span>
                                    <b v-if="isAanZet(speler)" class="naamAanBeurt"><u>{{speler.naam}}</u></b>
                                    <b v-if="!isAanZet(speler)" class="naamNietBeurt">{{speler.naam}}</b>
                                    <img src="/monkey.png" height="40px" v-if="isMonkey(speler)">
                                    </span>

                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;" v-if="getoept(speler)">
                                    <img src="/getoept.png" height="50px">
                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;" v-if="gaatMee(speler)">
                                    <img src="/gaatmee.png" height="50px">
                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;" v-if="gepast(speler)">
                                    <img src="/gepast.png" height="50px">
                                </div>
                                <div style="z-index: 2; position: absolute; top: 0px; left: 0px;" v-if="isAf(speler)">
                                    <img src="/af.png" height="50px">
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr style="height: 20px">
                        <td v-for="speler in mytafel.spelers">
                            <img src="/fiche.png" height="15px" v-for="lucifer in numToArrayRij1(speler.totaalLucifers-speler.ingezetteLucifers)">
                        </td>
                    </tr>
                    <tr style="height: 20px">
                        <td v-for="speler in mytafel.spelers">
                            <img src="/fiche.png" height="15px" v-for="lucifer in numToArrayRij2(speler.totaalLucifers-speler.ingezetteLucifers)">
                           {{overigeLucifers(speler.totaalLucifers-speler.ingezetteLucifers)}}
                        </td>
                    </tr>
                    <tr style="height: 10px">
                    </tr>
                    <tr style="height: 20px">
                        <td v-for="speler in mytafel.spelers">
                            <img src="/fiche.png" height="30px"
                                 v-for="lucifer in numToArray(speler.ingezetteLucifers)">
                        </td>
                    </tr>
                    <tr height="100px">
                        <td v-for="speler in mytafel.spelers">
                            <div class="kaartenred">{{getRodeKaart(speler.gespeeldeKaart)}}</div>
                            <div class="kaartenblack">{{getZwarteKaart(speler.gespeeldeKaart)}}</div>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="winnaarBalk" v-if="getWinnaarStatus()!=null">
                {{getWinnaarStatus()}}
            </div>


            <div  v-if="mytafel">
                <div class="statusbalkGlow" v-if="zelfAanZet()" >
                    {{getTafelStatus()}}
                </div>
                <div class="statusbalk" v-else>
                    {{getTafelStatus()}}
                </div>
            </div>
            <div class="errorBalk" v-if="error">
                {{error}}
            </div>

            <div>
                <div>
                    <table>
                        <tr>
                            <td>
                                <img src="/toep.png" height="60px" v-on:click="toep">
                                <img v-if="slagGewonnen()" src="/pakslag.png" height="60px" v-on:click="pakSlag">
                                <img v-if="toepKeuzeDoorgeven()" src="/gamee.png" height="60px" v-on:click="gaMee">
                                <img v-if="toepKeuzeDoorgeven()" src="/pas.png" height="60px" v-on:click="pas">
                            </td>
                            <td v-for="(kaart, idx) in myspeler.kaarten" width="150px">
                                <div class="kaartenred" v-on:click="speelKaart(idx)">{{getRodeKaart(kaart)}}</div>
                                <div class="kaartenblack" v-on:click="speelKaart(idx)">{{getZwarteKaart(kaart)}}</div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
            <div style="height: 200px">
            </div>

        </div>
</template>

<script>

    Vue.component("speler", {
        template: "#speler",
        data: () => ({
            speldata: null,
            mytafel: null,
            myspeler: null,
            rondewinnaar: null,
            slagwinnaar: null,
            tafelwinnaar: null,
            id: null,
            error: null
        }),
        created() {
            this.load()
        },

        methods: {
            loadedDataWinnaar(json) {
                var tafelNr = json.tafelNr;
                var winnaar = json.winnaar;
                var winnaarType = json.winnaarType;
                if (this.mytafel.tafelNr==tafelNr && winnaarType=="SLAG"){
                    this.slagwinnaar = winnaar+" pakt de slag";
                    setTimeout(()=>{
                        this.slagwinnaar = null
                    },1500);
                }
                if (this.mytafel.tafelNr==tafelNr && winnaarType=="RONDE"){
                    this.rondewinnaar = winnaar+" heeft deze ronde gewonnen";
                    setTimeout(()=>{
                        this.rondeWinnaarDeel2()
                    },2000);
                }
                if (this.mytafel.tafelNr==tafelNr && winnaarType=="SPEL"){
                    this.tafelwinnaar = winnaar+" heeft dit spel gewonnen";
                    setTimeout(()=>{
                        this.tafelwinnaar = null
                    },2000);
                }
            },
            rondeWinnaarDeel2: function () {
                this.rondewinnaar = "Een nieuwe ronde is gestart";
                setTimeout(()=>{
                    this.rondewinnaar = null
                },4000);
            },
            loadedData(json) {
                this.speldata = json;
                this.myspeler = null;
                this.mytafel = null;

                var tafelCount = this.speldata.tafels.length;
                for (var i = 0; i < tafelCount; i++) {
                    tafel = this.speldata.tafels[i];
                    var spelerCount = tafel.spelers.length;
                    for (var j = 0; j < spelerCount; j++) {
                        speler = tafel.spelers[j];
                        if (speler.id == this.id) {
                            this.myspeler = speler;
                            this.mytafel = tafel;
                        }
                    }
                }
                if (this.myspeler == null) {
                    this.error = "Geen tafel gevonden";
                }
            },
            load: function (event) {
                this.id = this.$javalin.pathParams["id"];
                // initieel de gegevens ophalen
                fetch(`/api/speldata`)
                    .then(res => res.json())
                    .then(json =>
                        this.loadedData(json)
                    )
                    .catch(() => alert("Error while fetching opdracht"));

                // gebruik websockets voor de volgende updates
                let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/game");
                ws.onmessage = msg => this.loadedData(JSON.parse(msg.data));
                ws.onclose = () => location.reload();

                let wsWinnaar = new WebSocket("ws://" + location.hostname + ":" + location.port + "/winnaar");
                wsWinnaar.onmessage = msg => this.loadedDataWinnaar(JSON.parse(msg.data));
                wsWinnaar.onclose = () => location.reload();

            },
            checkresult: function (res) {
                if (res.data.status == "FAILED") {
                    this.error = res.data.errorMessage;
                    setTimeout(()=>{
                        this.clearError()
                    },1500);
                }
            },
            clearError: function () {
               this.error = null;
            },
            speelKaart: function (kaart) {
                axios.post(`/api/speelkaart/` + this.id, this.myspeler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            pakSlag: function (kaart) {
                axios.post(`/api/pakslag/` + this.id, this.myspeler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            pas: function (kaart) {
                axios.post(`/api/pas/` + this.id, this.myspeler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            toep: function (kaart) {
                axios.post(`/api/toep/` + this.id, this.myspeler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            gaMee: function (kaart) {
                axios.post(`/api/gamee/` + this.id, this.myspeler.kaarten[kaart])
                    .then(res => this.checkresult(res))
            },
            getWaarde: function (kaart) {
                if (kaart == null) return;
                if (kaart.waarde == "7") return "B";
                if (kaart.waarde == "8") return "V";
                if (kaart.waarde == "9") return "H";
                if (kaart.waarde == "10") return "A";
                if (kaart.waarde == "11") return "7";
                if (kaart.waarde == "12") return "8";
                if (kaart.waarde == "13") return "9";
                if (kaart.waarde == "14") return "10";
                return "?";
            },
            getRodeKaart: function (kaart) {
                if (kaart == null) return;
                if (kaart.symbool == "KLAVER") return;
                if (kaart.symbool == "SCHOPPEN") return;
                if (kaart.symbool == "HARTEN") symbool = "♥"
                if (kaart.symbool == "RUITEN") symbool = "♦"
                return symbool + this.getWaarde(kaart);
            },
            getZwarteKaart: function (kaart) {
                if (kaart == null) return;
                if (kaart.symbool == "HARTEN") return;
                if (kaart.symbool == "RUITEN") return;
                if (kaart.symbool == "KLAVER") symbool = "♣"
                if (kaart.symbool == "SCHOPPEN") symbool = "♠"
                return symbool + this.getWaarde(kaart);
            },
            isAanZet: function (speler) {
                return this.mytafel.huidigeSpeler != null && (speler.naam == this.mytafel.huidigeSpeler.naam);

            },
            zelfAanZet: function () {
                return (this.mytafel.huidigeSpeler) != null && (this.myspeler.naam == this.mytafel.huidigeSpeler.naam);
            },
            isMonkey: function (speler) {
                return speler.isMonkey;
            },
            getStatus: function (speler) {
                if (!speler.actiefInSpel) return "Af";
                if (speler.gepast) return "Gepast";
                if (speler.toepKeuze == "GEEN_KEUZE") return "";
                if (speler.toepKeuze == "NVT") return "";
                if (speler.toepKeuze == "PAS") return "Pas";
                if (speler.toepKeuze == "MEE") return "Ga mee";
                if (speler.toepKeuze == "TOEP") return "Toep!";
                return ""
            },
            getWinnaarStatus: function (){
                if (this.tafelwinnaar!=null ) {
                    return this.tafelwinnaar;
                }
                if (this.rondewinnaar!=null ) {
                    return this.rondewinnaar;
                }
                return null;
            },
            getTafelStatus: function () {
                if (this.mytafel.gepauzeerd) {
                    return "Spel is gepauzeerd!";
                }
                if (this.mytafel.tafelWinnaar!=null && this.mytafel.tafelWinnaar.naam!=null) {
                    return "Spel afgelopen, "+this.mytafel.tafelWinnaar.naam+" heeft gewonnen!";
                }
                if (this.slagwinnaar!=null){
                    return this.slagwinnaar;
                }
                if (this.mytafel.huidigeSpeler==null) {
                    return "";
                }
                if (this.mytafel.huidigeSpeler.naam!=this.myspeler.naam) {
                    return this.mytafel.huidigeSpeler.naam+" is aan de beurt";
                }
                if (this.mytafel.huidigeSpeler.naam==this.myspeler.naam) {
                    if (this.slagGewonnen()) return "Je hebt de slag gewonnen, pak de slag";
                    if (this.toepKeuzeDoorgeven()) return "Er is getoept! Ga mee, pas of toep over";
                    return "Je bent aan de beurt";
                }
                return "?";
            },
            slagGewonnen: function () {
                if (this.mytafel == null) return false;
                if (this.myspeler == null) return false;
                if (this.mytafel.slagWinnaar == null) return false;
                return (this.mytafel.slagWinnaar.naam == this.myspeler.naam)
            }
            ,
            toepKeuzeDoorgeven: function () {
                if (this.mytafel == null) return false;
                if (this.myspeler == null) return false;
                if (this.mytafel.toeper == null) return false;
                if (this.myspeler.toepKeuze != "GEEN_KEUZE") return false;
                return (this.mytafel.huidigeSpeler.naam == this.myspeler.naam)
            },
            getSlagWinnaar: function () {
                if (this.mytafel == null) return "-";
                if (this.mytafel.slagWinnaar == null) return "-";
                return this.mytafel.slagWinnaar.naam;

            },
            getSpelWinnaar: function () {
                if (this.mytafel == null) return "-";
                if (this.mytafel.tafelWinnaar == null) return "-";
                return this.mytafel.tafelWinnaar.naam;
            },
            numToArray: function (nr) {
                var elements = [];
                for (i = 0; i < nr; i++) elements.push("1");
                return elements;
            },
            numToArrayRij1: function (nr) {
                var elements = [];
                var total = nr;
                if (total>8) total = 8;
                for (i = 0; i < total; i++) elements.push("1");
                return elements;
            },
            numToArrayRij2: function (nr) {
                var elements = [];
                var total = nr-8;
                if (total>8) total = 8;
                for (i = 0; i < total; i++) elements.push("1");
                return elements;
            },
            overigeLucifers: function (nr) {
                var elements = [];
                var total = nr-16;
                if (total>0) return "+"+total;
                return ""
            },
            getoept: function (speler) {
                if (!speler.actiefInSpel) return false;
                if (speler.gepast) return false;
                return (speler.toepKeuze == "TOEP")
            },
            gaatMee: function (speler) {
                if (!speler.actiefInSpel) return false;
                if (speler.gepast) return false;
                return (speler.toepKeuze == "MEE")
            },
            gepast: function (speler) {
                if (!speler.actiefInSpel) return false;
                return speler.gepast
            },
            isAf: function (speler) {
                return !speler.actiefInSpel;
            },
        }
    });
</script>
